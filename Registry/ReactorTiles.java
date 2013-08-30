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

import net.minecraft.tileentity.TileEntity;
import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;
import Reika.ReactorCraft.TileEntities.TileEntityFuelCore;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineBlade;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.TileEntityWaterPipe;

public enum ReactorTiles {

	FUEL("Fuel Core", TileEntityFuelCore.class),
	CONTROL("Control Rod", TileEntityControlRod.class),
	COOLANT("Coolant Cell", TileEntityWaterPipe.class),
	CPU("Central Control", TileEntityCPU.class),
	TURBINEBLADE("Turbine Blade", TileEntityTurbineBlade.class),
	TURBINECORE("Turbine Core", TileEntityTurbineCore.class);

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

}
