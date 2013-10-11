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
import Reika.DragonAPI.RetroGenController;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ControlledConfig;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Auxiliary.PotionRadiation;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.ReactorTab;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.World.ReactorOreGenerator;
import Reika.ReactorCraft.World.ReactorRetroGen;
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
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod( modid = "ReactorCraft", name="ReactorCraft", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ReactorCraftData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ReactorCraftData" }, packetHandler = ServerPackets.class))
public class ReactorCraft extends DragonAPIMod {

	@Instance("ReactorCraft")
	public static ReactorCraft instance = new ReactorCraft();

	public static final ControlledConfig config = new ControlledConfig(instance, ReactorOptions.optionList, ReactorBlocks.blockList, ReactorItems.itemList, null, 1);

	public static final String packetChannel = "ReactorCraftData";

	public static CreativeTabs tabRctr = new ReactorTab(CreativeTabs.getNextID(), "ReactorCraft");

	public static ModLogger logger;

	public static Item[] items = new Item[ReactorItems.itemList.length];
	public static Block[] blocks = new Block[ReactorBlocks.blockList.length];

	public static final Fluid D2O = new Fluid("heavy water").setDensity(1100).setViscosity(1050);
	public static final Fluid HF = new Fluid("hydrofluoric acid").setDensity(-1).setViscosity(10).setGaseous(true);
	public static final Fluid UF6 = new Fluid("uranium hexafluoride").setDensity(15).setViscosity(10).setGaseous(true);

	public static PotionRadiation radiation = (PotionRadiation)new PotionRadiation(30, true).setPotionName("Radiation Sickness");

	@SidedProxy(clientSide="Reika.ReactorCraft.ClientProxy", serverSide="Reika.ReactorCraft.CommonProxy")
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(this);

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, ReactorOptions.LOGLOADING.getState(), ReactorOptions.DEBUGMODE.getState(), false);

		this.addBlocks();
		this.addItems();
		this.addLiquids();
		this.registerOres();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		ReactorRecipes.addRecipes();
		EntityRegistry.registerModEntity(EntityNeutron.class, "Neutron", EntityRegistry.findGlobalUniqueEntityId(), instance, 64, 20, true);
		EntityRegistry.registerModEntity(EntityRadiation.class, "Radiation", EntityRegistry.findGlobalUniqueEntityId()+1, instance, 64, 20, true);
		NetworkRegistry.instance().registerGuiHandler(instance, new ReactorGuiHandler());
		GameRegistry.registerWorldGenerator(new ReactorOreGenerator());
		if (ReactorOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new ReactorRetroGen());
			//Set state back
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		ReactorRecipes.addModInterface();
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Post event) {
		setupLiquidIcons();
	}

	@SideOnly(Side.CLIENT)
	private static void setupLiquidIcons() {
		logger.log("Loading Liquid Icons");

		Item d2o = items[ReactorItems.HEAVYWATER.ordinal()];
		Item hf = items[ReactorItems.HF.ordinal()];
		Item uf6 = items[ReactorItems.UF6.ordinal()];

		D2O.setIcons(d2o.getIconFromDamage(0));
		HF.setIcons(hf.getIconFromDamage(0));
		UF6.setIcons(uf6.getIconFromDamage(0));
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ReactorItems.itemList, items, logger.shouldLog());
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ReactorBlocks.blockList, blocks, logger.shouldLog());
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			GameRegistry.registerTileEntity(ReactorTiles.TEList[i].getTEClass(), "Reactor"+ReactorTiles.TEList[i].getName());
			ReikaJavaLibrary.initClass(ReactorTiles.TEList[i].getTEClass());
		}
	}

	private static void addLiquids() {
		logger.log("Loading And Registering Liquids");

		Item d2o = items[ReactorItems.HEAVYWATER.ordinal()];
		Item uf6 = items[ReactorItems.UF6.ordinal()];
		Item hf = items[ReactorItems.HF.ordinal()];

		LanguageRegistry.addName(d2o, "Heavy Water");
		LanguageRegistry.addName(uf6, "Uranium Hexafluoride");
		LanguageRegistry.addName(hf, "Hydrofluoric Acid");

		FluidRegistry.registerFluid(D2O);
		FluidRegistry.registerFluid(HF);
		FluidRegistry.registerFluid(UF6);

		//ReikaJavaLibrary.spamConsole(new FluidStack(D2O, FluidContainerRegistry.BUCKET_VOLUME)+":"+ReactorItems.BUCKET.getStackOfMetadata(0)+":"+new ItemStack(Item.bucketEmpty));

		FluidContainerRegistry.registerFluidContainer(new FluidStack(D2O, FluidContainerRegistry.BUCKET_VOLUME), ReactorItems.BUCKET.getStackOfMetadata(0), new ItemStack(Item.bucketEmpty));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(HF, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.hfcan, ReactorStacks.emptycan);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(UF6, FluidContainerRegistry.BUCKET_VOLUME), ReactorStacks.uf6can, ReactorStacks.emptycan);
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
			OreDictionary.registerOre(ore.getDictionaryName(), ore.getOreBlock()); //only white fluorite gets registered
			OreDictionary.registerOre(ore.getProductDictionaryName(), ore.getProduct());
			if (ore != ReactorOres.FLUORITE)
				MinecraftForge.setBlockHarvestLevel(ReactorBlocks.ORE.getBlockVariable(), ore.getBlockMetadata(), "pickaxe", ore.harvestLevel);
		}
		Block b = ReactorBlocks.FLUORITEORE.getBlockVariable();
		MinecraftForge.setBlockHarvestLevel(b, "pickaxe", ReactorOres.FLUORITE.harvestLevel);
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
