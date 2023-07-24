/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.HTGR;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Base.TileEntityIntermediateBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.RotaryCraft.Auxiliary.ItemStacks;

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
		return 3000;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.CO2HEATER;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("rc co2");
	}

	@Override
	protected Fluid getOutputFluid() {
		return FluidRegistry.getFluid("rc hot co2");
	}

	@Override
	protected void overheat(World world, int x, int y, int z) {
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 4, true);
		for (int i = 0; i < 4; i++)
			ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), ItemStacks.scrap);
	}

	@Override
	public ReactorType getDefaultReactorType() {
		return ReactorType.HTGR;
	}

}
