/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Breeder;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Base.TileEntityIntermediateBoiler;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.ItemStacks;

public class TileEntitySodiumHeater extends TileEntityIntermediateBoiler {

	@Override
	public int getLiquidUsage() {
		return 100;
	}

	@Override
	public int getMinimumTemperature() {
		return 301;
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

	@Override
	protected void overheat(World world, int x, int y, int z) {
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 4, true);
		for (int i = 0; i < 4; i++) {
			ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), ItemStacks.scrap);
			ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), ItemStacks.ironscrap);
		}
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		NeutronType type = e.getType();
		return !tank.isEmpty() && ReikaRandomHelper.doWithChance(type.getSodiumBoilerAbsorptionChance());
	}

}
