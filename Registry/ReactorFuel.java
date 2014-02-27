/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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

	public static final ReactorFuel[] fuelList = values();

	private ReactorFuel(ItemStack item) {
		fuel = item;
	}

	public boolean canProducePower() {
		return true;
	}

	public ItemStack getFuelItem() {
		return fuel.copy();
	}

	public ItemStack getFissionProduct(ItemStack input) {
		if (input == null)
			return null;
		switch(this) {
		case PLUTONIUM:
			return null;
		case THORIUM:
			return null;
		case URANIUM:
			if (input.getItemDamage() == ReactorItems.FUEL.getNumberMetadatas()-1)
				return ReactorItems.DEPLETED.getStackOf();
			else
				return ReactorItems.FUEL.getStackOfMetadata(input.getItemDamage()+1);
		}
		return null;
	}
}
