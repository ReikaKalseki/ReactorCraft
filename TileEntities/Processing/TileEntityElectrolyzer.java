/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.API.Shockable;
import Reika.RotaryCraft.API.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityElectrolyzer extends TileEntityInventoriedReactorBase implements ShaftPowerReceiver, IFluidHandler, PipeConnector, TemperatureTE, ThermalMachine, Shockable {

	public static final int SODIUM_MELT = 98;

	public static final int SALT_MELT = 801;

	public static final int CAPACITY = 6000;

	public static final int MAXTEMP = 1200;

	private HybridTank tankL = new HybridTank("lighttank", this.getCapacity());
	private HybridTank tankH = new HybridTank("heavytank", this.getCapacity());

	private HybridTank input = new HybridTank("input", this.getCapacity()*2);

	private ItemStack[] inv = new ItemStack[1];

	private StepTimer timer = new StepTimer(50);
	private StepTimer tempTimer = new StepTimer(20);

	public int time;

	private int temperature;

	private int omega;
	private int torque;
	private long power;
	private int iotick;

	public static final int SALTPOWER = 131072;

	@Override
	public int getIndex() {
		return ReactorTiles.ELECTROLYZER.ordinal();
	}

	public int getCapacity() {
		return CAPACITY;
	}

	public int getHLevel() {
		return tankH.getLevel();
	}

	public int getLLevel() {
		return tankL.getLevel();
	}

	public int getTime() {
		return timer.getTick();
	}

	public int getTimerScaled(int d) {
		return d * timer.getTick() / timer.getCap();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (iotick > 0)
			iotick -= 8;
		tempTimer.update();
		if (tempTimer.checkCap())
			this.updateTemperature(world, x, y, z, meta);
		if (this.canMakeSodium()) {
			if (timer.checkCap())
				this.makeSodium();
		}
		else if (this.canMakeHydrogen()) {
			if (timer.checkCap())
				this.makeHydrogen();
		}
		else {
			timer.reset();
		}

		time = timer.getTick();
		//ReikaJavaLibrary.pConsole(timer.getFraction());

		//ReikaJavaLibrary.pConsole(this.getSide()+":"+input+":"+tankH+":"+tankL);
	}

	private boolean hasSalt() {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (this.isSalt(is))
				return true;
		}
		return false;
	}

	private boolean canMakeSodium() {
		if (tankL.isFull() || tankH.isFull())
			return false;
		return power >= SALTPOWER && temperature >= SALT_MELT && this.hasSalt();
	}

	private boolean canMakeHydrogen() {
		if (tankL.isFull() || tankH.isFull())
			return false;
		return this.hasHeavyWater();
	}

	private boolean hasHeavyWater() {
		return !input.isEmpty() && input.getLevel() > 100 && input.getActualFluid().equals(FluidRegistry.getFluid("heavy water"));
	}

	private void makeSodium() {
		ReikaInventoryHelper.decrStack(0, inv);
		tankH.addLiquid(100, FluidRegistry.getFluid("rc sodium"));
		tankL.addLiquid(100, FluidRegistry.getFluid("rc chlorine"));
	}

	private void makeHydrogen() {
		input.removeLiquid(100);
		tankH.addLiquid(50, FluidRegistry.getFluid("rc oxygen"));
		tankL.addLiquid(100, this.getHydrogenIsotope());
	}

	private Fluid getHydrogenIsotope() {
		return FluidRegistry.getFluid("rc deuterium");
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
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side.offsetY != 0;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side.offsetY != 0 ? Flow.OUTPUT : Flow.INPUT;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return input.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int maxDrain = resource.amount;
		if (from == ForgeDirection.DOWN)
			return tankH.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP)
			return tankL.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from == ForgeDirection.DOWN)
			return tankH.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP)
			return tankL.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0 && fluid.equals(FluidRegistry.getFluid("heavy water"));
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tankH.getInfo(), tankL.getInfo(), input.getInfo()};
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
			if (x == xCoord+dir.offsetX && y == yCoord+dir.offsetY && z == zCoord+dir.offsetZ)
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
	public boolean canRemoveItem(int i, ItemStack itemstack) {
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
		int Tamb = ReikaBiomeHelper.getBiomeTemp(world, x, z);

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

	@Override
	public void onDischarge(int charge, double range) {
		if (this.canMakeSodium() || this.canMakeHydrogen()) {
			timer.update();
		}
	}

	@Override
	public int getMinDischarge() {
		return 4096;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 1200;
	}

	@Override
	public void onOverheat(World world, int x, int y, int z) {

	}

	@Override
	public boolean canBeFrictionHeated() {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		tankH.writeToNBT(NBT);
		tankL.writeToNBT(NBT);
		input.writeToNBT(NBT);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

		NBT.setInteger("temp", temperature);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		tankH.readFromNBT(NBT);
		tankL.readFromNBT(NBT);
		input.readFromNBT(NBT);

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");

		temperature = NBT.getInteger("temp");

		NBTTagList nbttaglist = NBT.getTagList("Items");
		inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length)
			{
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	public boolean addHeavyWater(int amt) {
		if (input.canTakeIn(amt)) {
			input.addLiquid(amt, FluidRegistry.getFluid("heavy water"));
			return true;
		}
		return false;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return false;
	}

	@Override
	public float getAimX() {
		return 0.5F;
	}

	@Override
	public float getAimY() {
		return 0.9375F;
	}

	@Override
	public float getAimZ() {
		return 0.5F;
	}

	public int getInputLevel() {
		return input.getLevel();
	}

}
