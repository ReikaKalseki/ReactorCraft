/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.TileEntities.TileEntityFusionMarker;
import Reika.ReactorCraft.TileEntities.TileEntityGasCollector;
import Reika.ReactorCraft.TileEntities.TileEntityGasDuct;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.ReactorCraft.TileEntities.TileEntityNeutronReflector;
import Reika.ReactorCraft.TileEntities.TileEntityReactorFlywheel;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineMeter;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntityBreederCore;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntitySodiumHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityNeutronAbsorber;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityCO2Heater;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityCondenser;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHiPTurbine;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityReactorPump;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamGrate;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityTritizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ReactorTiles {

	FUEL("machine.fuel", 						ReactorBlocks.REACTOR,			TileEntityFuelRod.class, 		0),
	CONTROL("machine.control", 					ReactorBlocks.MODELREACTOR,		TileEntityControlRod.class, 	6, "RenderControl"),
	COOLANT("machine.coolant", 					ReactorBlocks.REACTOR,			TileEntityWaterCell.class, 		2),
	CPU("machine.cpu", 							ReactorBlocks.MACHINE,			TileEntityCPU.class, 			0),
	TURBINECORE("machine.turbine", 				ReactorBlocks.MODELREACTOR,		TileEntityTurbineCore.class, 	0, "RenderTurbine"),
	CONDENSER("machine.condenser", 				ReactorBlocks.MODELREACTOR,		TileEntityCondenser.class, 		1, "RenderCondenser"),
	STEAMLINE("machine.steamline", 				ReactorBlocks.LINE,				TileEntitySteamLine.class, 		2, "RenderWaterLine"),
	HEAVYPUMP("machine.heavypump", 				ReactorBlocks.MODELMACHINE,		TileEntityHeavyPump.class, 		0, "RenderHeavyPump"),
	CENTRIFUGE("machine.isocentrifuge", 		ReactorBlocks.MODELMACHINE,		TileEntityCentrifuge.class, 	1, "RenderCentrifuge"),
	PROCESSOR("machine.processor", 				ReactorBlocks.MODELMACHINE,		TileEntityUProcessor.class, 	2, "RenderProcessor"),
	WASTECONTAINER("machine.wastecontainer", 	ReactorBlocks.MACHINE,			TileEntityWasteContainer.class, 2),
	BOILER("machine.reactorboiler", 			ReactorBlocks.REACTOR,			TileEntityReactorBoiler.class, 	3),
	GRATE("machine.grate", 						ReactorBlocks.MODELREACTOR,		TileEntitySteamGrate.class, 	3, "RenderSteamGrate"),
	PUMP("machine.reactorpump", 				ReactorBlocks.MODELREACTOR,		TileEntityReactorPump.class, 	4, "RenderReactorPump"),
	SYNTHESIZER("machine.synthesizer", 			ReactorBlocks.MACHINE,			TileEntitySynthesizer.class, 	1),
	MAGNET("machine.magnet", 					ReactorBlocks.MODELREACTOR,		TileEntityToroidMagnet.class, 	5, "RenderMagnet"),
	ELECTROLYZER("machine.electrolyzer", 		ReactorBlocks.MODELMACHINE,		TileEntityElectrolyzer.class, 	5, "RenderElectrolyzer"),
	TRITIZER("machine.tritizer", 				ReactorBlocks.REACTOR,			TileEntityTritizer.class, 		4),
	BREEDER("machine.breedercore", 				ReactorBlocks.REACTOR,			TileEntityBreederCore.class, 	5),
	SODIUMBOILER("machine.sodiumboiler", 		ReactorBlocks.REACTOR,			TileEntitySodiumHeater.class, 	6),
	EXCHANGER("machine.exchanger", 				ReactorBlocks.MODELMACHINE,		TileEntityHeatExchanger.class, 	4, "RenderExchanger"),
	STORAGE("machine.storage", 					ReactorBlocks.MODELMACHINE,		TileEntityWasteStorage.class,	3, "RenderWasteStorage"),
	INJECTOR("machine.injector", 				ReactorBlocks.REACTOR,			TileEntityFusionInjector.class, 7),
	HEATER("machine.fusionheater", 				ReactorBlocks.REACTOR,			TileEntityFusionHeater.class, 	8),
	GASPIPE("machine.gasduct", 					ReactorBlocks.DUCT,				TileEntityGasDuct.class, 		0, "DuctRenderer"),
	MAGNETPIPE("machine.magnetpipe", 			ReactorBlocks.DUCT,				TileEntityMagneticPipe.class, 	1, "DuctRenderer"),
	ABSORBER("machine.absorber",				ReactorBlocks.REACTOR,			TileEntityNeutronAbsorber.class,1),
	SOLENOID("machine.solenoid",				ReactorBlocks.MODELREACTOR,		TileEntitySolenoidMagnet.class,	9, "RenderSolenoid"),
	COLLECTOR("machine.collector",				ReactorBlocks.MODELMACHINE,		TileEntityGasCollector.class,	6, "RenderGasCollector"),
	PEBBLEBED("machine.pebblebed",				ReactorBlocks.REACTOR,			TileEntityPebbleBed.class,		9),
	CO2HEATER("machine.co2heater",				ReactorBlocks.REACTOR,			TileEntityCO2Heater.class,		10),
	FLYWHEEL("machine.turbinewheel",			ReactorBlocks.MODELMACHINE,		TileEntityReactorFlywheel.class,7, "RenderTurbineWheel"),
	REFLECTOR("machine.reflector",				ReactorBlocks.REACTOR,			TileEntityNeutronReflector.class,11),
	GENERATOR("machine.reactorgenerator",		ReactorBlocks.MODELMACHINE,		TileEntityReactorGenerator.class,8, "RenderGenerator"),
	MARKER("machine.fusionmarker",				ReactorBlocks.MODELMACHINE,		TileEntityFusionMarker.class,	9,	"RenderFusionMarker"),
	TURBINEMETER("machine.turbinemeter",		ReactorBlocks.MACHINE,			TileEntityTurbineMeter.class,	3),
	BIGTURBINE("machine.bigturbine", 			ReactorBlocks.MODELREACTOR,		TileEntityHiPTurbine.class,		7, "RenderBigTurbine");

	private final String name;
	private final Class teClass;
	private final int meta;
	private String render;
	private final ReactorBlocks blockInstance;
	private TileEntity renderInstance;

	private static final BlockMap<ReactorTiles> reactorMappings = new BlockMap();

	public static final ReactorTiles[] TEList = values();

	private ReactorTiles(String n, ReactorBlocks block, Class<? extends TileEntity> tile, int m) {
		this(n, block, tile, m, null);
	}

	private ReactorTiles(String n, ReactorBlocks block, Class<? extends TileEntity> tile, int m, String r) {
		teClass = tile;
		name = n;
		render = r;
		meta = m;
		blockInstance = block;
	}

	public String getName() {
		return StatCollector.translateToLocal(name);
	}

	public Class getTEClass() {
		return teClass;
	}

	public static ArrayList<ReactorTiles> getTilesOfBlock(ReactorBlocks b) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < TEList.length; i++) {
			if (TEList[i].blockInstance == b)
				li.add(TEList[i]);
		}
		return li;
	}

	public static TileEntity createTEFromIDAndMetadata(Block id, int meta) {
		ReactorTiles index = getMachineFromIDandMetadata(id, meta);
		if (index == null) {
			ReactorCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
			return null;
		}
		Class TEClass = index.teClass;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ReactorCraft.instance, "ID "+id+" and Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ReactorCraft.instance, "ID "+id+" and Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
	}

	public static ReactorTiles getMachineFromIDandMetadata(Block id, int meta) {
		return reactorMappings.get(id, meta);
	}

	public boolean isAvailableInCreativeInventory() {
		if (this == GENERATOR)
			return PowerTypes.RF.exists();
		return true;
	}

	public static ReactorTiles getTE(IBlockAccess iba, int x, int y, int z) {
		Block id = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		return getMachineFromIDandMetadata(id, meta);
	}

	public ItemStack getCraftedProduct() {
		return new ItemStack(ReactorItems.PLACER.getItemInstance(), 1, this.ordinal());
	}

	public TileEntity createTEInstanceForRender() {
		if (renderInstance == null) {
			try {
				renderInstance = (TileEntity)teClass.newInstance();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
				throw new RegistrationException(ReactorCraft.instance, "Could not create TE instance to render "+this);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RegistrationException(ReactorCraft.instance, "Could not create TE instance to render "+this);
			}
		}
		return renderInstance;
	}

	public boolean hasRender() {
		return render != null;
	}

	public String getRenderer() {
		if (!this.hasRender())
			throw new RuntimeException("Machine "+name+" has no render to call!");
		return "Reika.ReactorCraft.Renders."+render;
	}

	public int getTextureStates() {
		switch(this) {
		case COOLANT:
			return TileEntityWaterCell.LiquidStates.list.length;
		case BOILER:
		case SODIUMBOILER:
		case CO2HEATER:
			return 4;
		case PEBBLEBED:
			return 5;
		case INJECTOR:
			return 3;
		case TURBINEMETER:
			return 3;
		default:
			return 1;
		}
	}

	public boolean hasSidedTextures() {
		return false;
	}

	public boolean isEndTextured() {
		switch(this) {
		case FUEL:
		case CONTROL:
		case WASTECONTAINER:
		case SYNTHESIZER:
		case BREEDER:
		case TRITIZER:
			return true;
		default:
			return false;
		}
	}

	public boolean hasTextureStates() {
		return this.getTextureStates() > 1;
	}

	public Block getBlock() {
		return this.getBlockInstance();
	}

	public Block getBlockInstance() {
		return blockInstance.getBlockInstance();
	}

	public int getBlockMetadata() {
		return meta%16;
	}

	public boolean renderInPass1() {
		switch(this) {
		case PROCESSOR:
		case MAGNET:
		case GASPIPE:
		case MAGNETPIPE:
		case COLLECTOR:
			return true;
		default:
			return false;
		}
	}

	public boolean isDummiedOut() {
		return false;
	}

	public void addRecipe(IRecipe ir) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addRecipe(ItemStack is, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(is, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
			}
		}
	}

	public void addCrafting(Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(), obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(this.getCraftedProduct(), obj);
			}
		}
	}

	public void addSizedCrafting(int num, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), obj);
			}
		}
	}

	public static void loadMappings() {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			Block id = r.getBlock();
			int meta = r.getBlockMetadata();
			reactorMappings.put(id, meta, r);
		}
	}

	public boolean isPipe() {
		return this == GASPIPE || this == MAGNETPIPE;
	}

	public ReactorType getReactorType() {
		switch (this) {
		case ABSORBER:
		case HEATER:
		case INJECTOR:
		case MAGNET:
		case MAGNETPIPE:
		case SOLENOID:
			return ReactorType.FUSION;
		case BOILER:
		case CONTROL:
		case COOLANT:
		case CPU:
		case FUEL:
			return ReactorType.FISSION;
		case BREEDER:
		case SODIUMBOILER:
			return ReactorType.BREEDER;
		case CO2HEATER:
		case PEBBLEBED:
			return ReactorType.HTGR;
		default:
			return null;
		}
	}

	public boolean isTurbine() {
		return TileEntityTurbineCore.class.isAssignableFrom(teClass);
	}

	public boolean isPowerReceiver() {
		return ReactorPowerReceiver.class.isAssignableFrom(teClass);
	}


}
