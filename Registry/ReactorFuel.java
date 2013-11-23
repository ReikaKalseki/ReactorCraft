/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import net.minecraft.item.ItemStack;

public enum ReactorFuel {
	URANIUM(ReactorItems.FUEL.getStackOf()),
	PLUTONIUM(ReactorItems.PLUTONIUM.getStackOf()),
	THORIUM(ReactorItems.THORIUM.getStackOf());

	private final ItemStack fuel;

	private ReactorFuel(ItemStack item) {
		fuel = item;
	}

	public boolean canProducePower() {
		return true;
	}

	public ItemStack getFuelItem() {
		return fuel.copy();
	}
}
