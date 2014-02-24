/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.Auxiliary.HandbookTracker;

public class ReactorBookTracker extends HandbookTracker {

	public ReactorBookTracker(String name) {
		super(name);
	}

	@Override
	public ItemStack getItem() {
		return ReactorItems.BOOK.getStackOf();
	}

}
