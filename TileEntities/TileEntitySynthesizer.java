package Reika.ReactorCraft.TileEntities;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Piping.TileEntityPipe;

public class TileEntitySynthesizer extends TileEntityInventoriedReactorBase implements IFluidHandler {

	private static final int WATER_PER_AMMONIA = 250;
	private static final int AMMONIA_PER_STEP = 1000;

	public int timer;

	private HybridTank tank = new HybridTank("synthout", 24000);

	private HybridTank water = new HybridTank("synthwater", 24000);

	private ItemStack[] inv = new ItemStack[3];

	private StepTimer steptimer = new StepTimer(1800);

	@Override
	public int getIndex() {
		return ReactorTiles.SYNTHESIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		steptimer.setCap(200);
		this.getWaterBuckets();
		this.getRCWater(world, x, y, z);
		if (this.getWater() > 0 && this.hasAmmonium() && this.hasQuicklime() && this.canMakeAmmonia(AMMONIA_PER_STEP)) {
			steptimer.update();
			if (steptimer.checkCap())
				this.makeAmmonia();
		}
		timer = steptimer.getTick();
		//ReikaJavaLibrary.pConsole(tank);
	}

	private boolean canMakeAmmonia(int amt) {
		return tank.isEmpty() || tank.getLevel()+amt < tank.getCapacity();
	}

	private void makeAmmonia() {
		ReikaInventoryHelper.decrStack(1, inv);
		ReikaInventoryHelper.decrStack(2, inv);
		water.removeLiquid(WATER_PER_AMMONIA);
		tank.addLiquid(AMMONIA_PER_STEP, FluidRegistry.getFluid("ammonia"));
	}

	private boolean hasQuicklime() {
		if (inv[1] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[1], ReactorStacks.lime))
			return true;
		ArrayList<ItemStack> lime = OreDictionary.getOres("dustQuicklime");
		return ReikaItemHelper.listContainsItemStack(lime, inv[1]);
	}

	private boolean hasAmmonium() {
		if (inv[2] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[2], ReactorStacks.ammonium))
			return true;
		ArrayList<ItemStack> dust = OreDictionary.getOres("dustAmmonium");
		return ReikaItemHelper.listContainsItemStack(dust, inv[2]);
	}

	private int getWater() {
		return water.getLevel();
	}

	public int getWaterScaled(int px) {
		return this.getWater()*px/water.getCapacity();
	}

	public int getAmmoniaScaled(int px) {
		return tank.getLevel() * px / tank.getCapacity();
	}

	public int getTimerScaled(int px) {
		return steptimer.getTick() * px / steptimer.getCap();
	}

	private void getRCWater(World world, int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (water.getLevel() < water.getCapacity()) {
				MachineRegistry m = MachineRegistry.getMachine(world, dx, dy, dz);
				if (m == MachineRegistry.PIPE) {
					TileEntityPipe tile = (TileEntityPipe)world.getBlockTileEntity(dx, dy, dz);
					if (tile != null) {
						if (tile.liquidID == 9 && tile.liquidLevel > 0) {
							int oldLevel = tile.liquidLevel;
							tile.liquidLevel = ReikaMathLibrary.extrema(tile.liquidLevel-tile.liquidLevel/4, 0, "max");
							water.addLiquid(oldLevel/4, FluidRegistry.WATER);
						}
					}
				}
			}
		}
	}

	private void getWaterBuckets() {
		if (inv[0] != null && inv[0].itemID == Item.bucketWater.itemID && this.canAcceptMoreWater(FluidContainerRegistry.BUCKET_VOLUME)) {
			water.fill(FluidRegistry.getFluidStack("water", FluidContainerRegistry.BUCKET_VOLUME), true);
			inv[0] = new ItemStack(Item.bucketEmpty);
		}
	}

	public boolean canAcceptMoreWater(int amt) {
		return water.getFluid() == null || water.getFluid().amount+amt <= water.getCapacity();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!this.canDrain(from, resource.getFluid()))
			return null;
		int maxDrain = resource.amount;
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.getFluid("ammonia"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.WATER);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{water.getInfo(), tank.getInfo()};
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return itemstack.itemID == Item.bucketEmpty.itemID;
	}

	@Override
	public int getSizeInventory() {
		return 3;
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
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (i == 0)
			return is.itemID == Item.bucketWater.itemID;
		if (i == 1)
			return ReikaItemHelper.matchStacks(is, ReactorStacks.lime);
		if (i == 2)
			return ReikaItemHelper.matchStacks(is, ReactorStacks.ammonium);
		return false;
	}



	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		timer = NBT.getInteger("time");

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

		water.readFromNBT(NBT);
		tank.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("time", timer);

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

		water.writeToNBT(NBT);
		tank.writeToNBT(NBT);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

}
