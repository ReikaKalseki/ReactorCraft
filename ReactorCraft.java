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

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.RetroGenController;
import Reika.DragonAPI.Auxiliary.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.ReikaMystcraftHelper;
import Reika.ReactorCraft.Auxiliary.PotionRadiation;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.ReactorTab;
import Reika.ReactorCraft.Entities.EntityFusion;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.World.ReactorOreGenerator;
import Reika.ReactorCraft.World.ReactorRetroGen;
import Reika.RotaryCraft.API.BlockColorInterface;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod( modid = "ReactorCraft", name="ReactorCraft", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ReactorCraftData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ReactorCraftData" }, packetHandler = ServerPackets.class))
public class ReactorCraft extends DragonAPIMod {

	@Instance("ReactorCraft")
	public static ReactorCraft instance = new ReactorCraft();

	public static final ReactorConfig config = new ReactorConfig(instance, ReactorOptions.optionList, ReactorBlocks.blockList, ReactorItems.itemList, null, 1);

	public static final String packetChannel = "ReactorCraftData";

	public static CreativeTabs tabRctr = new ReactorTab(CreativeTabs.getNextID(), "ReactorCraft");

	public static ModLogger logger;

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
	public static final Fluid NA_hot = new Fluid("hotsodium").setDensity(720).setViscosity(750).setTemperature(2000);

	public static final Fluid H2 = new Fluid("rc deuterium").setDensity(-1).setViscosity(10).setGaseous(true);
	public static final Fluid H3 = new Fluid("rc tritium").setDensity(-1).setViscosity(10).setGaseous(true);

	public static final Fluid He = new Fluid("rc helium").setDensity(-1).setViscosity(6).setGaseous(true);
	public static final Fluid He_hot = new Fluid("rc hot helium").setDensity(-1).setViscosity(6).setGaseous(true);

	public static final Fluid PLASMA = new Fluid("fusion plasma").setDensity(-1).setViscosity(100).setGaseous(true).setTemperature(TileEntityFusionHeater.PLASMA_TEMP).setLuminosity(15);

	public static PotionRadiation radiation;

	public static final CustomStringDamageSource radiationDamage = (CustomStringDamageSource)new CustomStringDamageSource("died of Radiation Poisoning").setDamageBypassesArmor();
	public static final CustomStringDamageSource fusionDamage = new CustomStringDamageSource("jumped in a Fusion Reactor");

	@SidedProxy(clientSide="Reika.ReactorCraft.ClientProxy", serverSide="Reika.ReactorCraft.CommonProxy")
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {

		if (!ModList.ROTARYCRAFT.isLoaded()) {
			throw new InstallationException(instance, "ReactorCraft requires RotaryCraft!");
		}

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new LiquidHandler());

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, ReactorOptions.LOGLOADING.getState(), ReactorOptions.DEBUGMODE.getState(), false);
		proxy.registerSounds();

		this.addBlocks();
		this.addItems();
		this.addLiquids();
		this.registerOres();
		ReactorTiles.loadMappings();

		PotionCollisionTracker.instance.addPotionID(instance, config.getRadiationPotionID(), PotionRadiation.class);
		radiation = (PotionRadiation)new PotionRadiation(config.getRadiationPotionID(), true).setPotionName("Radiation Sickness");

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		ReactorRecipes.addRecipes();
		this.addEntities();
		NetworkRegistry.instance().registerGuiHandler(instance, new ReactorGuiHandler());
		GameRegistry.registerWorldGenerator(new ReactorOreGenerator());
		if (ReactorOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new ReactorRetroGen());
			//Set state back
		}

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

		//TickRegistry.registerTickHandler(new VolcanicGasController(), Side.SERVER);

		IntegrityChecker.instance.addMod(instance, ReactorBlocks.blockList, ReactorItems.itemList);

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.bedrock);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.blockGold);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.hardenedClay);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		ReactorRecipes.addModInterface();

		//for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
		//	FluoriteTypes fl = FluoriteTypes.colorList[i];
		//	BlockColorMapper.instance.addModBlockColor(ReactorBlocks.FLUORITEORE.getBlockID(), i, fl.red, fl.green, fl.blue);
		//}
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			//BlockColorMapper.instance.addModBlockColor(r.getBlockID(), r.getBlockMetadata(), ReikaColorAPI.RGBtoHex(200, 200, 200));
			BlockColorInterface.addGPRBlockColor(r.getBlockID(), r.getBlockMetadata(), 200, 200, 200);
		}
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		setupLiquidIcons(event);
	}

	@SideOnly(Side.CLIENT)
	private static void setupLiquidIcons(TextureStitchEvent.Pre event) {
		logger.log("Loading Liquid Icons");

		if (event.map.textureType == 0) {
			Icon d2o = event.map.registerIcon("ReactorCraft:heavywater");
			Icon hf = event.map.registerIcon("ReactorCraft:hf");
			Icon uf6 = event.map.registerIcon("ReactorCraft:uf6");

			Icon nh3 = event.map.registerIcon("ReactorCraft:ammonia");
			Icon na = event.map.registerIcon("ReactorCraft:sodium");
			Icon nahot = event.map.registerIcon("ReactorCraft:sodiumhot");
			Icon cl = event.map.registerIcon("ReactorCraft:chlorine");
			Icon o = event.map.registerIcon("ReactorCraft:oxygen");

			Icon h2 = event.map.registerIcon("ReactorCraft:deuterium");
			Icon h3 = event.map.registerIcon("ReactorCraft:tritium");
			Icon plasma = event.map.registerIcon("ReactorCraft:plasma");

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
			H2O_lo.setIcons(Block.waterStill.getIcon(1, 0));
			NA_hot.setIcons(nahot);
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

		FluidRegistry.registerFluid(H2);
		FluidRegistry.registerFluid(H3);
		FluidRegistry.registerFluid(PLASMA);

		FluidRegistry.registerFluid(NH3_lo);
		FluidRegistry.registerFluid(H2O_lo);
		FluidRegistry.registerFluid(NA_hot);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(D2O, FluidContainerRegistry.BUCKET_VOLUME), ReactorItems.BUCKET.getStackOfMetadata(0), new ItemStack(Item.bucketEmpty));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(HF, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hfcan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(UF6, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.uf6can, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(NH3, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.nh3can, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(NA, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.nacan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(CL, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.clcan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(O, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.ocan, ReactorStacks.emptycan);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(H2, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.h2can, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(H3, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.h3can, ReactorStacks.emptycan);
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
				MinecraftForge.setBlockHarvestLevel(ReactorBlocks.ORE.getBlockVariable(), ore.getBlockMetadata(), "pickaxe", ore.harvestLevel);
			}
		}
		Block b = ReactorBlocks.FLUORITEORE.getBlockVariable();
		MinecraftForge.setBlockHarvestLevel(b, "pickaxe", ReactorOres.FLUORITE.harvestLevel);
		OreDictionary.registerOre("dustQuicklime", ReactorStacks.lime.copy());

		for (int i = 0; i < FluoriteTypes.colorList.length; i++) {
			FluoriteTypes fl = FluoriteTypes.colorList[i];
			ItemStack is = new ItemStack(ReactorBlocks.FLUORITEORE.getBlockID(), 1, i);
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
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}
