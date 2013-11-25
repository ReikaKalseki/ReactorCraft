/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.ReactorCraft.TileEntities.TileEntityWaterCell.LiquidStates;

public interface Temperatured {

	public abstract int getTemperature();

	public void setTemperature(int T);

	public int getMaxTemperature();

	public boolean canDumpHeatInto(LiquidStates liq);

}
