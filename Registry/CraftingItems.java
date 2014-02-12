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
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public enum CraftingItems {

	CANISTER(),
	ROD(),
	TANK(),
	ALLOY(),
	BACKING(),
	MAGNETIC(),
	MAGNETCORE(),
	COOLANT(),
	WIRE(),
	SHIELD(),
	FERROINGOT(),
	HYSTERESIS(),
	HYSTERESISRING();

	public final String itemName;

	public static final CraftingItems[] partList = values();

	private CraftingItems() {
		itemName = StatCollector.translateToLocal("crafting."+this.name().toLowerCase());
	}

	public ItemStack getItem() {
		return ReactorItems.CRAFTING.getStackOfMetadata(this.ordinal());
	}

	public void addRecipe(Object... o) {
		GameRegistry.addRecipe(this.getItem(), o);
	}

	public void addSizedRecipe(int size, Object... o) {
		GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), size), o);
	}

	public void addShapelessRecipe(Object... o) {
		GameRegistry.addShapelessRecipe(this.getItem(), o);
	}

}
