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

import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.Auxiliary.HandbookTracker;

import net.minecraft.item.ItemStack;

public class ReactorBookTracker extends HandbookTracker {

	@Override
	public ItemStack getItem() {
		return ReactorItems.BOOK.getStackOf();
	}

	@Override
	public String getID() {
		return "ReactorCraft_Handbook";
	}

}