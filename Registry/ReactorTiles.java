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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityCondenser;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineBlade;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.TileEntityWaterLine;

public enum ReactorTiles {

	FUEL("Fuel Core", TileEntityFuelRod.class),
	CONTROL("Control Rod", TileEntityControlRod.class),
	COOLANT("Coolant Cell", TileEntityWaterCell.class),
	CPU("Central Control", TileEntityCPU.class),
	TURBINEBLADE("Turbine Blade", TileEntityTurbineBlade.class),
	TURBINECORE("Turbine Core", TileEntityTurbineCore.class),
	CONDENSER("Condenser", TileEntityCondenser.class),
	WATERLINE("Water Line", TileEntityWaterLine.class);
	//Heavy Water Pump

	private String name;
	private Class teClass;

	public static final ReactorTiles[] TEList = values();

	private ReactorTiles(String n, Class<? extends TileEntity> tile) {
		teClass = tile;
		name = n;
	}

	public String getName() {
		return name;
	}

	public Class getTEClass() {
		return teClass;
	}

	public static TileEntity createTEFromMetadata(int meta) {
		Class TEClass = TEList[meta].teClass;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ReactorCraft.instance, "Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ReactorCraft.instance, "Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
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
		if (iba.getBlockId(x, y, z) == ReactorBlocks.TILEENTITY.getBlockID()) {
			return TEList[iba.getBlockMetadata(x, y, z)];
		}
		return null;
	}

	public ItemStack getCraftedProduct() {
		return new ItemStack(ReactorItems.PLACER.getShiftedItemID(), 1, this.ordinal());
	}

}
