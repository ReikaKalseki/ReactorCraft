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

import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.Registry.ItemRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReactorRecipes {

	public static void addRCInterface() {
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(3, 7), ReactorItems.DEPLETED.getStackOf());
	}

	public static void addModInterface() {
		addRCInterface();
	}

	public static void addRecipes() {

	}

}
