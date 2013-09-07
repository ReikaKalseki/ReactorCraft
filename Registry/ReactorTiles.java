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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.TileEntityCondenser;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.TileEntityULine;
import Reika.ReactorCraft.TileEntities.TileEntityUProcessor;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.TileEntityWaterLine;

public enum ReactorTiles {

	FUEL("Fuel Core", TileEntityFuelRod.class, 0),
	CONTROL("Control Rod", TileEntityControlRod.class, 1),
	COOLANT("Coolant Cell", TileEntityWaterCell.class, 2),
	CPU("Central Control", TileEntityCPU.class, 0),
	TURBINECORE("Turbine Core", TileEntityTurbineCore.class, 0, ""),
	CONDENSER("Condenser", TileEntityCondenser.class, 1, ""),
	WATERLINE("Water Line", TileEntityWaterLine.class, 2, ""),
	ITEMLINE("Fuel Supply Line", TileEntityULine.class, 3, ""),
	HEAVYPUMP("Heavy Water Extractor", TileEntityHeavyPump.class, 0, "RenderHeavyPump"), //looks like vertical impeller
	CENTRIFUGE("Isotope Centrifuge", TileEntityCentrifuge.class, 1, ""),
	PROCESSOR("Uranium Processor", TileEntityUProcessor.class, 1),
	WASTECONTAINER("Spent Fuel Container", TileEntityWasteContainer.class, 4, "");

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
		return name;
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
			return true;
		default:
			return false;
		}
	}

	public boolean hasTextureStates() {
		switch(this) {
		case COOLANT:
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
			ReactorCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
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
		case ITEMLINE:
		case TURBINECORE:
		case WASTECONTAINER:
		case WATERLINE:
			return true;
		default:
			return false;
		}
	}

	public int getTextureStates() {
		switch(this) {
		case COOLANT:
			return 3;
		default:
			return 1;
		}
	}

	public int getBlockID() {
		return this.getBlockVariable().blockID;
	}

	public Block getBlockVariable() {
		return ReactorBlocks.blockList[this.getBlockVariableIndex()].getBlockVariable();
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
		return false;
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

}
