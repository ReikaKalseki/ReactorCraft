/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Breeder;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.ReactorCraft.Base.TileEntityIntermediateBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntitySodiumHeater extends TileEntityIntermediateBoiler {

	@Override
	public int getLiquidUsage() {
		return 100;
	}

	@Override
	public int getMinimumTemperature() {
		return 300;
	}

	@Override
	protected double getFluidHeatCapacity() {
		return ReikaThermoHelper.SODIUM_HEAT;
	}

	@Override
	public int getMaxTemperature() {
		return 2000;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.SODIUMBOILER.ordinal();
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("rc sodium");
	}

	@Override
	protected Fluid getOutputFluid() {
		return FluidRegistry.getFluid("hotsodium");
	}

}
