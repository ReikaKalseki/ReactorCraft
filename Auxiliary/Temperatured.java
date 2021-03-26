/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.Interfaces.TileEntity.ThermalTile;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.RotaryCraft.Auxiliary.Interfaces.HeatConduction;

/** Reactor core blocks only. */
public interface Temperatured extends ThermalTile, HeatConduction {

	public abstract int getTemperature();

	public void setTemperature(int T);

	public int getMaxTemperature();

	public boolean canDumpHeatInto(LiquidStates liq);

}
