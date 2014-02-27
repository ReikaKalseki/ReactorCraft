/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaTimeHelper;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;

public abstract class TileEntityWasteUnit extends TileEntityInventoriedReactorBase {

	protected ItemStack[] inv = new ItemStack[this.getSizeInventory()];

	protected void fill() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (this.getStackInSlot(i) == null) {
				ItemStack is = WasteManager.getFullyRandomWasteItem();
				this.setInventorySlotContents(i, is);
			}
		}
	}

	public abstract boolean leaksRadiation();

	public abstract boolean isValidIsotope(Isotopes i);

	protected void decayWaste() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null && inv[i].itemID == ReactorItems.WASTE.getShiftedItemID() && inv[i].stackTagCompound != null) {
				Isotopes atom = Isotopes.getIsotope(inv[i].getItemDamage());
				if (ReikaRandomHelper.doWithChance(0.125*ReikaNuclearHelper.getDecayChanceFromHalflife(Math.log(atom.getMCHalfLife())))) {
					//ReikaJavaLibrary.pConsole("Radiating from "+atom);
					if (this.leaksRadiation())
						this.leakRadiation(worldObj, xCoord, yCoord, zCoord);
				}
				//ReikaJavaLibrary.pConsole(ReikaNuclearHelper.getDecayChanceFromHalflife(atom.getMCHalfLife()));
				if (ReikaNuclearHelper.shouldDecay(atom)) {
					ReikaInventoryHelper.decrStack(i, inv);
				}
			}
		}
	}

	protected void leakRadiation(World world, int x, int y, int z) {
		ForgeDirection dir = dirs[rand.nextInt(dirs.length)];
		world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, dir));
	}

	@Override
	public final ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public final void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.itemID == ReactorItems.WASTE.getShiftedItemID();
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
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
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public final boolean canEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public final boolean canExitToSide(ForgeDirection dir) {
		return true;
	}

	public final int countWaste() {
		int count = 0;
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == ReactorItems.WASTE.getShiftedItemID()) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	public final boolean hasWaste() {
		return this.countWaste() > 0;
	}

	protected final double getHalfLife(ItemStack is) {
		if (is.itemID != ReactorItems.WASTE.getShiftedItemID())
			return 0;
		return WasteManager.getWasteList().get(is.getItemDamage()).getMCHalfLife();
	}

	protected final boolean isLongLivedWaste(ItemStack is) {
		return is.itemID == ReactorItems.WASTE.getShiftedItemID() && this.getHalfLife(is) > 6*ReikaTimeHelper.YEAR.getMinecraftDuration();
	}
}
