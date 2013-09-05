/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public abstract class TileEntityInventoriedReactorBase extends TileEntityReactorBase implements ISidedInventory {

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return ReikaInventoryHelper.decrStackSize(this, i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, i);
	}

	@Override
	public String getInvName() {
		return this.getTEName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {
		return ReikaMathLibrary.py3d(ep.posX-xCoord-0.5, ep.posY-yCoord-0.5, ep.posZ-zCoord-0.5) <= 8;
	}

	@Override
	public void openChest() {

	}

	@Override
	public void closeChest() {

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return ReikaInventoryHelper.getWholeInventoryForISided(this);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return this.isStackValidForSlot(i, itemstack);
	}

}
