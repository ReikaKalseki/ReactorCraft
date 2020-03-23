/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

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
	HYSTERESISRING(),
	GRAPHITE(),
	UDUST(),
	FABRIC(),
	CARBIDEFLAKES(),
	CARBIDE(),
	TURBCORE();

	public final String itemName;

	public static final CraftingItems[] partList = values();

	private CraftingItems() {
		itemName = StatCollector.translateToLocal("crafting."+this.name().toLowerCase(Locale.ENGLISH));
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

	public void addSizedOreRecipe(int size, Object... o) {
		ShapedOreRecipe ir = new ShapedOreRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), size), o);
		GameRegistry.addRecipe(ir);
	}

	public void addShapelessRecipe(Object... o) {
		GameRegistry.addShapelessRecipe(this.getItem(), o);
	}

	public void addShapelessOreRecipe(Object... o) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(this.getItem(), o));
	}

	public boolean isGating() {
		switch(this) {
			case WIRE:
			case COOLANT:
			case UDUST:
			case FABRIC:
			case HYSTERESIS:
			case HYSTERESISRING:
				return false;
			default:
				return true;
		}
	}

}
