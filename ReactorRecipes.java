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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.RotaryCraft.Registry.ItemRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReactorRecipes {

	private static void addRCInterface() {
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(3, 7), ReactorItems.DEPLETED.getStackOf());
	}

	public static void addModInterface() {
		addRCInterface();
	}

	public static void addRecipes() {

		addMachines();
		addItems();
		addMisc();
		addSmelting();
	}

	private static void addSmelting() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITEORE.getBlockID(), 1, fl.ordinal());
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(fl.ordinal());
			FurnaceRecipes.smelting().addSmelting(block.itemID, block.getItemDamage(), shard, ReactorOres.FLUORITE.xpDropped);
		}
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			ItemStack block = ore.getOreBlock();
			ItemStack drop = ore.getProduct();
			FurnaceRecipes.smelting().addSmelting(block.itemID, block.getItemDamage(), drop, ore.xpDropped);
		}
	}

	private static void addMisc() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITE.getBlockID(), 1, fl.ordinal());
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(fl.ordinal());
			GameRegistry.addRecipe(block, "CCC", "CCC", "CCC", 'C', shard);
		}
	}

	private static void addItems() {
		GameRegistry.addRecipe(ReactorItems.DEPLETED.getStackOf(), "dd", "dd", 'd', ReactorStacks.depdust.copy());
		GameRegistry.addRecipe(ReactorItems.FUEL.getStackOf(), "dd", "dd", 'd', ReactorStacks.fueldust.copy());

		GameRegistry.addRecipe(ReactorItems.CANISTER.getCraftedProduct(16), " i ", "igi", " i ", 'g', Block.glass, 'i', Item.ingotIron);
	}

	private static void addMachines() {

	}

}
