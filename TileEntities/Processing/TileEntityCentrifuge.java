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
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityCentrifuge extends TileEntityInventoriedReactorBase implements IFluidHandler, ShaftPowerReceiver, PipeConnector {

	private int torque;
	private int omega;
	private long power;
	private int iotick;

	public int split; //timer

	/** "In the range of 100000 rpm" -> 10.5k rad/s <br>
	 * http://science.howstuffworks.com/uranium-centrifuge.htm */
	//public static final int REAL_SPEED = 16384;

	public static final int MINSPEED = 262144; //much faster since doing it in one step

	private ItemStack[] inv = new ItemStack[2];

	private HybridTank tank = new HybridTank("centri", 12000);

	private StepTimer timer = new StepTimer(900);

	public static final int UF6_PER_DUST = 50;
	public static final int FUEL_CHANCE = 9;

	@Override
	public int getIndex() {
		return ReactorTiles.CENTRIFUGE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (omega >= 262144) {
			phi += 40;
		}
		else if (omega >= 65536) {
			phi += 30;
		}
		else if (omega >= 16384) {
			phi += 20;
		}
		else if (omega >= 4096) {
			phi += 15;
		}
		if (omega >= 1024) {
			phi += 10;
		}
		if (omega >= 256) {
			phi += 7;
		}
		else if (omega > 0) {
			phi += 5;
		}
		iotick -= 8;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		timer.setCap(this.setTimer());
		if (power > 0 && omega >= MINSPEED) {
			if (this.canMake()) {
				timer.update();
				if (timer.checkCap()) {
					if (!world.isRemote)
						this.make();
				}
			}
		}
		else {
			timer.reset();
		}
		if (!world.isRemote) {
			split = timer.getTick();
		}
	}

	private int setTimer() {
		if (omega >= 67108864) {
			return 8;
		}
		else if (omega >= 33554432) {
			return 20;
		}
		else if (omega >= 16777216) {
			return 50;
		}
		else if (omega >= 8388608) {
			return 100;
		}
		else if (omega >= 4194304) {
			return 240;
		}
		else if (omega >= 2097152) {
			return 400;
		}
		else if (omega >= 1048576) {
			return 600;
		}
		else if (omega >= 524288) {
			return 800;
		}
		else
			return 900;
	}

	private void make() {
		tank.drain(UF6_PER_DUST, true);
		if (ReikaRandomHelper.doWithChance(FUEL_CHANCE/100D))
			ReikaInventoryHelper.addOrSetStack(ReactorStacks.fueldust.copy(), inv, 0);
		else
			ReikaInventoryHelper.addOrSetStack(ReactorStacks.depdust.copy(), inv, 1);

		ReactorAchievements.UF6.triggerAchievement(this.getPlacer());
	}

	public boolean canMake() {
		return this.getUF6() > UF6_PER_DUST && this.hasInventorySpace();
	}

	private boolean hasInventorySpace() {
		if (inv[0] != null && inv[0].stackSize >= inv[0].getMaxStackSize())
			return false;
		if (inv[1] != null && inv[1].stackSize >= inv[1].getMaxStackSize())
			return false;
		return true;
	}

	public int getProcessingScaled(int p) {
		return (int)(p*split/(float)timer.getCap());
	}

	public int getUF6() {
		return tank.getFluid() != null ? tank.getFluid().amount : 0;
	}

	public int getUF6Scaled(int p) {
		return p*this.getUF6()/tank.getCapacity();
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 2;
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
		return ReikaItemHelper.matchStacks(itemstack, ReactorStacks.fueldust);
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
		return x == xCoord && y == yCoord-1 && z == zCoord;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		torque = omega = 0;
		power = 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && fluid.equals(ReactorCraft.UF6);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		split = NBT.getInteger("time");

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");

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

		tank.readFromNBT(NBT);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("time", split);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

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

		tank.writeToNBT(NBT);
	}

	public boolean canAcceptMoreUF6(int amt) {
		return tank.getFluid() == null || tank.getFluid().amount+amt <= tank.getCapacity();
	}

	public void addUF6(int amt) {
		tank.fill(FluidRegistry.getFluidStack("uranium hexafluoride", amt), true);
	}

	public void removeFluid(int volume) {
		tank.removeLiquid(volume);
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return dir.offsetY == 0;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side == ForgeDirection.UP;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == ForgeDirection.UP ? Flow.OUTPUT : Flow.NONE;
	}
}
