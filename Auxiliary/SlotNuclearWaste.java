/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.ReactorCraft.Base.TileEntityWasteUnit;
import Reika.ReactorCraft.Registry.ReactorItems;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotNuclearWaste extends Slot {

	private TileEntityWasteUnit tile;

	public SlotNuclearWaste(TileEntityWasteUnit te, int id, int x, int y)
	{
		super(te, id, x, y);
		tile = te;
	}

	@Override
	public final boolean isItemValid(ItemStack is)
	{
		return is.getItem() == ReactorItems.WASTE.getItemInstance() && tile.isItemValidForSlot(this.getSlotIndex(), is);
	}

}