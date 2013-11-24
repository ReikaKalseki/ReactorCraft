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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.CraftingItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.InertIInv;

public class TileEntityMagnet extends TileEntityInventoriedReactorBase implements ShaftPowerReceiver, InertIInv {

	private int omega;
	private int torque;
	private long power;

	private int iotick;

	private ItemStack[] inv = new ItemStack[1];

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNET.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
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
		return this.isMagnet(itemstack);
	}

	private boolean hasMagnet() {
		return inv[0] != null && this.isMagnet(inv[0]);
	}

	private boolean isMagnet(ItemStack is) {
		return ReikaItemHelper.matchStacks(CraftingItems.MAGNET.getItem(), is);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

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
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

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
		return y == yCoord+1 && x == xCoord && z == zCoord;
	}

	@Override
	public boolean isReceiving() {
		return this.hasMagnet();
	}

	@Override
	public void noInputMachine() {
		torque = omega = 0;
		power = 0;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return false;
	}

}