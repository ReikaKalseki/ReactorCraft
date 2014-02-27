/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

/** Reactor core blocks only. */
public interface Temperatured {

	public abstract int getTemperature();

	public void setTemperature(int T);

	public int getMaxTemperature();

	public boolean canDumpHeatInto(LiquidStates liq);

}
