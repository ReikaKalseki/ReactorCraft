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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.TileEntityBreederCore;
import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.TileEntityCondenser;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.TileEntityMagnet;
import Reika.ReactorCraft.TileEntities.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.TileEntityReactorPump;
import Reika.ReactorCraft.TileEntities.TileEntitySodiumHeater;
import Reika.ReactorCraft.TileEntities.TileEntitySteamGrate;
import Reika.ReactorCraft.TileEntities.TileEntitySteamLine;
import Reika.ReactorCraft.TileEntities.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.TileEntityTritizer;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.TileEntityUProcessor;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ReactorTiles {

	FUEL("machine.fuel", TileEntityFuelRod.class, 0),
	CONTROL("machine.control", TileEntityControlRod.class, 1),
	COOLANT("machine.coolant", TileEntityWaterCell.class, 2),
	CPU("machine.cpu", TileEntityCPU.class, 0),
	TURBINECORE("machine.turbine", TileEntityTurbineCore.class, 0, "RenderTurbine"),
	CONDENSER("machine.condenser", TileEntityCondenser.class, 1, "RenderCondenser"),
	STEAMLINE("machine.steamline", TileEntitySteamLine.class, 2, "RenderWaterLine"),
	HEAVYPUMP("machine.heavypump", TileEntityHeavyPump.class, 0, "RenderHeavyPump"),
	CENTRIFUGE("machine.centrifuge", TileEntityCentrifuge.class, 1, "RenderCentrifuge"),
	PROCESSOR("machine.processor", TileEntityUProcessor.class, 2, "RenderProcessor"),
	WASTECONTAINER("machine.wastecontainer", TileEntityWasteContainer.class, 2),
	BOILER("machine.reactorboiler", TileEntityReactorBoiler.class, 3),
	GRATE("machine.grate", TileEntitySteamGrate.class, 3, "RenderSteamGrate"),
	PUMP("machine.pump", TileEntityReactorPump.class, 4, "RenderReactorPump"),
	SYNTHESIZER("machine.synthesizer", TileEntitySynthesizer.class, 1),
	MAGNET("machine.magnet", TileEntityMagnet.class, 5, ""),
	ELECTROLYZER("machine.electrolyzer", TileEntityElectrolyzer.class, 3),
	TRITIZER("machine.tritizer", TileEntityTritizer.class, 4),
	BREEDER("machine.breedercore", TileEntityBreederCore.class, 5),
	SODIUMBOILER("machine.sodiumboiler", TileEntitySodiumHeater.class, 6),
	EXCHANGER("machine.exchanger", TileEntityHeatExchanger.class, 4, "RenderExchanger"),
	STORAGE("machine.storage", TileEntityWasteStorage.class, 3, "RenderWasteStorage");

	private String name;
	private Class teClass;
	private int meta;
	private String render;

	public static final ReactorTiles[] TEList = values();

	private ReactorTiles(String n, Class<? extends TileEntity> tile, int m) {
		this(n, tile, m, null);
	}

	private ReactorTiles(String n, Class<? extends TileEntity> tile, int m, String r) {
		teClass = tile;
		name = n;
		render = r;
		meta = m;
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
			if (TEList[i].getBlockVariableIndex() == b.ordinal())
				li.add(TEList[i]);
		}
		return li;
	}

	public static TileEntity createTEFromIDAndMetadata(int id, int meta) {
		int index = getMachineIndexFromIDandMetadata(id, meta);
		if (index == -1) {
			ReactorCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
			return null;
		}
		Class TEClass = TEList[index].teClass;
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

	public static int getMachineIndexFromIDandMetadata(int id, int meta) {
		for (int i = 0; i < TEList.length; i++) {
			ReactorTiles m = TEList[i];
			if (m.getBlockID() == id && meta == m.getBlockMetadata())
				return i;
		}
		//throw new RegistrationException(ReactorCraft.instance, "ID "+id+" and metadata "+metad+" are not a valid machine identification pair!");
		return -1;
	}

	public boolean isAvailableInCreativeInventory() {
		return true;
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
			return true;
		default:
			return false;
		}
	}

	public static ReactorTiles getTE(IBlockAccess iba, int x, int y, int z) {
		int id = iba.getBlockId(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		int index = getMachineIndexFromIDandMetadata(id, meta);
		if (index == -1) {
			//ReactorCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
			return null;
		}
		return TEList[index];
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

	public boolean isReactorBlock() {
		switch(this) {
		case COOLANT:
		case CONDENSER:
		case CONTROL:
		case FUEL:
			//case ITEMLINE:
		case TURBINECORE:
		case STEAMLINE:
		case BOILER:
		case GRATE:
		case PUMP:
		case MAGNET:
		case TRITIZER:
		case BREEDER:
			return true;
		default:
			return false;
		}
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

	public int getBlockID() {
		return this.getBlockVariable().blockID;
	}

	public Block getBlockVariable() {
		int idx = this.getBlockVariableIndex();
		//ReikaJavaLibrary.pConsole(this+"  "+idx+"  "+ReactorBlocks.blockList[idx].getBlockID()+" "+this.getBlockMetadata());
		return ReactorBlocks.blockList[idx].getBlockVariable();
	}

	public int getBlockVariableIndex() {
		if (this.hasRender()) {
			if (this.isReactorBlock()) {
				return ReactorBlocks.MODELREACTOR.ordinal();
			}
			else {
				return ReactorBlocks.MODELMACHINE.ordinal();
			}
		}
		else {
			if (this.isReactorBlock()) {
				return ReactorBlocks.REACTOR.ordinal();
			}
			else {
				return ReactorBlocks.MACHINE.ordinal();
			}
		}
	}

	public int getBlockMetadata() {
		return meta;
	}

	public boolean renderInPass1() {
		switch(this) {
		case PROCESSOR:
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


}
