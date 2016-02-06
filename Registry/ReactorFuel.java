/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ReactorFuel {

	URANIUM(ReactorItems.FUEL.getItemInstance(), 25, 3, 5, 20),
	PLUTONIUM(ReactorItems.PLUTONIUM.getItemInstance(), 30, 4, 10, 30);
	//THORIUM(ReactorItems.THORIUM.getItemInstance(), 20, 3, 2, 15);

	private final Item fuel;
	public final int fissionChance;
	public final int consumeChance;
	public final int wasteChance;
	public final int temperatureStep;

	private static final HashMap<Item, ReactorFuel> itemMap = new HashMap();
	public static final ReactorFuel[] fuelList = values();

	private ReactorFuel(Item item, int fiss, int con, int waste, int temp) {
		fuel = item;
		fissionChance = fiss;
		consumeChance = con;
		wasteChance = waste;
		temperatureStep = temp;
	}

	public boolean canProducePower() {
		return true;
	}

	public ItemStack getFuelItem() {
		return new ItemStack(fuel);
	}

	public ItemStack getFissionProduct(ItemStack input) {
		if (input == null)
			return null;
		switch(this) {
			case PLUTONIUM:
				if (input.getItemDamage() == ReactorItems.PLUTONIUM.getNumberMetadatas()-1)
					return null;
				else
					return ReactorItems.PLUTONIUM.getStackOfMetadata(input.getItemDamage()+1);
				/*
			case THORIUM:
				if (input.getItemDamage() == ReactorItems.THORIUM.getNumberMetadatas()-1)
					return null;
				else
					return ReactorItems.THORIUM.getStackOfMetadata(input.getItemDamage()+1);
				 */
			case URANIUM:
				if (input.getItemDamage() == ReactorItems.FUEL.getNumberMetadatas()-1)
					return ReactorItems.DEPLETED.getStackOf();
				else
					return ReactorItems.FUEL.getStackOfMetadata(input.getItemDamage()+1);
		}
		return null;
	}

	public static ReactorFuel getFrom(ItemStack is) {
		return is != null ? itemMap.get(is.getItem()) : null;
	}

	static {
		for (int i = 0; i < fuelList.length; i++) {
			ReactorFuel f = fuelList[i];
			itemMap.put(f.fuel, f);
		}
	}
}
