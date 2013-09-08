/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityCentrifuge extends TileEntityInventoriedReactorBase implements ITankContainer, ShaftPowerReceiver {

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

	private LiquidTank tank = new LiquidTank(12000);

	private StepTimer timer = new StepTimer(900);

	public static final int UF6_PER_DUST = 250;

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
		if (this.canMake()) {
			timer.update();
			if (timer.checkCap()) {
				if (!world.isRemote)
					this.make();
			}
		}
		if (!world.isRemote) {
			split = timer.getTick();
		}
	}

	private void make() {
		tank.drain(UF6_PER_DUST, true);
		ReikaInventoryHelper.addOrSetStack(ReactorStacks.fueldust.copy(), inv, 0);
		ReikaInventoryHelper.addOrSetStack(ReactorStacks.depdust.copy(), inv, 1);
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
		return tank.getLiquid() != null ? tank.getLiquid().amount : 0;
	}

	public int getUF6Scaled(int p) {
		return p*this.getUF6()/tank.getCapacity();
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
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
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
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
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		if (from != ForgeDirection.UP)
			return 0;
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (!resource.isLiquidEqual(LiquidDictionary.getCanonicalLiquid("Uranium Hexafluoride")))
			return 0;
		return tank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		if (direction == ForgeDirection.UP)
			return new ILiquidTank[]{tank};
		else
			return new ILiquidTank[0];
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		if (direction == ForgeDirection.UP && type.isLiquidEqual(LiquidDictionary.getCanonicalLiquid("Uranium Hexafluoride")))
			return tank;
		else
			return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		split = NBT.getInteger("time");

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");

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

		if (NBT.hasKey("internaltank")) {
			tank.setLiquid(new LiquidStack(NBT.getInteger("tankId"), NBT.getInteger("internaltank")));
		}
		else if (NBT.hasKey("tank")) {
			tank.setLiquid(LiquidStack.loadLiquidStackFromNBT(NBT.getCompoundTag("tank")));
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("time", split);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);

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

		if (tank.getLiquid() != null) {
			NBT.setTag("tank", tank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	public boolean canAcceptMoreUF6(int amt) {
		return tank.getLiquid() == null || tank.getLiquid().amount+amt <= tank.getCapacity();
	}

	public void addUF6(int amt) {
		tank.fill(LiquidDictionary.getLiquid("Uranium Hexafluoride", amt), true);
	}
}
