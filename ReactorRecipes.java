/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
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
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesCompactor;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesGrinder;
import Reika.RotaryCraft.Registry.DifficultyEffects;
import Reika.RotaryCraft.Registry.ItemRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReactorRecipes {

	private static void addRCInterface() {
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(3, 7), ReactorItems.DEPLETED.getStackOf());
		GameRegistry.addShapelessRecipe(FluoriteTypes.WHITE.getItem(), ItemStacks.getModOreIngot(ModOreList.FLUORITE));

		RecipesGrinder.getRecipes().addRecipe(ReactorOres.PITCHBLENDE.getProduct(), CraftingItems.UDUST.getItem(), 0);
		RecipesGrinder.getRecipes().addRecipe(ItemStacks.getModOreIngot(ModOreList.PITCHBLENDE), CraftingItems.UDUST.getItem(), 0);
		RecipesGrinder.getRecipes().addRecipe(ItemStacks.getModOreIngot(ModOreList.URANIUM), CraftingItems.UDUST.getItem(), 0);

		RecipesCompactor.getRecipes().addCompacting(ReactorStacks.lodestone.copy(), ReactorItems.MAGNET.getCraftedProduct(2), 0, 5000, 100);
		for (int i = 0; i < ReactorItems.MAGNET.getNumberMetadatas()-1; i++)
			RecipesCompactor.getRecipes().addCompacting(ReactorItems.MAGNET.getStackOfMetadata(i), ReactorItems.MAGNET.getCraftedMetadataProduct(2, i+1), 0, 10000*(1+i), 100);
	}

	public static void addModInterface() {
		addRCInterface();
	}

	public static void addRecipes() {

		addMachines();
		addCrafting();
		addItems();
		addMisc();
		addMultiblocks();
		addSmelting();
	}

	private static void addMultiblocks() {
		int id = ReactorBlocks.HEATERMULTI.getBlockID();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "SBS", "BLB", "SBS", 'B', RotaryCraft.obsidianglass, 'S', ItemStacks.steelingot, 'L', ItemStacks.lens);
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "WWW", "WSW", "WWW", 'W', Block.cloth, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "SOS", "OSO", "SOS", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "OSO", "SSS", "OSO", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "SSS", "SOS", "SSS", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);

		id = ReactorBlocks.INJECTORMULTI.getBlockID();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "WWW", "HHH", "MMM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "MWM", "MHM", "MMM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "WMW", "MHM", "WMW", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "MMM", "HHH", "WWW", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "MMM", "HHH", "MWM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 5), "MWM", "HHH", "MWM", 'H', CraftingItems.WIRE.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 6), "MWM", "MHM", "MWM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 7), "HWH", "WMW", "HWH", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);

		id = ReactorBlocks.SOLENOIDMULTI.getBlockID();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "SSS", "SSS", "SSS", 'S', CraftingItems.FERROINGOT.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "SSS", "SBS", "SSS", 'S', CraftingItems.FERROINGOT.getItem(), 'B', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "SSS", "MMM", "SSS", 'M', ReactorStacks.maxMagnet, 'S', CraftingItems.MAGNETIC.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "SSS", "MMM", "SSS", 'M', ReactorStacks.weakerMagnet, 'S', CraftingItems.MAGNETIC.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "SSS", "MMM", "SSS", 'M', CraftingItems.FERROINGOT.getItem(), 'S', CraftingItems.HYSTERESIS.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 5), "SSS", "WPW", "SSS", 'W', CraftingItems.WIRE.getItem(), 'P', CraftingItems.MAGNETIC.getItem(), 'S', CraftingItems.FERROINGOT.getItem());
	}

	private static void addCrafting() {
		CraftingItems.TANK.addRecipe("OOO", "O O", "OOO", 'O', RotaryCraft.obsidianglass);
		CraftingItems.CANISTER.addRecipe(" S ", "SCS", " S ", 'S', CraftingItems.ALLOY.getItem(), 'C', Block.chest);
		CraftingItems.ROD.addRecipe("SAS", "SAS", "SAS", 'S', ItemStacks.steelingot, 'A', CraftingItems.ALLOY.getItem());
		GameRegistry.addRecipe(new ShapelessOreRecipe(CraftingItems.ALLOY.getItem(), "ingotCadmium", "ingotIndium", "ingotSilver"));
		CraftingItems.FERROINGOT.addShapelessRecipe(ItemStacks.steelingot, Item.ingotIron, ReactorStacks.lodestone);
		CraftingItems.MAGNETIC.addSizedRecipe(3, "SSS", 'S', CraftingItems.FERROINGOT.getItem());
		CraftingItems.MAGNETCORE.addRecipe("CCC", "C C", "CCC", 'C', CraftingItems.MAGNETIC.getItem());
		CraftingItems.HYSTERESISRING.addRecipe("CCC", "C C", "CCC", 'C', CraftingItems.HYSTERESIS.getItem());
		CraftingItems.WIRE.addSizedRecipe(2, "  G", " G ", "G  ", 'G', Item.ingotGold);
		CraftingItems.HYSTERESIS.addSizedRecipe(DifficultyEffects.PARTCRAFT.getInt(), "ISI", 'I', Item.ingotIron, 'S', ItemStacks.steelingot);
		CraftingItems.COOLANT.addRecipe("SPS", "S S", "SpS", 'S', ItemStacks.steelingot, 'P', Block.thinGlass, 'p', ItemStacks.pipe);
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
		ReikaRecipeHelper.addSmelting(ItemStacks.coaldust, CraftingItems.GRAPHITE.getItem(), 0);
	}

	private static void addMisc() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITE.getBlockID(), 1, fl.ordinal());
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(fl.ordinal());
			GameRegistry.addRecipe(block, "CCC", "CCC", "CCC", 'C', shard);
		}

		GameRegistry.addRecipe(MatBlocks.CALCITE.getStackOf(), "CCC", "CCC", "CCC", 'C', ReactorStacks.calcite);
		GameRegistry.addShapelessRecipe(ReikaItemHelper.getSizedItemStack(ReactorStacks.calcite, 9), MatBlocks.CALCITE.getStackOf());

		GameRegistry.addShapelessRecipe(MatBlocks.CONCRETE.getStackOf(2), Block.blockClay, Block.sand, Item.bucketWater);

		GameRegistry.addShapelessRecipe(new ItemStack(Item.leather), ReactorStacks.lime, Item.rottenFlesh);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 16, 0), ReactorStacks.lime, Item.bucketWater, "logWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 4, 0), ReactorStacks.lime, Item.bucketWater, "plankWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 1, 0), ReactorStacks.lime, Item.bucketWater, "stickWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.paper, 1, 0), ReactorStacks.lime, Item.bucketWater, "dustWood"));

		GameRegistry.addShapelessRecipe(ReactorStacks.calcite, ItemStacks.getModOreIngot(ModOreList.CALCITE));
		GameRegistry.addShapelessRecipe(ReactorStacks.ammonium, ItemStacks.getModOreIngot(ModOreList.AMMONIUM));
		GameRegistry.addShapelessRecipe(ReactorStacks.lodestone, ItemStacks.getModOreIngot(ModOreList.MAGNETITE));
	}

	private static void addItems() {
		GameRegistry.addRecipe(ReactorItems.DEPLETED.getCraftedProduct(2), "dd", "dd", 'd', ReactorStacks.depdust.copy());
		GameRegistry.addRecipe(ReactorItems.FUEL.getCraftedProduct(2), "dd", "dd", 'd', ReactorStacks.fueldust.copy());

		GameRegistry.addRecipe(ReactorItems.PELLET.getStackOf(), " G ", "GUG", " G ", 'G', CraftingItems.GRAPHITE.getItem(), 'U', CraftingItems.UDUST.getItem());

		GameRegistry.addRecipe(ReactorItems.BREEDERFUEL.getCraftedProduct(4), " D ", "DED", " D ", 'D', ReactorItems.DEPLETED.getStackOf(), 'E', ReactorItems.FUEL.getStackOf());

		GameRegistry.addRecipe(ReactorItems.CANISTER.getCraftedProduct(16), " i ", "igi", " i ", 'g', Block.glass, 'i', Item.ingotIron);

		GameRegistry.addRecipe(ReactorItems.REMOTE.getStackOf(), "SES", "BCB", "BPB", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'B', Block.stoneButton, 'E', Item.enderPearl, 'C', ItemStacks.pcb);

		GameRegistry.addRecipe(ReactorItems.BOOK.getStackOf(), "RSR", "PPP", "PPP", 'R', ReactorItems.FLUORITE.getItemInstance(), 'S', ItemStacks.steelingot, 'P', Item.paper);
	}

	private static void addMachines() {
		ReactorTiles.FUEL.addCrafting("SHS", "PCP", "SCS", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', CraftingItems.CANISTER.getItem(), 'H', Block.hopperBlock);
		ReactorTiles.CONTROL.addCrafting("SGS", "RRR", "PPP", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'R', CraftingItems.ROD.getItem(), 'G', ItemStacks.gearunit);
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
		ReactorTiles.SODIUMBOILER.addCrafting(" i ", "ibi", " i ", 'b', ReactorTiles.BOILER.getCraftedProduct(), 'i', Item.ingotIron);
		ReactorTiles.BREEDER.addCrafting("SPS", "PCP", "SPS", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', ReactorTiles.FUEL.getCraftedProduct());
		ReactorTiles.TRITIZER.addCrafting("SPS", "GPG", "SPS", 'G', Block.glass, 'P', ItemStacks.pipe, 'S', ItemStacks.steelingot);
		ReactorTiles.GASPIPE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "CGC", "CGC", "CGC", 'C', Block.hardenedClay, 'G', Block.glass);
		ReactorTiles.ELECTROLYZER.addCrafting("SPS", "PRP", "BPB", 'P', ItemStacks.pipe, 'B', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.EXCHANGER.addCrafting("FPF", "GIG", "FPF", 'P', ItemStacks.pipe, 'I', ItemStacks.impeller, 'G', Item.ingotGold, 'F', MachineRegistry.COOLINGFIN.getCraftedProduct());
		ReactorTiles.STORAGE.addCrafting("SPS", "PCP", "SPS", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'C', Block.chest);
		ReactorTiles.CPU.addCrafting("SCS", "CGC", "SCS", 'S', ItemStacks.basepanel, 'C', ItemStacks.pcb, 'G', ItemStacks.gearunit);
		ReactorTiles.MAGNETPIPE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "CGC", "CGC", "CGC", 'C', Item.ingotGold, 'G', RotaryCraft.obsidianglass);
		ReactorTiles.MAGNET.addCrafting("MCM", "CHC", "MCM", 'H', CraftingItems.HYSTERESISRING.getItem(), 'M', CraftingItems.MAGNETCORE.getItem(), 'C', CraftingItems.COOLANT.getItem());
		ReactorTiles.SOLENOID.addCrafting("SPS", "MCM", "IGI", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'M', CraftingItems.MAGNETIC.getItem(), 'C', CraftingItems.MAGNETCORE.getItem(), 'I', CraftingItems.FERROINGOT.getItem(), 'G', ItemStacks.gearunit16);
		ReactorTiles.HEATER.addCrafting("MPM", "P P", "MPM", 'M', CraftingItems.FERROINGOT.getItem(), 'P', RotaryCraft.obsidianglass);
		ReactorTiles.INJECTOR.addCrafting("PMP", "M M", "PMP", 'P', ReactorTiles.MAGNETPIPE.getCraftedProduct(), 'M', CraftingItems.MAGNETIC.getItem());
		ReactorTiles.ABSORBER.addCrafting("SPS", "PCP", "SPS", 'C', MatBlocks.CONCRETE.getStackOf(), 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel);
		ReactorTiles.CO2HEATER.addCrafting(" i ", "ibi", " i ", 'b', ReactorTiles.BOILER.getCraftedProduct(), 'i', ItemStacks.basepanel);
		ReactorTiles.PEBBLEBED.addCrafting("SHS", "PCP", "SHS", 'H', Block.hopperBlock, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', ReactorTiles.FUEL.getCraftedProduct());
		ReactorTiles.COLLECTOR.addCrafting(" p ", "SpS", "PpP", 'p', ItemStacks.pipe, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot);
	}

}
