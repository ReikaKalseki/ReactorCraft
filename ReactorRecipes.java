/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Instantiable.Recipe.FlexibleIngredient;
import Reika.DragonAPI.Instantiable.Recipe.FluidInputRecipe.ShapelessFluidInputRecipe;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.Isotopes.ElementGroup;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2Handler;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Registry.CraftingItems;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityTritizer;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipeHandler.RecipeLevel;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesBlastFurnace;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesCentrifuge;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesCompactor;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesCrystallizer;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesFrictionHeater;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesGrinder;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesPulseFurnace;
import Reika.RotaryCraft.Items.Tools.ItemEngineUpgrade.Upgrades;
import Reika.RotaryCraft.Registry.BlockRegistry;
import Reika.RotaryCraft.Registry.DifficultyEffects;
import Reika.RotaryCraft.Registry.GearboxTypes;
import Reika.RotaryCraft.Registry.GearboxTypes.GearPart;
import Reika.RotaryCraft.Registry.ItemRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Processing.TileEntityFuelConverter;

import cpw.mods.fml.common.registry.GameRegistry;

public class ReactorRecipes {

	private static void addRCInterface() {
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(3, 9), ReactorItems.DEPLETED.getStackOf());
		GameRegistry.addShapelessRecipe(ItemRegistry.RAILGUN.getCraftedMetadataProduct(1, 9), ReactorItems.OLDPELLET.getStackOf());
		GameRegistry.addShapelessRecipe(FluoriteTypes.WHITE.getItem(), ItemStacks.getModOreIngot(ModOreList.FLUORITE));

		GameRegistry.addShapedRecipe(Upgrades.LODESTONE.getStack(), "php", "mcm", "php", 'p', ItemStacks.basepanel, 'm', ReactorItems.MAGNET.getStackOfMetadata(3), 'h', ReactorBlocks.SOLENOIDMULTI.getStackOfMetadata(4), 'c', CraftingItems.MAGNETCORE.getItem());

		RecipesGrinder.getRecipes().addRecipe(ReactorOres.PITCHBLENDE.getProduct(), CraftingItems.UDUST.getItem());
		RecipesGrinder.getRecipes().addRecipe(ItemStacks.getModOreIngot(ModOreList.PITCHBLENDE), CraftingItems.UDUST.getItem());
		RecipesGrinder.getRecipes().addOreDictRecipe(ModOreList.PITCHBLENDE.getProductOreDictName(), CraftingItems.UDUST.getItem(), RecipeLevel.CORE);
		RecipesGrinder.getRecipes().addOreDictRecipe(ModOreList.URANIUM.getProductOreDictName(), CraftingItems.UDUST.getItem(), RecipeLevel.CORE);

		RecipesGrinder.getRecipes().addRecipe(ItemStacks.tungsteningot, ItemStacks.tungstenflakes);

		RecipesGrinder.getRecipes().addRecipe(new ItemStack(Items.emerald), ReactorStacks.emeralddust);

		RecipesCrystallizer.getRecipes().addRecipe(FluidRegistry.getFluid("rc nuclear waste"), 50, ReactorStacks.wastedust, RecipeLevel.CORE);

		RecipesCentrifuge.getRecipes().addRecipe(ReactorStacks.wastedust, WasteManager.getThoriumOutputs(true), null, RecipeLevel.CORE);
		for (ElementGroup g : ElementGroup.values()) {
			RecipesCentrifuge.getRecipes().addRecipe(WasteManager.getWaste(g), WasteManager.getThoriumGroupOutputs(g), null, RecipeLevel.CORE);
		}

		RecipesPulseFurnace.getRecipes().addSmelting(CraftingItems.CARBIDEFLAKES.getItem(), CraftingItems.CARBIDE.getItem());

		RecipesCompactor.getRecipes().addRecipe(ReactorStacks.lodestone.copy(), ReactorItems.MAGNET.getCraftedProduct(2), 5000, 100);
		for (int i = 0; i < ReactorItems.MAGNET.getNumberMetadatas()-1; i++)
			RecipesCompactor.getRecipes().addRecipe(ReactorItems.MAGNET.getStackOfMetadata(i), ReactorItems.MAGNET.getCraftedMetadataProduct(2, i+1), 10000*(1+i), 100);

		TileEntityFuelConverter.addRecipe("rc lifbe", "rc lifbe fuel", 10, 1, 0.1, new ItemMatch("dustThorium"));

		if (ModList.IC2.isLoaded() && !IC2Handler.getInstance().isIC2Classic())
			TileEntityFuelConverter.addRecipe("rc lowpwater", "ic2distilledwater", 25, 1, 0.25, new ItemMatch(ReactorStacks.lime));
	}

	public static void addModInterface() {
		addRCInterface();

		ArrayList<ItemStack> li = OreDictionary.getOres(ReactorOres.MAGNETITE.getProductDictionaryName());
		for(ItemStack is : li) {
			if (is.getItem() != ReactorStacks.lodestone.getItem()) {
				GameRegistry.addShapelessRecipe(is.copy(), ReactorStacks.lodestone);
			}
		}

		if (ModList.THERMALEXPANSION.isLoaded()) {
			ItemStack wool = ReikaItemHelper.lookupItem("ThermalExpansion:Rockwool");
			if (wool != null) {
				wool = wool.copy();
				wool.setItemDamage(OreDictionary.WILDCARD_VALUE);
				GameRegistry.addRecipe(ReactorStacks.insulCore, "WWW", "WSW", "WWW", 'W', wool, 'S', ItemStacks.steelingot);
			}
		}

		GameRegistry.addRecipe(new ShapelessFluidInputRecipe(MatBlocks.CONCRETE.getStackOf(4), Blocks.clay, Blocks.sand, Blocks.gravel, FluidRegistry.WATER));
		if (FluidRegistry.getFluid("oil") != null)
			GameRegistry.addRecipe(new ShapelessFluidInputRecipe(MatBlocks.CONCRETE.getStackOf(6), Blocks.sand, Blocks.gravel, FluidRegistry.getFluid("oil")));
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
		Block id = ReactorBlocks.HEATERMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "SBS", "BLB", "SBS", 'B', BlockRegistry.BLASTGLASS.getStackOf(), 'S', ItemStacks.steelingot, 'L', ItemStacks.lens);
		GameRegistry.addRecipe(ReactorStacks.insulCore, "WWW", "WSW", "WWW", 'W', Blocks.wool, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "SOS", "OSO", "SOS", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "OSO", "SSS", "OSO", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "SSS", "SOS", "SSS", 'O', ReactorStacks.insulCore, 'S', ItemStacks.steelingot);

		id = ReactorBlocks.INJECTORMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "WWW", "HHH", "MMM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "MWM", "MHM", "MMM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "WMW", "MHM", "WMW", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "MMM", "HHH", "WWW", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "MMM", "MHM", "MWM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 5), "MWM", "HHH", "MWM", 'H', CraftingItems.WIRE.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 6), "MWM", "MHM", "MWM", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);
		GameRegistry.addRecipe(new ItemStack(id, 1, 7), "HWH", "WMW", "HWH", 'H', CraftingItems.HYSTERESIS.getItem(), 'M', CraftingItems.MAGNETIC.getItem(), 'W', ReactorStacks.insulCore);

		id = ReactorBlocks.SOLENOIDMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "SSS", "SSS", "SSS", 'S', CraftingItems.FERROINGOT.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "SSS", "SBS", "SSS", 'S', CraftingItems.FERROINGOT.getItem(), 'B', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "SSS", "MMM", "SSS", 'M', ReactorStacks.maxMagnet, 'S', CraftingItems.MAGNETIC.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "SSS", "MMM", "SSS", 'M', ReactorStacks.weakerMagnet, 'S', CraftingItems.MAGNETIC.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 4), "SSS", "MMM", "SSS", 'M', CraftingItems.FERROINGOT.getItem(), 'S', CraftingItems.HYSTERESIS.getItem());
		GameRegistry.addRecipe(new ItemStack(id, 1, 5), "SSS", "WPW", "SSS", 'W', CraftingItems.WIRE.getItem(), 'P', CraftingItems.MAGNETIC.getItem(), 'S', CraftingItems.FERROINGOT.getItem());

		id = ReactorBlocks.GENERATORMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "SsS", "scs", "SsS", 'c', GearboxTypes.BEDROCK.getPart(GearPart.SHAFTCORE), 'S', ItemStacks.steelingot, 's', ItemStacks.shaftitem);
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "SSS", "SBS", "SSS", 'S', CraftingItems.WIRE.getItem(), 'B', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "SSS", "MMM", "SSS", 'M', CraftingItems.WIRE.getItem(), 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 3), "W W", " S ", "W W", 'W', ReikaItemHelper.blackWool, 'S', ItemStacks.steelingot);

		id = ReactorBlocks.TURBINEMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "sis", "ici", "sis", 's', ItemStacks.prop, 'c', ItemStacks.springtungsten, 'i', ItemStacks.silumin);
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "BBB", "SSS", "ppp", 'B', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'p', ItemStacks.prop);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "PbP", "bIb", "PbP", 'P', ItemStacks.pipe, 'b', ItemStacks.basepanel, 'I', MachineRegistry.PIPEPUMP.getCraftedProduct());

		id = ReactorBlocks.FLYWHEELMULTI.getBlockInstance();
		GameRegistry.addRecipe(new ItemStack(id, 1, 0), "sSs", "SCS", "sSs", 's', ItemStacks.springtungsten, 'S', ItemStacks.steelblock, 'C', GearboxTypes.BEDROCK.getPart(GearPart.UNIT16));
		GameRegistry.addRecipe(new ItemStack(id, 1, 1), "WWW", "SSS", "WWW", 'W', Blocks.wool, 'S', ItemStacks.steelingot);
		GameRegistry.addRecipe(new ItemStack(id, 1, 2), "sSs", "SSS", "sSs", 's', ItemStacks.basepanel, 'S', ItemStacks.springingot);
	}

	private static void addCrafting() {
		CraftingItems.TANK.addRecipe("OOO", "O O", "OOO", 'O', BlockRegistry.BLASTGLASS.getStackOf());
		CraftingItems.CANISTER.addRecipe(" S ", "SCS", " S ", 'S', CraftingItems.ALLOY.getItem(), 'C', Blocks.chest);
		CraftingItems.ROD.addRecipe("SAS", "ACA", "SAS", 'S', ItemStacks.steelingot, 'A', CraftingItems.ALLOY.getItem(), 'C', CraftingItems.GRAPHITE.getItem());
		CraftingItems.MAGNETIC.addSizedRecipe(3, "SSS", 'S', CraftingItems.FERROINGOT.getItem());
		CraftingItems.MAGNETCORE.addRecipe("CCC", "C C", "CCC", 'C', CraftingItems.MAGNETIC.getItem());
		CraftingItems.HYSTERESISRING.addRecipe("CCC", "C C", "CCC", 'C', CraftingItems.HYSTERESIS.getItem());
		CraftingItems.WIRE.addSizedRecipe(2, "  G", " G ", "G  ", 'G', Items.gold_ingot);
		CraftingItems.HYSTERESIS.addSizedRecipe(DifficultyEffects.PARTCRAFT.getInt(), "ISI", 'I', Items.iron_ingot, 'S', ItemStacks.steelingot);
		CraftingItems.COOLANT.addRecipe("SPS", "S S", "SpS", 'S', ItemStacks.steelingot, 'P', Blocks.glass_pane, 'p', ItemStacks.pipe);
		CraftingItems.FABRIC.addSizedRecipe(3, "LDL", "LDL", "LDL", 'D', ReactorItems.DEPLETED.getStackOf(), 'L', Items.leather);
		CraftingItems.FABRIC.addSizedRecipe(1, "LDL", "LDL", "LDL", 'D', ReactorItems.OLDPELLET.getStackOf(), 'L', Items.leather);
		CraftingItems.FABRIC.addSizedOreRecipe(2, "LDL", "LDL", "LDL", 'D', "ingotLead", 'L', Items.leather);
		CraftingItems.CARBIDEFLAKES.addShapelessOreRecipe("dustCoal", ItemStacks.tungstenflakes);
		CraftingItems.TURBCORE.addRecipe("CCC", "CTC", "CCC", 'C', CraftingItems.CARBIDE.getItem(), 'T', ItemStacks.compoundturb);

		//GameRegistry.addRecipe(new ShapelessOreRecipe(CraftingItems.ALLOY.getItem(), "ingotCadmium", "ingotIndium", "ingotSilver"));
		ItemStack is = ReikaItemHelper.getSizedItemStack(CraftingItems.ALLOY.getItem(), 3);
		ShapelessOreRecipe slr = new ShapelessOreRecipe(is, "ingotCadmium", "ingotIndium", "ingotSilver");
		RecipesBlastFurnace.getRecipes().addAlloyingRecipe(is, 1600, slr, 1, 0.8F); //1450

		//CraftingItems.FERROINGOT.addShapelessRecipe(ItemStacks.steelingot, Items.iron_ingot, ReactorStacks.lodestone);
		is = ReikaItemHelper.getSizedItemStack(CraftingItems.FERROINGOT.getItem(), 1);
		ShapelessOreRecipe sor = new ShapelessOreRecipe(is, ItemStacks.steelingot, Items.iron_ingot, ReactorStacks.lodestone);
		RecipesBlastFurnace.getRecipes().addAlloyingRecipe(is, 1200, sor, 1, 1); //1600

		if (ReikaItemHelper.oreItemExists("ingotNickel")) {
			sor = new ShapelessOreRecipe(is, ItemStacks.steelingot, "ingotNickel", ReactorStacks.lodestone);
			RecipesBlastFurnace.getRecipes().addAlloyingRecipe(is, 1200, sor, 1, 1); //1600
		}
	}

	private static void addSmelting() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITEORE.getBlockInstance(), 1, fl.ordinal());
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(fl.ordinal());
			ReikaRecipeHelper.addSmelting(block, shard, ReactorOres.FLUORITE.xpDropped);
		}
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			ItemStack block = ore.getOreBlock();
			ItemStack drop = ore.getProduct();
			ReikaRecipeHelper.addSmelting(block, drop, ore.xpDropped);
		}

		ReikaRecipeHelper.addSmelting(ReactorStacks.calcite, ReactorStacks.lime, 0.2F);
		//ReikaRecipeHelper.addSmelting(ItemStacks.coaldust, CraftingItems.GRAPHITE.getItem(), 0);
		RecipesFrictionHeater.getRecipes().addCoreRecipe(ItemStacks.coaldust, CraftingItems.GRAPHITE.getItem(), 400, 100);

		RecipesBlastFurnace.getRecipes().addRecipe(ReactorStacks.lime, 850, ReikaRecipeHelper.getShapelessRecipeFor(ReactorStacks.lime, new ItemStack(Items.egg)), 1, 0.1F);
	}

	private static void addMisc() {
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack block = new ItemStack(ReactorBlocks.FLUORITE.getBlockInstance(), 1, i);
			ItemStack shard = ReactorItems.FLUORITE.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(ReactorBlocks.LAMP.getBlockInstance(), 1, i);
			GameRegistry.addRecipe(block, "CCC", "CCC", "CCC", 'C', shard);
			GameRegistry.addShapelessRecipe(ReikaItemHelper.getSizedItemStack(shard, 9), block);
			GameRegistry.addRecipe(lamp, "SCS", "C C", "SOS", 'C', shard, 'S', ItemStacks.steelingot, 'O', Blocks.obsidian);

			if (ReactorOptions.DYECRAFT.getState()) {
				for (int k = 0; k < FluoriteTypes.colorList.length; k++) {
					if (k != i) {
						String dye = FluoriteTypes.colorList[k].getCorrespondingDyeType().getOreDictName();
						GameRegistry.addRecipe(new ShapelessOreRecipe(ReactorItems.FLUORITE.getStackOfMetadata(k), shard, dye));
					}
				}
			}
		}

		GameRegistry.addRecipe(MatBlocks.CALCITE.getStackOf(), "CCC", "CCC", "CCC", 'C', ReactorStacks.calcite);
		GameRegistry.addShapelessRecipe(ReikaItemHelper.getSizedItemStack(ReactorStacks.calcite, 9), MatBlocks.CALCITE.getStackOf());

		GameRegistry.addRecipe(MatBlocks.GRAPHITE.getStackOf(), "CCC", "CCC", "CCC", 'C', CraftingItems.GRAPHITE.getItem());
		GameRegistry.addShapelessRecipe(ReikaItemHelper.getSizedItemStack(CraftingItems.GRAPHITE.getItem(), 9), MatBlocks.GRAPHITE.getStackOf());

		GameRegistry.addRecipe(MatBlocks.LODESTONE.getStackOf(), "CCC", "CCC", "CCC", 'C', ReactorStacks.lodestone);
		GameRegistry.addShapelessRecipe(ReikaItemHelper.getSizedItemStack(ReactorStacks.lodestone, 9), MatBlocks.LODESTONE.getStackOf());

		GameRegistry.addRecipe(MatBlocks.SCRUBBER.getStackOf(), "IWI", "WPW", "IWI", 'I', Blocks.iron_bars, 'W', Blocks.wool, 'P', ItemStacks.pipe);

		GameRegistry.addShapelessRecipe(MatBlocks.CONCRETE.getStackOf(4), Blocks.clay, Blocks.sand, Blocks.gravel, Items.water_bucket);

		GameRegistry.addShapelessRecipe(new ItemStack(Items.leather), ReactorStacks.lime, Items.rotten_flesh);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.paper, 16, 0), ReactorStacks.lime, Items.water_bucket, "logWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.paper, 4, 0), ReactorStacks.lime, Items.water_bucket, "plankWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.paper, 1, 0), ReactorStacks.lime, Items.water_bucket, "stickWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.paper, 1, 0), ReactorStacks.lime, Items.water_bucket, "dustWood"));

		GameRegistry.addShapelessRecipe(ReactorStacks.calcite, ItemStacks.getModOreIngot(ModOreList.CALCITE));
		GameRegistry.addShapelessRecipe(ReactorStacks.ammonium, ItemStacks.getModOreIngot(ModOreList.AMMONIUM));
		GameRegistry.addShapelessRecipe(ReactorStacks.lodestone, ItemStacks.getModOreIngot(ModOreList.MAGNETITE));
	}

	private static void addItems() {
		GameRegistry.addRecipe(ReactorItems.DEPLETED.getCraftedProduct(2), "dd", "dd", 'd', ReactorStacks.depdust.copy());
		GameRegistry.addRecipe(ReactorItems.FUEL.getCraftedProduct(2), "dd", "dd", 'd', ReactorStacks.fueldust.copy());
		//GameRegistry.addRecipe(ReactorItems.THORIUM.getCraftedProduct(1), "dd", "dd", 'd', ReactorStacks.thordust.copy());

		//GameRegistry.addRecipe(ReactorItems.PELLET.getCraftedProduct(3), " G ", "GUG", " G ", 'G', CraftingItems.GRAPHITE.getItem(), 'U', CraftingItems.UDUST.getItem());
		ShapedRecipes sr = ReikaRecipeHelper.getShapedRecipeFor(ReactorItems.PELLET.getCraftedProduct(4), " G ", "GUG", " G ", 'G', CraftingItems.GRAPHITE.getItem(), 'U', CraftingItems.UDUST.getItem());
		RecipesBlastFurnace.getRecipes().addRecipe(ReactorItems.PELLET.getCraftedProduct(4), 750, sr, 1, 1);

		GameRegistry.addRecipe(ReactorItems.BREEDERFUEL.getCraftedProduct(4), " D ", "DED", " D ", 'D', ReactorItems.DEPLETED.getStackOf(), 'E', ReactorItems.FUEL.getStackOf());

		GameRegistry.addRecipe(ReactorItems.CANISTER.getCraftedProduct(16), " i ", "igi", " i ", 'g', Blocks.glass, 'i', Items.iron_ingot);

		GameRegistry.addRecipe(ReactorItems.REMOTE.getStackOf(), "SES", "BCB", "BPB", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'B', Blocks.stone_button, 'E', Items.ender_pearl, 'C', ItemStacks.pcb);

		GameRegistry.addRecipe(ReactorItems.BOOK.getStackOf(), "RSR", "PPP", "PPP", 'R', ReactorItems.FLUORITE.getItemInstance(), 'S', ItemStacks.steelingot, 'P', Items.paper);

		GameRegistry.addRecipe(ReactorItems.HAZHELMET.getStackOf(), "FFF", "F F", 'F', CraftingItems.FABRIC.getItem());
		GameRegistry.addRecipe(ReactorItems.HAZCHEST.getStackOf(), "F F", "FFF", "FFF", 'F', CraftingItems.FABRIC.getItem());
		GameRegistry.addRecipe(ReactorItems.HAZLEGS.getStackOf(), "FFF", "F F", "F F", 'F', CraftingItems.FABRIC.getItem());
		GameRegistry.addRecipe(ReactorItems.HAZBOOTS.getStackOf(), "F F", "F F", 'F', CraftingItems.FABRIC.getItem());

		GameRegistry.addRecipe(ReactorItems.GEIGER.getStackOf(), " r ", "sSs", "sgs", 'g', ItemStacks.steelgear, 's', ItemStacks.steelingot, 'S', ItemStacks.screen, 'r', ItemStacks.radar);

		GameRegistry.addRecipe(ReactorItems.CLEANUP.getStackOf(), " sp", "sbs", "ss ", 'b', Items.water_bucket, 's', ItemStacks.steelingot, 'p', ItemStacks.pipe);

		GameRegistry.addRecipe(ReactorItems.IRONFINDER.getStackOf(), "L L", "S S", "SSS", 'S', ItemStacks.steelingot, 'L', ReactorStacks.lodestone);

		ItemRegistry.UPGRADE.addMetaBlastRecipe(1980, 32, Upgrades.EFFICIENCY.ordinal(), "IGI", "FTF", "BPB", 'G', ItemStacks.generator, 'I', ItemStacks.redgoldingot, 'B', ItemStacks.waterplate, 'P', ItemStacks.power, 'F', CraftingItems.FERROINGOT.getItem(), 'T', ItemStacks.tungsteningot);
	}

	private static void addMachines() {
		ReactorTiles.FUEL.addCrafting("SHS", "PCP", "SCS", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', CraftingItems.CANISTER.getItem(), 'H', Blocks.hopper);
		ReactorTiles.CONTROL.addCrafting("SGS", "RRR", "PPP", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'R', CraftingItems.ROD.getItem(), 'G', ItemStacks.gearunit);
		ReactorTiles.COOLANT.addCrafting("SPS", "GRG", "SPS", 'S', ItemStacks.steelingot, 'P', ItemStacks.pipe, 'G', Blocks.glass, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.TURBINECORE.addCrafting("BBB", "BCB", "BBB", 'B', ItemStacks.prop, 'C', CraftingItems.TURBCORE.getItem());
		ReactorTiles.STEAMLINE.addSizedCrafting(3, "NPN", "NPN", "NPN", 'N', Blocks.wool, 'P', ItemStacks.pipe);
		ReactorTiles.FLUIDEXTRACTOR.addCrafting("PpP", "GIG", "PSP", 'P', ItemStacks.basepanel, 'p', ItemStacks.pipe, 'G', Blocks.glass, 'I', ItemStacks.impeller, 'S', ItemStacks.shaftitem);
		ReactorTiles.CENTRIFUGE.addCrafting("SPS", "B B", "PGP", 'B', ItemStacks.bedingot, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'G', GearboxTypes.BEDROCK.getPart(GearPart.UNIT16));
		ReactorTiles.PROCESSOR.addCrafting("POP", "OMO", 'O', CraftingItems.TANK.getItem(), 'M', ItemStacks.mixer, 'P', ItemStacks.pipe);
		ReactorTiles.WASTECONTAINER.addCrafting("SCS", "CcC", "SCS", 'S', ItemStacks.steelingot, 'C', MachineRegistry.COOLINGFIN.getCraftedProduct(), 'c', Blocks.chest);
		ReactorTiles.BOILER.addCrafting("SPS", "PrP", "SPS", 'r', MachineRegistry.RESERVOIR.getCraftedProduct(), 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot);
		ReactorTiles.CONDENSER.addCrafting("SPS", "pRp", "FFF", 'F', MachineRegistry.COOLINGFIN.getCraftedProduct(), 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'p', ItemStacks.pipe, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.GRATE.addCrafting("SIS", "p p", "SPS", 'p', ItemStacks.basepanel, 'P', ItemStacks.pipe, 'S', ItemStacks.steelingot, 'I', Blocks.iron_bars);
		ReactorTiles.PUMP.addCrafting("PpP", "gCg", "PsP", 'P', ItemStacks.basepanel, 'g', Blocks.glass_pane, 'p', ItemStacks.pipe, 'C', ItemStacks.compressor, 's', ItemStacks.shaftitem);
		ReactorTiles.SYNTHESIZER.addCrafting("SpS", "pMp", "ShS", 'S', ItemStacks.steelingot, 'M', ItemStacks.mixer, 'p', ItemStacks.basepanel, 'h', ItemStacks.igniter);
		ReactorTiles.SODIUMBOILER.addCrafting(" i ", "ibi", " i ", 'b', ReactorTiles.BOILER.getCraftedProduct(), 'i', Items.iron_ingot);
		ReactorTiles.BREEDER.addCrafting("SPS", "PCP", "SPS", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', ReactorTiles.FUEL.getCraftedProduct());
		ReactorTiles.TRITIZER.addCrafting("SPS", "GPG", "SPS", 'G', Blocks.glass, 'P', ItemStacks.pipe, 'S', ItemStacks.steelingot);
		ReactorTiles.GASPIPE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "CGC", "CGC", "CGC", 'C', Blocks.hardened_clay, 'G', Blocks.glass);
		ReactorTiles.ELECTROLYZER.addCrafting("SPS", "PRP", "BPB", 'P', ItemStacks.pipe, 'B', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'R', MachineRegistry.RESERVOIR.getCraftedProduct());
		ReactorTiles.EXCHANGER.addCrafting("FPF", "GIG", "FPF", 'P', ItemStacks.pipe, 'I', ItemStacks.impeller, 'G', Items.gold_ingot, 'F', MachineRegistry.COOLINGFIN.getCraftedProduct());
		ReactorTiles.STORAGE.addCrafting("SPS", "PCP", "SPS", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'C', Blocks.chest);
		ReactorTiles.CPU.addCrafting("SCS", "CGC", "SCS", 'S', ItemStacks.basepanel, 'C', ItemStacks.pcb, 'G', ItemStacks.gearunit);
		ReactorTiles.MAGNETPIPE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "CGC", "CGC", "CGC", 'C', Items.gold_ingot, 'G', BlockRegistry.BLASTGLASS.getStackOf());
		ReactorTiles.MAGNET.addCrafting("MCM", "CHC", "MCM", 'H', CraftingItems.HYSTERESISRING.getItem(), 'M', CraftingItems.MAGNETCORE.getItem(), 'C', CraftingItems.COOLANT.getItem());
		ReactorTiles.SOLENOID.addCrafting("SPS", "MCM", "IGI", 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel, 'M', CraftingItems.MAGNETIC.getItem(), 'C', CraftingItems.MAGNETCORE.getItem(), 'I', CraftingItems.FERROINGOT.getItem(), 'G', GearboxTypes.TUNGSTEN.getPart(GearPart.UNIT16));
		ReactorTiles.HEATER.addCrafting("MPM", "P P", "MPM", 'M', CraftingItems.FERROINGOT.getItem(), 'P', BlockRegistry.BLASTGLASS.getStackOf());
		ReactorTiles.INJECTOR.addCrafting("PMP", "M M", "PMP", 'P', ReactorTiles.MAGNETPIPE.getCraftedProduct(), 'M', CraftingItems.MAGNETIC.getItem());
		ReactorTiles.ABSORBER.addCrafting(" P ", "PCP", " P ", 'C', ItemStacks.steelblock, 'P', ReactorItems.DEPLETED.getStackOf());
		ReactorTiles.ABSORBER.addCrafting("PPP", "PCP", "PPP", 'C', ItemStacks.steelblock, 'P', ReactorItems.OLDPELLET.getStackOf());
		ReactorTiles.CO2HEATER.addCrafting(" i ", "ibi", " i ", 'b', ReactorTiles.BOILER.getCraftedProduct(), 'i', ItemStacks.basepanel);
		ReactorTiles.PEBBLEBED.addCrafting("SHS", "PCP", "SHS", 'H', Blocks.hopper, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', ReactorTiles.FUEL.getCraftedProduct());
		ReactorTiles.COLLECTOR.addCrafting(" p ", "SpS", "PpP", 'p', ItemStacks.pipe, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot);
		ReactorTiles.REFLECTOR.addCrafting("GGG", "GSG", "GGG", 'G', CraftingItems.GRAPHITE.getItem(), 'S', ItemStacks.steelblock);

		if (PowerTypes.RF.isLoaded() || PowerTypes.EU.isLoaded())
			ReactorTiles.GENERATOR.addCrafting("RGR", "GFG", "RGR", 'G', CraftingItems.WIRE.getItem(), 'R', Items.redstone, 'F', CraftingItems.MAGNETCORE.getItem());

		ReactorTiles.MARKER.addCrafting("F", "R", 'F', FluoriteTypes.BLUE.getItem(), 'R', Blocks.redstone_torch);
		ReactorTiles.TURBINEMETER.addCrafting("SrS", "PGP", "PCP", 'P', ItemStacks.basepanel, 'C', ItemStacks.pcb, 'G', Blocks.glowstone, 'r', Items.redstone, 'S', ItemStacks.steelingot);
		ReactorTiles.BIGTURBINE.addCrafting("BBB", "BCB", "BBB", 'B', ItemStacks.prop, 'C', ReactorTiles.TURBINECORE.getCraftedProduct());
		ReactorTiles.FLYWHEEL.addCrafting("BBB", "STS", "BBB", 'B', ItemStacks.steelblock, 'S', ItemStacks.shaftitem, 'T', GearboxTypes.TUNGSTEN.getPart(GearPart.SHAFTCORE));

		ReactorTiles.DIFFUSER.addCrafting("BBB", "DPD", "BBB", 'B', ItemStacks.basepanel, 'D', ItemStacks.diffuser, 'P', ItemStacks.pipe);

		ReactorTiles.THORIUM.addCrafting("aSa", "PCP", "tPt", 't', ItemStacks.tungsteningot, 'a', ItemStacks.silumin, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot, 'C', ReactorTiles.FUEL.getCraftedProduct());
		ReactorTiles.FUELDUMP.addCrafting("pIp", "BPB", "pbp", 'b', Blocks.iron_bars, 'p', ItemStacks.pipe, 'P', ItemStacks.bedpipe, 'B', ItemStacks.basepanel, 'I', ItemStacks.impeller);

		ReactorTiles.WASTEPIPE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "CbC", "CGC", "CbC", 'C', MatBlocks.CONCRETE.getStackOf(), 'b', Blocks.iron_bars, 'G', Blocks.glass);

		ReactorTiles.SOLARTOP.addCrafting("aPa", "tct", "sPs", 'a', CraftingItems.ALLOY.getItem(), 't', ItemStacks.tungsteningot, 'c', ItemStacks.condenser, 'P', ItemStacks.basepanel, 's', ItemStacks.steelingot);
		ReactorTiles.SOLAR.addCrafting("sPs", "pEp", "sPs", 'p', ItemStacks.pipe, 'E', ReactorTiles.EXCHANGER.getCraftedProduct(), 'P', ItemStacks.basepanel, 's', ItemStacks.steelingot);

		ReactorTiles.HEATPIPE.addSizedCrafting(6, " NP", "NPN", "PN ", 'N', Blocks.wool, 'P', Items.gold_ingot);
		if (ReikaItemHelper.oreItemExists("ingotCopper"))
			ReactorTiles.HEATPIPE.addSizedOreCrafting(3, " NP", "NPN", "PN ", 'N', Blocks.wool, 'P', "ingotCopper");
	}

	public static void loadCustomRecipeFiles() {
		CustomRecipeList.addFieldLookup("reactorcraft_stack", ReactorStacks.class);
		loadRecipeFile("electrolyzer");
		loadRecipeFile("synthesizer");
	}

	private static void loadRecipeFile(String id) {
		CustomRecipeList crl = new CustomRecipeList(ReactorCraft.instance, id);
		if (crl.load()) {
			for (LuaBlock lb : crl.getEntries()) {
				Exception e = null;
				boolean flag = false;
				String n = lb.getString("type");
				try {
					if (LuaBlock.isErrorCode(n))
						throw new IllegalArgumentException("Custom recipes require a specified name!");
					if (!ReikaStringParser.isValidVariableName(n))
						throw new IllegalArgumentException("Name must be a valid field name in Java syntax! '"+n+"' is not valid!");
					switch(id) {
						case "electrolyzer":
							flag = addCustomElectrolyzerRecipe(n, lb, crl);
							break;
						case "synthesizer":
							flag = addCustomSynthesizerRecipe(n, lb, crl);
							break;
						case "tritizer":
							flag = addCustomTritizerRecipe(n, lb, crl);
							break;
					}
				}
				catch (Exception ex) {
					e = ex;
					flag = false;
				}
				if (flag) {
					ReactorCraft.logger.log("Loaded custom "+id+" recipe '"+n+"'");
				}
				else {
					ReactorCraft.logger.logError("Could not load custom "+id+" recipe '"+n+"'");
					if (e != null)
						e.printStackTrace();
				}
			}
		}
		else {/*
			crl.createFolders();
			crl.addToExample(createLuaBlock(Electrolysis.HEAVYWATER));
			crl.addToExample(createLuaBlock(FluidSynthesis.AMMONIA));
			crl.addToExample(createLuaBlock(Reactions.TRITIUM));
			crl.createExampleFile();*/
		}
	}

	private static boolean addCustomSynthesizerRecipe(String n, LuaBlock lb, CustomRecipeList crl) {
		LuaBlock fluidIn = lb.getChild("fluid_in");
		LuaBlock fluidOut = lb.getChild("fluid_out");
		if (fluidIn == null) {
			throw new IllegalArgumentException("Recipe '"+n+"' missing an input fluid!");
		}
		if (fluidOut == null) {
			throw new IllegalArgumentException("Recipe '"+n+"' missing an output fluid!");
		}
		LuaBlock itemIn1 = lb.getChild("item_in");
		LuaBlock itemIn2 = lb.getChild("item_in_2");
		Fluid in = FluidRegistry.getFluid(fluidIn.getString("type"));
		int amtin = fluidIn.getInt("amount");
		Fluid out = FluidRegistry.getFluid(fluidOut.getString("type"));
		int amtout = fluidOut.getInt("amount");
		FlexibleIngredient item1 = FlexibleIngredient.parseLua(crl, itemIn1, false);
		FlexibleIngredient item2 = FlexibleIngredient.parseLua(crl, itemIn2, false);
		TileEntitySynthesizer.addRecipe(n, in, out, amtin, amtout, lb.getInt("min_temperature"), lb.getInt("base_time"), lb.getInt("temp_curve"), item1, item2);
		return true;
	}

	private static boolean addCustomElectrolyzerRecipe(String n, LuaBlock lb, CustomRecipeList crl) {
		LuaBlock fluidIn = lb.getChild("fluid_in");
		LuaBlock itemIn = lb.getChild("item_in");
		LuaBlock fluidUp = lb.getChild("fluid_out_up");
		LuaBlock fluidDown = lb.getChild("fluid_out_down");
		Fluid in = fluidIn != null ? FluidRegistry.getFluid(fluidIn.getString("type")) : null;
		int amt = fluidIn != null ? fluidIn.getInt("amount") : 0;
		ItemMatch item = null;
		boolean cata = false;
		if (itemIn != null) {
			item = new ItemMatch(crl.parseItemCollection(itemIn.getChild("items").getDataValues(), false));
			cata = itemIn.getBoolean("is_catalyst");
		}
		Fluid out1 = fluidUp != null ? FluidRegistry.getFluid(fluidUp.getString("type")) : null;
		int amt1 = fluidUp != null ? fluidUp.getInt("amount") : 0;
		Fluid out2 = fluidDown != null ? FluidRegistry.getFluid(fluidDown.getString("type")) : null;
		int amt2 = fluidDown != null ? fluidDown.getInt("amount") : 0;
		if (in == null && item == null) {
			throw new IllegalArgumentException("Recipe '"+n+"' has no ingredients!");
		}
		if (out1 == null && out2 == null) {
			throw new IllegalArgumentException("Recipe '"+n+"' has no products!");
		}
		TileEntityElectrolyzer.addRecipe(n, in, amt, item, cata, out1, amt1, out2, amt2, lb.getInt("temperature"));
		return true;
	}

	private static boolean addCustomTritizerRecipe(String n, LuaBlock lb, CustomRecipeList crl) {
		String fluidIn = lb.getString("fluid_in");
		String fluidOut = lb.getString("fluid_out");
		int chance = lb.getInt("interaction_chance");
		int amount = lb.getInt("converted_amount");
		Fluid in = fluidIn != null ? FluidRegistry.getFluid(fluidIn) : null;
		Fluid out = fluidOut != null ? FluidRegistry.getFluid(fluidOut) : null;
		if (in == null)
			throw new IllegalArgumentException("Input fluid '"+fluidIn+"' does not exist!");
		if (out == null)
			throw new IllegalArgumentException("Output fluid '"+fluidOut+"' does not exist!");
		if (amount > TileEntityTritizer.CAPACITY)
			throw new IllegalArgumentException("Fluid amount exceeds tank capacity!");
		TileEntityTritizer.addRecipe(n, in, out, chance, amount);
		return true;
	}

}
