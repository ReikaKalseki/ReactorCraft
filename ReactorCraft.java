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

import java.net.URL;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.API.AcceleratorBlacklist;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;
import Reika.ReactorCraft.Auxiliary.PotionRadiation;
import Reika.ReactorCraft.Auxiliary.ReactorBookTracker;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.ReactorTab;
import Reika.ReactorCraft.Auxiliary.Lua.ReactorLuaMethods;
import Reika.ReactorCraft.Blocks.BlockTritiumLamp.TileEntityTritiumLamp;
import Reika.ReactorCraft.Entities.EntityFusion;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamInjector;
import Reika.ReactorCraft.World.ReactorOreGenerator;
import Reika.ReactorCraft.World.ReactorRetroGen;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.Auxiliary.LockNotification;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod( modid = "ReactorCraft", name="ReactorCraft", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")
public class ReactorCraft extends DragonAPIMod {

	@Instance("ReactorCraft")
	public static ReactorCraft instance = new ReactorCraft();

	public static final ReactorConfig config = new ReactorConfig(instance, ReactorOptions.optionList, null, 1);

	public static final String packetChannel = "ReactorCraftData";

	public static CreativeTabs tabRctr = new ReactorTab(CreativeTabs.getNextID(), "ReactorCraft");

	public static final ArmorMaterial HAZ = EnumHelper.addArmorMaterial("RCHazmat", Integer.MAX_VALUE, new int[]{0,0,0,0}, 0);

	public static ModLogger logger;

	private boolean isLocked = false;

	public static Item[] items = new Item[ReactorItems.itemList.length];
	public static Block[] blocks = new Block[ReactorBlocks.blockList.length];

	public static final Fluid D2O = new Fluid("heavy water").setDensity(1100).setViscosity(1050);
	public static final Fluid HF = new Fluid("hydrofluoric acid").setDensity(-1).setViscosity(10).setGaseous(true);
	public static final Fluid UF6 = new Fluid("uranium hexafluoride").setDensity(15).setViscosity(10).setGaseous(true);

	public static final Fluid NH3 = new Fluid("rc ammonia").setDensity(682).setViscosity(600);
	public static final Fluid NA = new Fluid("rc sodium").setDensity(927).setViscosity(700).setTemperature(1100);
	public static final Fluid CL = new Fluid("rc chlorine").setDensity(320).setViscosity(12).setGaseous(true);
	public static final Fluid O = new Fluid("rc oxygen").setDensity(138).setViscosity(20).setGaseous(true);

	public static final Fluid NH3_lo = new Fluid("lowpammonia").setDensity(200).setViscosity(600);
	public static final Fluid H2O_lo = new Fluid("lowpwater").setDensity(800).setViscosity(800);
	public static final Fluid NA_hot = new Fluid("hotsodium").setDensity(720).setViscosity(650).setTemperature(2000);

	public static final Fluid H2 = new Fluid("rc deuterium").setDensity(-1).setViscosity(10).setGaseous(true);
	public static final Fluid H3 = new Fluid("rc tritium").setDensity(-1).setViscosity(10).setGaseous(true);

	public static final Fluid CO2 = new Fluid("rc co2").setDensity(2).setViscosity(7).setGaseous(true);
	public static final Fluid CO2_hot = new Fluid("rc hot co2").setDensity(1).setViscosity(5).setGaseous(true);

	public static final Fluid PLASMA = new Fluid("fusion plasma").setDensity(-1).setViscosity(100).setGaseous(true).setTemperature(TileEntityFusionHeater.PLASMA_TEMP).setLuminosity(15);

	public static final Fluid CORIUM = new Fluid("corium").setDensity(5000).setViscosity(8000).setTemperature(2173);

	public static PotionRadiation radiation;

	public static Achievement[] achievements;

	public static final CustomStringDamageSource radiationDamage = (CustomStringDamageSource)new CustomStringDamageSource("died of radiation poisoning").setDamageBypassesArmor();
	public static final CustomStringDamageSource fusionDamage = new CustomStringDamageSource("jumped in a Fusion Reactor");

	@SidedProxy(clientSide="Reika.ReactorCraft.ClientProxy", serverSide="Reika.ReactorCraft.CommonProxy")
	public static CommonProxy proxy;

	public final boolean isLocked() {
		return isLocked || RotaryCraft.instance.isLocked();
	}

	private final boolean checkForLock() {
		for (int i = 0; i < ReactorItems.itemList.length; i++) {
			ReactorItems r = ReactorItems.itemList[i];
			if (!r.isDummiedOut()) {
				Item id = r.getItemInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		for (int i = 0; i < ReactorBlocks.blockList.length; i++) {
			ReactorBlocks r = ReactorBlocks.blockList[i];
			if (!r.isDummiedOut()) {
				Block id = r.getBlockInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		return false;
	}

	@Override
	protected HashMap<String, String> getDependencies() {
		HashMap map = new HashMap();
		map.put("RotaryCraft", RotaryCraft.currentVersion);
		return map;
	}

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.verifyVersions();

		MinecraftForge.EVENT_BUS.register(new LiquidHandler());

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			;//MinecraftForge.EVENT_BUS.register(IronFinderOverlay.instance);

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		proxy.registerSounds();

		isLocked = this.checkForLock();
		if (this.isLocked()) {
			ReikaJavaLibrary.pConsole("");
			ReikaJavaLibrary.pConsole("\t========================================= REACTORCRAFT ===============================================");
			ReikaJavaLibrary.pConsole("\tNOTICE: It has been detected that third-party plugins are being used to disable parts of ReactorCraft.");
			ReikaJavaLibrary.pConsole("\tBecause this is frequently done to sell access to mod content, which is against the Terms of Use");
			ReikaJavaLibrary.pConsole("\tof both Mojang and the mod, the mod has been functionally disabled. No damage will occur to worlds,");
			ReikaJavaLibrary.pConsole("\tand all machines (including contents) and items already placed or in inventories will remain so,");
			ReikaJavaLibrary.pConsole("\tbut its machines will not function, recipes will not load, and no renders or textures will be present.");
			ReikaJavaLibrary.pConsole("\tAll other mods in your installation will remain fully functional.");
			ReikaJavaLibrary.pConsole("\tTo regain functionality, unban the ReactorCraft content, and then reload the game. All functionality");
			ReikaJavaLibrary.pConsole("\twill be restored. You may contact Reika for further information on his forum thread.");
			ReikaJavaLibrary.pConsole("\t=====================================================================================================");
			ReikaJavaLibrary.pConsole("");
		}

		logger = new ModLogger(instance, false);

		this.addLiquids();
		this.addBlocks();
		this.addItems();
		this.addLiquidContainers();
		this.registerOres();
		ReactorTiles.loadMappings();
		ReactorItems.loadMappings();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ReactorPacketCore());

		//if (ReactorBlocks.CORIUMSTILL.getBlock() != ReactorBlocks.CORIUMFLOWING.getBlock()+1)
		//	throw new InstallationException(instance, "The still corium block ID needs to be exactly one more than the flowing ID!");

		if (ConfigRegistry.ACHIEVEMENTS.getState()) {
			achievements = new Achievement[ReactorAchievements.list.length];
			ReactorAchievements.registerAchievements();
		}

		PotionCollisionTracker.instance.addPotionID(instance, config.getRadiationPotionID(), PotionRadiation.class);
		radiation = (PotionRadiation)new PotionRadiation(config.getRadiationPotionID(), true).setPotionName("Radiation Sickness");

		this.basicSetup(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		if (this.isLocked() && !RotaryCraft.instance.isLocked())
			PlayerHandler.instance.registerTracker(LockNotification.instance);
		if (!this.isLocked()) {
			proxy.registerRenderers();
			ReactorRecipes.addRecipes();
		}
		this.addEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ReactorGuiHandler());
		GameRegistry.registerWorldGenerator(new ReactorOreGenerator(), 0);
		if (ReactorOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new ReactorRetroGen());
			//Set state back
		}

		ReactorDescriptions.loadData();

		ReikaMystcraftHelper.disableFluidPage("fusion plasma");
		ReikaMystcraftHelper.disableFluidPage("rc deuterium");
		ReikaMystcraftHelper.disableFluidPage("rc tritium");
		ReikaMystcraftHelper.disableFluidPage("hydrofluoric acid");
		ReikaMystcraftHelper.disableFluidPage("uranium hexafluoride");
		ReikaMystcraftHelper.disableFluidPage("rc sodium");
		ReikaMystcraftHelper.disableFluidPage("rc chlorine");
		ReikaMystcraftHelper.disableFluidPage("rc oxygen");
		ReikaMystcraftHelper.disableFluidPage("lowpammonia");
		ReikaMystcraftHelper.disableFluidPage("lowpwater");
		ReikaMystcraftHelper.disableFluidPage("hotsodium");

		for (int i = 0; i < MatBlocks.matList.length; i++) {
			ItemStack is = MatBlocks.matList[i].getStackOf();
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			ItemStack is = FluoriteTypes.colorList[i].getStorageBlock();
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}

		//TickRegistry.instance.registerTickHandler(new VolcanicGasController(), Side.SERVER);

		if (!this.isLocked())
			IntegrityChecker.instance.addMod(instance, ReactorBlocks.blockList, ReactorItems.itemList);

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.bedrock);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.gold_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.hardened_clay);

		if (ConfigRegistry.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ReactorBookTracker());

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.CHROMATICRAFT, "Dense pitchblende generation in its biomes");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense pitchblende generation in its biomes");
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		if (!this.isLocked())
			ReactorRecipes.addModInterface();

		//for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
		//	FluoriteTypes fl = FluoriteTypes.colorList[i];
		//	BlockColorMapper.instance.addModBlockColor(ReactorBlocks.FLUORITEORE.getBlock(), i, fl.red, fl.green, fl.blue);
		//}
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			//BlockColorMapper.instance.addModBlockColor(r.getBlock(), r.getBlockMetadata(), ReikaColorAPI.RGBtoHex(200, 200, 200));
			BlockColorInterface.addGPRBlockColor(r.getBlock(), r.getBlockMetadata(), 200, 200, 200);
		}

		ReikaJavaLibrary.initClass(ReactorLuaMethods.class);

		if (ModList.CHROMATICRAFT.isLoaded()) {
			for (int i = 0; i < ReactorTiles.TEList.length; i++) {
				ReactorTiles m = ReactorTiles.TEList[i];
				if (m != ReactorTiles.PROCESSOR)
					AcceleratorBlacklist.addBlacklist(m.getTEClass(), m.getName(), BlacklistReason.EXPLOIT);
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < ReactorOres.oreList.length; i++) {
				ReactorOres ore = ReactorOres.oreList[i];
				ItemStack block = ore.getOreBlock();
				ItemStack drop = ore.getProduct();
				//ReikaThaumHelper.addAspects(block, Aspect.STONE, 1);
			}

			ReikaThaumHelper.addAspects(ReactorOres.CADMIUM.getOreBlock(), Aspect.METAL, 1);
			ReikaThaumHelper.addAspects(ReactorOres.INDIUM.getOreBlock(), Aspect.METAL, 1, Aspect.GREED, 1);
			ReikaThaumHelper.addAspects(ReactorOres.CALCITE.getOreBlock(), Aspect.CRYSTAL, 1);
			ReikaThaumHelper.addAspects(ReactorOres.MAGNETITE.getOreBlock(), Aspect.METAL, 1, Aspect.CRYSTAL, 1, Aspect.AURA, 1);
			ReikaThaumHelper.addAspects(ReactorOres.AMMONIUM.getOreBlock(), Aspect.FIRE, 1);

			ReikaThaumHelper.addAspects(ReactorOres.MAGNETITE.getProduct(), Aspect.AURA, 2, Aspect.METAL, 2, Aspect.CRYSTAL, 2);
			ReikaThaumHelper.addAspects(ReactorOres.CALCITE.getProduct(), Aspect.CRYSTAL, 2);
			ReikaThaumHelper.addAspects(ReactorOres.AMMONIUM.getProduct(), Aspect.FIRE, 2);

			for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
				FluoriteTypes f = FluoriteTypes.colorList[i];
				ItemStack block = f.getOreBlock();
				ItemStack drop = f.getItem();
				ItemStack ore = f.getOreBlock();
				ReikaThaumHelper.addAspects(ore, Aspect.CRYSTAL, 1);
				ReikaThaumHelper.addAspects(ore, Aspect.SENSES, 1);

				//ReikaThaumHelper.addAspects(block, Aspect.STONE, 1);
				ReikaThaumHelper.addAspects(block, Aspect.CRYSTAL, 1);
				ReikaThaumHelper.addAspects(block, Aspect.SENSES, 1);

				ReikaThaumHelper.addAspects(drop, Aspect.CRYSTAL, 2);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (!this.isLocked())
			setupLiquidIcons(event);
	}

	@SideOnly(Side.CLIENT)
	private static void setupLiquidIcons(TextureStitchEvent.Pre event) {
		logger.log("Loading Liquid Icons");

		if (event.map.getTextureType() == 0) {
			IIcon d2o = event.map.registerIcon("ReactorCraft:heavywater");
			IIcon hf = event.map.registerIcon("ReactorCraft:hf");
			IIcon uf6 = event.map.registerIcon("ReactorCraft:uf6");

			IIcon nh3 = event.map.registerIcon("ReactorCraft:ammonia");
			IIcon na = event.map.registerIcon("ReactorCraft:sodium");
			IIcon nahot = event.map.registerIcon("ReactorCraft:sodiumhot");
			IIcon cl = event.map.registerIcon("ReactorCraft:chlorine");
			IIcon o = event.map.registerIcon("ReactorCraft:oxygen");

			IIcon h2 = event.map.registerIcon("ReactorCraft:deuterium");
			IIcon h3 = event.map.registerIcon("ReactorCraft:tritium");
			IIcon plasma = event.map.registerIcon("ReactorCraft:plasma");

			IIcon co2 = event.map.registerIcon("ReactorCraft:co2");

			IIcon corium = event.map.registerIcon("ReactorCraft:slag_flow");
			IIcon corium2 = event.map.registerIcon("ReactorCraft:slag_flow");

			D2O.setIcons(d2o);
			HF.setIcons(hf);
			UF6.setIcons(uf6);

			NH3.setIcons(nh3);
			NA.setIcons(na);
			CL.setIcons(cl);
			O.setIcons(o);

			H2.setIcons(h2);
			H3.setIcons(h3);
			PLASMA.setIcons(plasma);

			NH3_lo.setIcons(nh3);
			H2O_lo.setIcons(Blocks.water.getIcon(1, 0));
			NA_hot.setIcons(nahot);

			CO2.setIcons(co2);
			CO2_hot.setIcons(co2);

			CORIUM.setIcons(corium, corium2);
		}
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ReactorItems.itemList, items);
	}

	private static void addEntities() {
		EntityRegistry.registerModEntity(EntityNeutron.class, "Neutron", EntityRegistry.findGlobalUniqueEntityId(), instance, 64, 20, true);
		EntityRegistry.registerModEntity(EntityRadiation.class, "Radiation", EntityRegistry.findGlobalUniqueEntityId()+1, instance, 64, 20, true);
		EntityRegistry.registerModEntity(EntityPlasma.class, "Plasma", EntityRegistry.findGlobalUniqueEntityId()+2, instance, 64, 20, true);
		EntityRegistry.registerModEntity(EntityFusion.class, "Fusion", EntityRegistry.findGlobalUniqueEntityId()+3, instance, 64, 20, true);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ReactorBlocks.blockList, blocks);
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			GameRegistry.registerTileEntity(ReactorTiles.TEList[i].getTEClass(), "Reactor"+ReactorTiles.TEList[i].getName());
			ReikaJavaLibrary.initClass(ReactorTiles.TEList[i].getTEClass());
		}
		GameRegistry.registerTileEntity(TileEntitySteamInjector.class, "ReactorSteamInjector");
		GameRegistry.registerTileEntity(TileEntityTritiumLamp.class, "ReactorTritiumLamp");
	}

	private static void addLiquids() {
		FluidRegistry.registerFluid(D2O);
		FluidRegistry.registerFluid(HF);
		FluidRegistry.registerFluid(UF6);

		FluidRegistry.registerFluid(NH3);
		FluidRegistry.registerFluid(NA);
		FluidRegistry.registerFluid(CL);
		FluidRegistry.registerFluid(O);

		FluidRegistry.registerFluid(H2);
		FluidRegistry.registerFluid(H3);
		FluidRegistry.registerFluid(PLASMA);

		FluidRegistry.registerFluid(NH3_lo);
		FluidRegistry.registerFluid(H2O_lo);
		FluidRegistry.registerFluid(NA_hot);

		FluidRegistry.registerFluid(CO2);
		FluidRegistry.registerFluid(CO2_hot);

		FluidRegistry.registerFluid(CORIUM);
	}

	private static void addLiquidContainers() {
		logger.log("Loading And Registering Liquids");

		FluidContainerRegistry.registerFluidContainer(new FluidStack(D2O, FluidContainerRegistry.BUCKET_VOLUME), ReactorItems.BUCKET.getStackOfMetadata(0), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(HF, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hfcan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(UF6, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.uf6can, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(NH3, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.nh3can, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(NA, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.nacan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(CL, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.clcan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(O, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.ocan, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(H2, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.h2can, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(H3, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.h3can, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(CO2, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.co2can, ReactorStacks.emptycan);
	}

	public static final boolean hasGui(World world, int x, int y, int z, EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			Object GUI = ReactorGuiHandler.instance.getClientGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Object GUI = ReactorGuiHandler.instance.getServerGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		return false;
	}

	private static void registerOres() {
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			if (ore != ReactorOres.FLUORITE) {
				OreDictionary.registerOre(ore.getDictionaryName(), ore.getOreBlock());
				OreDictionary.registerOre(ore.getProductDictionaryName(), ore.getProduct());
				ReactorBlocks.ORE.getBlockInstance().setHarvestLevel("pickaxe", ore.harvestLevel, ore.getBlockMetadata());
			}
		}
		Block b = ReactorBlocks.FLUORITEORE.getBlockInstance();
		b.setHarvestLevel("pickaxe", ReactorOres.FLUORITE.harvestLevel);
		OreDictionary.registerOre("dustQuicklime", ReactorStacks.lime.copy());

		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack is = new ItemStack(ReactorBlocks.FLUORITEORE.getBlockInstance(), 1, i);
			//ReikaOreHelper.addOreForReference(is);
			OreDictionary.registerOre(ReactorOres.FLUORITE.getDictionaryName(), is);
			OreDictionary.registerOre(ReactorOres.FLUORITE.getProductDictionaryName(), fl.getItem());
		}
	}

	@Override
	public String getDisplayName() {
		return "ReactorCraft";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public String getWiki() {
		return RotaryCraft.instance.getWiki();
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}
