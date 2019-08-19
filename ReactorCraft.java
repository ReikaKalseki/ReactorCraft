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

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import net.bdew.gendustry.api.GendustryAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.API.AcceleratorBlacklist;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.ChromatiCraft.API.CrystalPotionInterface;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.IDCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.ModLockController;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Base.EnumOreBlock;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ItemStackRepository;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.FrameBlacklist.FrameUsageEvent;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveFluidRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveItemRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.TimeTorchHelper;
import Reika.ReactorCraft.Auxiliary.ClearSteamCommand;
import Reika.ReactorCraft.Auxiliary.IronFinderOverlay;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Auxiliary.PoisonGasDamage;
import Reika.ReactorCraft.Auxiliary.PotionRadiation;
import Reika.ReactorCraft.Auxiliary.RadiationDamage;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationFluidEffect;
import Reika.ReactorCraft.Auxiliary.ReactorBlock;
import Reika.ReactorCraft.Auxiliary.ReactorBookTracker;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.ReactorTab;
import Reika.ReactorCraft.Auxiliary.Lua.ReactorLuaMethods;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Blocks.BlockTritiumLamp.TileEntityTritiumLamp;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorEntities;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamInjector;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;
import Reika.ReactorCraft.World.ReactorOreGenerator;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.Auxiliary.LockNotification;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.TileEntities.Storage.TileEntityReservoir;

import WayofTime.alchemicalWizardry.api.event.TeleposeEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

@Mod( modid = "ReactorCraft", name="ReactorCraft", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")
public class ReactorCraft extends DragonAPIMod {

	@Instance("ReactorCraft")
	public static ReactorCraft instance = new ReactorCraft();

	public static final ReactorConfig config = new ReactorConfig(instance, ReactorOptions.optionList, null);

	public static final String packetChannel = "ReactorCraftData";

	public static ReactorTab tabRctr = new ReactorTab("ReactorCraft");
	public static ReactorTab tabRctrItems = new ReactorTab("ReactorCraft Items");
	public static ReactorTab tabRctrMultis = new ReactorTab("ReactorCraft Components");

	public static final ArmorMaterial HAZ = EnumHelper.addArmorMaterial("RCHazmat", 0/*Integer.MAX_VALUE*/, new int[]{0,0,0,0}, 0);

	public static ModLogger logger;

	public static Item[] items = new Item[ReactorItems.itemList.length];
	public static Block[] blocks = new Block[ReactorBlocks.blockList.length];

	public static final Fluid D2O = new Fluid("rc heavy water").setDensity(1100).setViscosity(1050);
	public static final Fluid HF = new Fluid("rc hydrofluoric acid").setDensity(115).setViscosity(10).setGaseous(true);
	public static final Fluid UF6 = new Fluid("rc uranium hexafluoride").setDensity(15).setViscosity(10).setGaseous(true);

	public static final Fluid NH3 = new Fluid("rc ammonia").setDensity(682).setViscosity(600);
	public static final Fluid NA = new Fluid("rc sodium").setDensity(927).setViscosity(700).setTemperature(1100);
	public static final Fluid CL = new Fluid("rc chlorine").setDensity(320).setViscosity(12).setGaseous(true);
	public static final Fluid O = new Fluid("rc oxygen").setDensity(138).setViscosity(20).setGaseous(true);
	public static final Fluid Oliq = new Fluid("rc liquid oxygen").setDensity(1141).setViscosity(195).setGaseous(false).setTemperature(90);

	public static final Fluid NH3_lo = new Fluid("rc lowpammonia").setDensity(200).setViscosity(600);
	public static final Fluid H2O_lo = new Fluid("rc lowpwater").setDensity(800).setViscosity(800);
	public static final Fluid NA_hot = new Fluid("rc hotsodium").setDensity(720).setViscosity(650).setTemperature(2000).setLuminosity(8);

	public static final Fluid H2 = new Fluid("rc deuterium").setDensity(-1).setViscosity(10).setGaseous(true);
	public static final Fluid H3 = new Fluid("rc tritium").setDensity(-1).setViscosity(10).setGaseous(true);

	public static final Fluid CO2 = new Fluid("rc co2").setDensity(2).setViscosity(7).setGaseous(true);
	public static final Fluid CO2_hot = new Fluid("rc hot co2").setDensity(1).setViscosity(5).setGaseous(true).setLuminosity(2);

	public static final Fluid PLASMA = new Fluid("rc fusion plasma").setDensity(-1).setViscosity(100).setGaseous(true).setTemperature(TileEntityFusionHeater.PLASMA_TEMP).setLuminosity(15);

	public static final Fluid CORIUM = new Fluid("rc corium").setDensity(5000).setViscosity(8000).setTemperature(2173);
	public static final Fluid WASTE = new Fluid("rc nuclear waste").setDensity(4000).setViscosity(12000).setTemperature(800);

	public static final Fluid LI = new Fluid("rc lithium").setDensity(516).setViscosity(645).setTemperature(454).setLuminosity(6);

	public static final Fluid LIFBe = new Fluid("rc lifbe").setDensity(6300).setViscosity(800*3).setTemperature(773);
	public static final Fluid LIFBe_fuel = new Fluid("rc lifbe fuel").setDensity(6750).setViscosity(850*3).setTemperature(773);
	public static final Fluid LIFBe_hot = new Fluid("rc hot lifbe").setDensity(6000).setViscosity(800*3).setTemperature(1273).setLuminosity(8);

	public static PotionRadiation radiation;
	public static IIcon solarFlare;

	public static Achievement[] achievements;

	public static final RadiationDamage radiationDamage = new RadiationDamage();
	public static final CustomStringDamageSource fusionDamage = new CustomStringDamageSource("jumped in a Fusion Reactor");
	public static final PoisonGasDamage gasDamage = new PoisonGasDamage();

	@SidedProxy(clientSide="Reika.ReactorCraft.ClientProxy", serverSide="Reika.ReactorCraft.CommonProxy")
	public static CommonProxy proxy;

	public final boolean isLocked() {
		return !ModLockController.instance.verify(this) || RotaryCraft.instance.isLocked();
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
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();

		MinecraftForge.EVENT_BUS.register(new LiquidHandler());

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(IronFinderOverlay.instance);

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		proxy.registerSounds();
		ModLockController.instance.registerMod(this);

		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		this.addLiquids();
		this.addBlocks();
		this.addItems();
		this.addLiquidContainers();
		this.registerOres();
		ReactorTiles.loadMappings();
		ReactorItems.loadMappings();
		ReactorBlocks.loadMappings();

		CORIUM.setBlock(ReactorBlocks.CORIUMFLOWING.getBlockInstance());
		HF.setBlock(ReactorBlocks.HF.getBlockInstance());
		CL.setBlock(ReactorBlocks.CHLORINE.getBlockInstance());

		tabRctr.setIcon(ReactorTiles.MAGNET.getCraftedProduct());
		tabRctrItems.setIcon(ReactorItems.WASTE.getStackOf());
		tabRctrMultis.setIcon(ReactorBlocks.SOLENOIDMULTI.getStackOfMetadata(2)); //central magnet
		CreativeTabSorter.instance.registerCreativeTabAfter(tabRctr, RotaryCraft.tabRotary);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabRctrMultis, tabRctr);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabRctrItems, tabRctr);

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ReactorPacketCore());

		//if (ReactorBlocks.CORIUMSTILL.getBlock() != ReactorBlocks.CORIUMFLOWING.getBlock()+1)
		//	throw new InstallationException(instance, "The still corium block ID needs to be exactly one more than the flowing ID!");

		if (ConfigRegistry.ACHIEVEMENTS.getState()) {
			achievements = new Achievement[ReactorAchievements.list.length];
			ReactorAchievements.registerAchievements();
		}

		IDCollisionTracker.instance.addPotionID(instance, config.getRadiationPotionID(), PotionRadiation.class);
		radiation = (PotionRadiation)new PotionRadiation(config.getRadiationPotionID()).setPotionName("Radiation Sickness");

		FMLInterModComms.sendMessage("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

		if (ModList.GENDUSTRY.isLoaded()) {
			try {
				GendustryAPI.Registries.getMutagenRegistry().add(ReactorItems.WASTE.getItemInstance(), 10000);
				GendustryAPI.Registries.getMutagenRegistry().add(ReactorItems.FUEL.getItemInstance(), 2000);
				GendustryAPI.Registries.getMutagenRegistry().add(ReactorItems.PLUTONIUM.getItemInstance(), 5000);
				GendustryAPI.Registries.getMutagenRegistry().add(ReactorItems.PELLET.getItemInstance(), 1200);
				//GendustryAPI.Registries.getMutagenRegistry().add(ReactorItems.THORIUM.getItemInstance(), 2000);
			}
			catch (IncompatibleClassChangeError e) {
				logger.logError("Could not add Gendustry integration. Check your versions; if you are up-to-date with both mods, notify Reika. "+e.toString());
			}
			catch (Exception e) {
				logger.logError("Could not add Gendustry integration. Check your versions; if you are up-to-date with both mods, notify Reika. "+e.toString());
			}
		}

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);

		if (this.checkForLock()) {
			ModLockController.instance.unverify(this);
		}
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

		if (this.isLocked() && !RotaryCraft.instance.isLocked())
			PlayerHandler.instance.registerTracker(LockNotification.instance);
		if (!this.isLocked()) {
			proxy.registerRenderers();
			ReactorRecipes.addRecipes();
		}
		this.addEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ReactorGuiHandler());
		RetroGenController.instance.addHybridGenerator(ReactorOreGenerator.instance, 0, ReactorOptions.RETROGEN.getState());

		ItemStackRepository.instance.registerClass(this, ReactorStacks.class);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReactorDescriptions.loadData();

		for (int i = 0; i < MatBlocks.matList.length; i++) {
			ItemStack is = MatBlocks.matList[i].getStackOf();
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}
		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			ItemStack is = FluoriteTypes.colorList[i].getStorageBlock();
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}

		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes f = FluoriteTypes.colorList[i];
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("value", f.getCorrespondingDyeType().getWoolMeta());
			f.getItem().writeToNBT(tag);
			FMLInterModComms.sendMessage(ModList.MINEFACTORY.modLabel, "addLaserPreferredOre", tag);
		}

		FMLInterModComms.sendMessage("Randomod", "blacklist", this.getModContainer().getModId());

		PackModificationTracker.instance.addMod(this, config);

		//TickRegistry.instance.registerTickHandler(new VolcanicGasController(), Side.SERVER);

		if (!this.isLocked())
			IntegrityChecker.instance.addMod(instance, ReactorBlocks.blockList, ReactorItems.itemList);

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.bedrock);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.gold_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.hardened_clay);

		if (ConfigRegistry.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ReactorBookTracker());

		//ReikaEEHelper.blacklistRegistry(ReactorBlocks.blockList);
		//ReikaEEHelper.blacklistRegistry(ReactorItems.itemList);

		ReikaEEHelper.blacklistEntry(ReactorItems.FUEL);
		ReikaEEHelper.blacklistEntry(ReactorItems.BREEDERFUEL);
		ReikaEEHelper.blacklistEntry(ReactorItems.PELLET);
		ReikaEEHelper.blacklistEntry(ReactorItems.PLUTONIUM);
		//ReikaEEHelper.blacklistEntry(ReactorItems.THORIUM);

		if (ModList.APPENG.isLoaded()) {
			MESystemReader.registerMESystemEffect(RadiationEffects.instance.createMESystemEffect());
		}

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.CHROMATICRAFT, "Dense pitchblende generation in its biomes");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense pitchblende generation in its biomes");

		SensitiveItemRegistry.instance.registerItem(this, ReactorItems.FUEL.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorItems.BREEDERFUEL.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorItems.PLUTONIUM.getItemInstance(), true);
		//SensitiveItemRegistry.instance.registerItem(this, ReactorItems.THORIUM.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorItems.PELLET.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorStacks.fueldust, true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorStacks.thordust, true);
		SensitiveItemRegistry.instance.registerItem(this, ReactorItems.CRAFTING.getItemInstance(), false);

		SensitiveFluidRegistry.instance.registerFluid("rc fusion plasma");
		SensitiveFluidRegistry.instance.registerFluid("rc deuterium");
		SensitiveFluidRegistry.instance.registerFluid("rc tritium");
		SensitiveFluidRegistry.instance.registerFluid("rc hydrofluoric acid");
		SensitiveFluidRegistry.instance.registerFluid("rc uranium hexafluoride");
		SensitiveFluidRegistry.instance.registerFluid("rc sodium");
		SensitiveFluidRegistry.instance.registerFluid("rc chlorine");
		SensitiveFluidRegistry.instance.registerFluid("rc oxygen");
		SensitiveFluidRegistry.instance.registerFluid("rc lowpammonia");
		SensitiveFluidRegistry.instance.registerFluid("rc lowpwater");
		SensitiveFluidRegistry.instance.registerFluid("rc hotsodium");
		SensitiveFluidRegistry.instance.registerFluid("rc co2");
		SensitiveFluidRegistry.instance.registerFluid("rc hot co2");
		SensitiveFluidRegistry.instance.registerFluid("rc corium");
		SensitiveFluidRegistry.instance.registerFluid("rc lithium");
		SensitiveFluidRegistry.instance.registerFluid("rc lifbe");
		SensitiveFluidRegistry.instance.registerFluid("rc lifbe fuel");
		SensitiveFluidRegistry.instance.registerFluid("rc hot lifbe");

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		if (!this.isLocked()) {
			ReactorRecipes.addModInterface();
			ReactorRecipes.loadCustomRecipeFiles();
		}

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

		proxy.loadDonatorRender();

		TileEntityReservoir.addFluidEffect("rc nuclear waste", new RadiationFluidEffect());

		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles m = ReactorTiles.TEList[i];
			if (m != ReactorTiles.PROCESSOR) {
				if (ModList.CHROMATICRAFT.isLoaded()) {
					AcceleratorBlacklist.addBlacklist(m.getTEClass(), m.getName(), BlacklistReason.EXPLOIT);
				}
				TimeTorchHelper.blacklistTileEntity(m.getTEClass());
			}
		}

		if (ModList.CHROMATICRAFT.isLoaded()) {
			CrystalPotionInterface.addBadPotionForIgnore(radiation);
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

		this.finishTiming();
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new ClearSteamCommand());
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
			IIcon d2o = event.map.registerIcon("ReactorCraft:fluid/heavywater");
			IIcon hf = event.map.registerIcon("ReactorCraft:fluid/hf");
			IIcon uf6 = event.map.registerIcon("ReactorCraft:fluid/uf6");

			IIcon nh3 = event.map.registerIcon("ReactorCraft:fluid/ammonia");
			IIcon na = event.map.registerIcon("ReactorCraft:fluid/sodium");
			IIcon nahot = event.map.registerIcon("ReactorCraft:fluid/sodiumhot");
			IIcon cl = event.map.registerIcon("ReactorCraft:fluid/chlorine");
			IIcon o = event.map.registerIcon("ReactorCraft:fluid/oxygen");

			IIcon h2 = event.map.registerIcon("ReactorCraft:fluid/deuterium");
			IIcon h3 = event.map.registerIcon("ReactorCraft:fluid/tritium");
			IIcon plasma = event.map.registerIcon("ReactorCraft:fluid/plasma");

			IIcon co2 = event.map.registerIcon("ReactorCraft:fluid/co2");

			IIcon corium = event.map.registerIcon("ReactorCraft:fluid/slag_flow");
			IIcon corium2 = event.map.registerIcon("ReactorCraft:fluid/slag_flow");

			IIcon li = event.map.registerIcon("ReactorCraft:fluid/lithium");

			IIcon lifbe = event.map.registerIcon("ReactorCraft:fluid/lifbe");
			IIcon lifbe_fuel = event.map.registerIcon("ReactorCraft:fluid/lifbe_fuel");
			IIcon lifbe_hot = event.map.registerIcon("ReactorCraft:fluid/lifbe_hot");

			D2O.setIcons(d2o);
			HF.setIcons(hf);
			UF6.setIcons(uf6);

			NH3.setIcons(nh3);
			NA.setIcons(na);
			CL.setIcons(cl);
			O.setIcons(o);
			Oliq.setIcons(o);

			H2.setIcons(h2);
			H3.setIcons(h3);
			PLASMA.setIcons(plasma);

			NH3_lo.setIcons(nh3);
			H2O_lo.setIcons(Blocks.water.getIcon(1, 0));
			NA_hot.setIcons(nahot);

			CO2.setIcons(co2);
			CO2_hot.setIcons(co2);

			CORIUM.setIcons(corium, corium2);
			WASTE.setIcons(corium, corium2);

			LI.setIcons(li);

			LIFBe.setIcons(lifbe);
			LIFBe_fuel.setIcons(lifbe_fuel);
			LIFBe_hot.setIcons(lifbe_hot);

			solarFlare = event.map.registerIcon("ReactorCraft:solarflare");
		}
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ReactorItems.itemList, items);
	}

	private static void addEntities() {
		ReikaRegistryHelper.registerModEntities(instance, ReactorEntities.entityList);
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
		logger.log("Loading And Registering Liquids");
		FluidRegistry.registerFluid(D2O);
		FluidRegistry.registerFluid(HF);
		FluidRegistry.registerFluid(UF6);

		FluidRegistry.registerFluid(NH3);
		FluidRegistry.registerFluid(NA);
		FluidRegistry.registerFluid(CL);
		FluidRegistry.registerFluid(O);
		FluidRegistry.registerFluid(Oliq);

		FluidRegistry.registerFluid(H2);
		FluidRegistry.registerFluid(H3);
		FluidRegistry.registerFluid(PLASMA);

		FluidRegistry.registerFluid(NH3_lo);
		FluidRegistry.registerFluid(H2O_lo);
		FluidRegistry.registerFluid(NA_hot);

		FluidRegistry.registerFluid(CO2);
		FluidRegistry.registerFluid(CO2_hot);

		FluidRegistry.registerFluid(CORIUM);
		FluidRegistry.registerFluid(WASTE);

		FluidRegistry.registerFluid(LI);

		FluidRegistry.registerFluid(LIFBe);
		FluidRegistry.registerFluid(LIFBe_hot);
		FluidRegistry.registerFluid(LIFBe_fuel);
	}

	private static void addLiquidContainers() {
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

		FluidContainerRegistry.registerFluidContainer(new FluidStack(CO2_hot, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hotco2can, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(NA_hot, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hotnacan, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(LI, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.lican, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(LIFBe, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.lifbecan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(LIFBe_hot, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hotlifbecan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(LIFBe_fuel, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.lifbefuelcan, ReactorStacks.emptycan);
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
			}
		}
		((EnumOreBlock)ReactorBlocks.ORE.getBlockInstance()).register();
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

	@SubscribeEvent
	public void cancelFramez(FrameUsageEvent evt) {
		if (!this.isMovable(evt.tile)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	@ClassDependent("WayofTime.alchemicalWizardry.api.event.TeleposeEvent")
	public void noTelepose(TeleposeEvent evt) {
		if (!this.isMovable(evt.getInitialTile()) || !this.isMovable(evt.getFinalTile()))
			evt.setCanceled(true);
	}

	@SubscribeEvent
	@ModDependent(ModList.IC2)
	public void radiationProtection(LivingHurtEvent evt) {
		if (evt.source.damageType.contains("radiation")) {
			if (RadiationEffects.instance.hasHazmatSuit(evt.entityLiving)) {
				evt.setCanceled(true);
			}
		}
	}

	private boolean isMovable(TileEntity te) {
		if (te instanceof ReactorBlock)
			return false;
		if (te instanceof MultiBlockTile)
			return false;
		if (te instanceof TileEntityReactorPiping)
			return false;
		if (te instanceof TileEntitySteamLine)
			return false;
		return true;
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

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

}
