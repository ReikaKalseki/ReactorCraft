/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.TileEntityGasDuct;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.ReactorCraft.TileEntities.TileEntityReactorPump;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCondenser;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.Fission.TileEntitySteamGrate;
import Reika.ReactorCraft.TileEntities.Fission.TileEntitySteamLine;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntityBreederCore;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntitySodiumHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityMagnet;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityNeutronAbsorber;
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
	CENTRIFUGE("machine.centrifuge", 			ReactorBlocks.MODELMACHINE,		TileEntityCentrifuge.class, 	1, "RenderCentrifuge"),
	PROCESSOR("machine.processor", 				ReactorBlocks.MODELMACHINE,		TileEntityUProcessor.class, 	2, "RenderProcessor"),
	WASTECONTAINER("machine.wastecontainer", 	ReactorBlocks.MACHINE,			TileEntityWasteContainer.class, 2),
	BOILER("machine.reactorboiler", 			ReactorBlocks.REACTOR,			TileEntityReactorBoiler.class, 	3),
	GRATE("machine.grate", 						ReactorBlocks.MODELREACTOR,		TileEntitySteamGrate.class, 	3, "RenderSteamGrate"),
	PUMP("machine.reactorpump", 				ReactorBlocks.MODELREACTOR,		TileEntityReactorPump.class, 	4, "RenderReactorPump"),
	SYNTHESIZER("machine.synthesizer", 			ReactorBlocks.MACHINE,			TileEntitySynthesizer.class, 	1),
	MAGNET("machine.magnet", 					ReactorBlocks.MODELREACTOR,		TileEntityMagnet.class, 		5, "RenderMagnet"),
	ELECTROLYZER("machine.electrolyzer", 		ReactorBlocks.MODELMACHINE,		TileEntityElectrolyzer.class, 	5, "RenderElectrolyzer"),
	TRITIZER("machine.tritizer", 				ReactorBlocks.REACTOR,			TileEntityTritizer.class, 		4),
	BREEDER("machine.breedercore", 				ReactorBlocks.REACTOR,			TileEntityBreederCore.class, 	5),
	SODIUMBOILER("machine.sodiumboiler", 		ReactorBlocks.MACHINE,			TileEntitySodiumHeater.class, 	6),
	EXCHANGER("machine.exchanger", 				ReactorBlocks.MODELMACHINE,		TileEntityHeatExchanger.class, 	4, "RenderExchanger"),
	STORAGE("machine.storage", 					ReactorBlocks.MODELMACHINE,		TileEntityWasteStorage.class,	3, "RenderWasteStorage"),
	INJECTOR("machine.injector", 				ReactorBlocks.REACTOR,			TileEntityFusionInjector.class, 7, ""),
	HEATER("machine.fusionheater", 				ReactorBlocks.REACTOR,			TileEntityFusionHeater.class, 	8, ""),
	GASPIPE("machine.gasduct", 					ReactorBlocks.DUCT,				TileEntityGasDuct.class, 		0, "DuctRenderer"),
	//IONIZER("machine.ionizer", 				ReactorBlocks.IONIZER,			TileEntityIonizer.class, 		8, ""),
	MAGNETPIPE("machine.magnetpipe", 			ReactorBlocks.DUCT,				TileEntityMagneticPipe.class, 	1, "DuctRenderer"),
	ABSORBER("machine.absorber",				ReactorBlocks.REACTOR,			TileEntityNeutronAbsorber.class,1);

	private String name;
	private final Class teClass;
	private int meta;
	private String render;
	private final ReactorBlocks blockInstance;

	private static final HashMap<List<Integer>, ReactorTiles> reactorMappings = new HashMap();

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

	public static TileEntity createTEFromIDAndMetadata(int id, int meta) {
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

	public static ReactorTiles getMachineFromIDandMetadata(int id, int meta) {
		return reactorMappings.get(Arrays.asList(id, meta));
	}

	public boolean isAvailableInCreativeInventory() {
		return true;
	}

	public static ReactorTiles getTE(IBlockAccess iba, int x, int y, int z) {
		int id = iba.getBlockId(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		return getMachineFromIDandMetadata(id, meta);
	}

	public ItemStack getCraftedProduct() {
		return new ItemStack(ReactorItems.PLACER.getShiftedItemID(), 1, this.ordinal());
	}

	public TileEntity createTEInstanceForRender() {
		try {
			return (TileEntity)teClass.newInstance();
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
			return 4;
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
		switch(this) {
		case COOLANT:
		case BOILER:
		case SODIUMBOILER:
		case CONTROL:
			return true;
		default:
			return false;
		}
	}

	public int getBlockID() {
		return this.getBlockVariable().blockID;
	}

	public Block getBlockVariable() {
		return blockInstance.getBlockVariable();
	}

	public int getBlockMetadata() {
		return meta;
	}

	public boolean renderInPass1() {
		switch(this) {
		case PROCESSOR:
		case MAGNET:
		case GASPIPE:
		case MAGNETPIPE:
			return true;
		default:
			return false;
		}
	}

	// A development feature, really
	public boolean renderWorks() {
		try {
			Class.forName(this.getRenderer());
			return true;
		}
		catch (ClassNotFoundException e) {
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
			int id = r.getBlockID();
			int meta = r.getBlockMetadata();
			List<Integer> li = Arrays.asList(id, meta);
			reactorMappings.put(li, r);
		}
	}

	public boolean isPipe() {
		return this == GASPIPE || this == MAGNETPIPE;
	}


}
