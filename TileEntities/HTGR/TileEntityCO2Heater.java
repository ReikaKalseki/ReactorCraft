/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.HTGR;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.ReactorCraft.Base.TileEntityIntermediateBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityCO2Heater extends TileEntityIntermediateBoiler {

	@Override
	public int getLiquidUsage() {
		return 100;
	}

	@Override
	public int getMinimumTemperature() {
		return TileEntityPebbleBed.MINTEMP;
	}

	@Override
	protected double getFluidHeatCapacity() {
		return ReikaThermoHelper.CO2_HEAT;
	}

	@Override
	public int getMaxTemperature() {
		return 4000;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.CO2HEATER.ordinal();
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("rc co2");
	}

	@Override
	protected Fluid getOutputFluid() {
		return FluidRegistry.getFluid("rc hot co2");
	}

}
