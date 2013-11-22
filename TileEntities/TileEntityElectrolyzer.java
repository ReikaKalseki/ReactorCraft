package Reika.ReactorCraft.TileEntities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.PipeConnector;
import Reika.RotaryCraft.Auxiliary.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityElectrolyzer extends TileEntityInventoriedReactorBase implements ShaftPowerReceiver, IFluidHandler, PipeConnector, TemperatureTE {

	public static final int SODIUM_MELT = 98;

	public static final int SALT_MELT = 801;

	public static final int CAPACITY = 6000;

	public static final int MAXTEMP = 1200;

	private HybridTank cltank = new HybridTank("chlorine", this.getCapacity());
	private HybridTank natank = new HybridTank("sodium", this.getCapacity());

	private ItemStack[] inv = new ItemStack[1];

	private StepTimer timer = new StepTimer(400);
	private StepTimer tempTimer = new StepTimer(20);

	private int temperature;

	private int omega;
	private int torque;
	private long power;
	private int iotick;

	public static final int MINPOWER = 1048576; //1MW

	@Override
	public int getIndex() {
		return ReactorTiles.ELECTROLYZER.ordinal();
	}

	public int getCapacity() {
		return CAPACITY;
	}

	public int getSodium() {
		return natank.getLevel();
	}

	public int getChlorine() {
		return cltank.getLevel();
	}

	public int getTime() {
		return timer.getTick();
	}

	public int getTimerScaled(int d) {
		return d * timer.getTick() / timer.getCap();
	}

	public int getChlorineScaled(int d) {
		return d * cltank.getLevel() / cltank.getCapacity();
	}

	public int getSodium(int d) {
		return d * natank.getLevel() / natank.getCapacity();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		tempTimer.update();
		if (tempTimer.checkCap())
			this.updateTemperature(world, x, y, z, meta);
		if (this.canMake()) {
			timer.update();
			if (timer.checkCap())
				this.make();
		}
		else {
			timer.reset();
		}
	}

	private boolean hasSalt() {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (this.isSalt(is))
				return true;
		}
		return false;
	}

	private boolean canMake() {
		if (cltank.isFull() || natank.isFull())
			return false;
		return power >= MINPOWER && temperature >= SALT_MELT && this.hasSalt();
	}

	private void make() {
		ReikaInventoryHelper.decrStack(0, inv);
		natank.addLiquid(100, FluidRegistry.getFluid("sodium"));
		cltank.addLiquid(100, FluidRegistry.getFluid("chlorine"));
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public int getMachineX() {
		return xCoord;
	}

	@Override
	public int getMachineY() {
		return yCoord;
	}

	@Override
	public int getMachineZ() {
		return zCoord;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side.offsetY != 0;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side.offsetY != 0 ? Flow.OUTPUT : Flow.NONE;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int maxDrain = resource.amount;
		if (from == ForgeDirection.DOWN)
			return natank.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP)
			return cltank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from == ForgeDirection.DOWN)
			return natank.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP)
			return cltank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{natank.getInfo(), cltank.getInfo()};
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (x == xCoord+dir.offsetX && x == yCoord+dir.offsetY && x == zCoord+dir.offsetZ)
				return true;
		}
		return false;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = 0;
		torque = 0;
		power = 0;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return this.isSalt(itemstack);
	}

	private boolean isSalt(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (ReikaItemHelper.matchStacks(itemstack, ItemStacks.salt))
			return true;
		List<ItemStack> li = OreDictionary.getOres("salt");
		return ReikaItemHelper.listContainsItemStack(li, itemstack);
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);

		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water);
		if (waterside != null) {
			Tamb /= 2;
		}
		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.ice.blockID);
		if (iceside != null) {
			if (Tamb > 0)
				Tamb /= 4;
			ReikaWorldHelper.changeAdjBlock(world, x, y, z, iceside, Block.waterMoving.blockID, 0);
		}
		ForgeDirection fireside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.fire.blockID);
		if (fireside != null) {
			Tamb += 200;
		}
		ForgeDirection lavaside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.lava);
		if (lavaside != null) {
			Tamb += 600;
		}
		if (temperature > Tamb)
			temperature--;
		if (temperature > Tamb*2)
			temperature--;
		if (temperature < Tamb)
			temperature++;
		if (temperature*2 < Tamb)
			temperature++;
		if (temperature > MAXTEMP)
			temperature = MAXTEMP;
		if (temperature > 100) {
			ForgeDirection side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.snow.blockID);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, 0, 0);
			side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.ice.blockID);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.waterMoving.blockID, 0);
		}
	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return 0;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {
		world.setBlock(x, y, z, 0);
		world.newExplosion(null, x+0.5, y+0.5, z+0.5, 3F, true, true);
	}

}
