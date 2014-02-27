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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.Auxiliary.Interfaces.InertIInv;

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
		return 64;
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
	public final boolean canExtractItem(int slot, ItemStack is, int j) {
		return this.canRemoveItem(slot, is) && this.canExitToSide(dirs[j]);
	}

	public abstract boolean canEnterFromSide(ForgeDirection dir);

	public abstract boolean canExitToSide(ForgeDirection dir);

	public abstract boolean canRemoveItem(int slot, ItemStack is);

	@Override
	public final int[] getAccessibleSlotsFromSide(int var1) {
		if (this instanceof InertIInv)
			return new int[0];
		return ReikaInventoryHelper.getWholeInventoryForISided(this);
	}

	@Override
	public final boolean canInsertItem(int i, ItemStack is, int j) {
		if (this instanceof InertIInv)
			return false;
		return this.isItemValidForSlot(i, is) && this.canEnterFromSide(dirs[j]);
	}

}
