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
import net.minecraftforge.oredict.ShapelessOreRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Registry.CraftingItems;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.ItemRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReactorRecipes {

	private static void addRCInterface() {
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(3, 7), ReactorItems.DEPLETED.getStackOf());
		GameRegistry.addShapelessRecipe(FluoriteTypes.WHITE.getItem(), ItemStacks.getModOreIngot(ModOreList.FLUORITE));
	}

	public static void addModInterface() {
		addRCInterface();
	}

	public static void addRecipes() {

		addMachines();
		addCrafting();
		addItems();
		addMisc();
		addSmelting();
	}

	private static void addCrafting() {
		CraftingItems.TANK.addRecipe("OOO", "O O", "OOO", 'O', RotaryCraft.obsidianglass);
		CraftingItems.CANISTER.addRecipe(" S ", "SCS", " S ", 'S', ItemStacks.steelingot, 'C', Block.chest);
		CraftingItems.ROD.addRecipe("SAS", "SAS", "SAS", 'S', ItemStacks.steelingot, 'A', CraftingItems.ALLOY.getItem());
		GameRegistry.addRecipe(new ShapelessOreRecipe(CraftingItems.ALLOY.getItem(), "ingotCadmium", "ingotIndium", "ingotSilver"));
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

		ReikaRecipeHelper.addSmelting(ReactorStacks.calcite, ReactorStacks.lime, 0.2F);
	}

	private static void addMisc() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITE.getBlockID(), 1, fl.ordinal());
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(fl.ordinal());
			GameRegistry.addRecipe(block, "CCC", "CCC", "CCC", 'C', shard);
		}

		GameRegistry.addShapelessRecipe(new ItemStack(ReactorBlocks.MATS.getBlockID(), 2, MatBlocks.CONCRETE.ordinal()), Block.blockClay, Block.sand, Item.bucketWater);

		GameRegistry.addShapelessRecipe(new ItemStack(Item.leather), ReactorStacks.lime, Item.rottenFlesh);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 16, 0), ReactorStacks.lime, Item.bucketWater, "logWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 4, 0), ReactorStacks.lime, Item.bucketWater, "plankWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 1, 0), ReactorStacks.lime, Item.bucketWater, "stickWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 1, 0), ReactorStacks.lime, Item.bucketWater, "dustWood"));
	}

	private static void addItems() {
		GameRegistry.addRecipe(ReactorItems.DEPLETED.getStackOf(), "dd", "dd", 'd', ReactorStacks.depdust.copy());
		GameRegistry.addRecipe(ReactorItems.FUEL.getStackOf(), "dd", "dd", 'd', ReactorStacks.fueldust.copy());

		GameRegistry.addRecipe(ReactorItems.CANISTER.getCraftedProduct(16), " i ", "igi", " i ", 'g', Block.glass, 'i', Item.ingotIron);
	}

	private static void addMachines() {
		ReactorTiles.FUEL.addCrafting("SHS", "PCP", "SCS", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', CraftingItems.CANISTER.getItem(), 'H', Block.hopperBlock);
		ReactorTiles.CONTROL.addCrafting("SGS", " R ", "PPP", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'R', CraftingItems.ROD.getItem(), 'G', ItemStacks.gearunit);
		ReactorTiles.COOLANT.addCrafting("SPS", "GRG", "SPS", 'S', ItemStacks.steelingot, 'P', ItemStacks.pipe, 'G', Block.glass, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.TURBINECORE.addCrafting("BBB", "BCB", "BBB", 'B', ItemStacks.prop, 'C', ItemStacks.compoundturb);
		ReactorTiles.STEAMLINE.addSizedCrafting(3, "NPN", "NPN", "NPN", 'N', Block.cloth, 'P', ItemStacks.pipe);
		ReactorTiles.HEAVYPUMP.addCrafting("PSP", "GIG", "PpP", 'P', ItemStacks.basepanel, 'p', ItemStacks.pipe, 'G', Block.glass, 'I', ItemStacks.impeller, 'S', ItemStacks.shaftitem);
		ReactorTiles.CENTRIFUGE.addCrafting("SPS", "P P", "PGP", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'G', ItemStacks.gearunit16);
		ReactorTiles.PROCESSOR.addCrafting("POP", "OMO", 'O', CraftingItems.TANK.getItem(), 'M', ItemStacks.mixer, 'P', ItemStacks.pipe);
		ReactorTiles.WASTECONTAINER.addCrafting("SCS", "CcC", "SCS", 'S', ItemStacks.steelingot, 'C', MachineRegistry.COOLINGFIN.getCraftedProduct(), 'c', Block.chest);
		ReactorTiles.BOILER.addCrafting("SPS", "PrP", "SPS", 'r', MachineRegistry.RESERVOIR.getCraftedProduct(), 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot);
		ReactorTiles.CONDENSER.addCrafting("SPS", "pRp", "FFF", 'F', MachineRegistry.COOLINGFIN.getCraftedProduct(), 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'p', ItemStacks.pipe, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.GRATE.addCrafting("SIS", "p p", "SPS", 'p', ItemStacks.basepanel, 'P', ItemStacks.pipe, 'S', ItemStacks.steelingot, 'I', Block.fenceIron);
		ReactorTiles.PUMP.addCrafting("PpP", "gCg", "PsP", 'P', ItemStacks.basepanel, 'g', Block.thinGlass, 'p', ItemStacks.pipe, 'C', ItemStacks.compressor, 's', ItemStacks.shaftitem);
		ReactorTiles.SYNTHESIZER.addCrafting("SpS", "pMp", "ShS", 'S', ItemStacks.steelingot, 'M', ItemStacks.mixer, 'p', ItemStacks.basepanel, 'h', ItemStacks.igniter);
	}

}
