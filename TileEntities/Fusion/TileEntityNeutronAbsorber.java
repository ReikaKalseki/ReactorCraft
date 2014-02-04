/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityNeutronAbsorber extends TileEntityReactorBase implements Temperatured, ReactorCoreTE {

	private StepTimer tempTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.ABSORBER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		tempTimer.update();
		if (tempTimer.checkCap())
			this.updateTemperature(world, x, y, z);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		temperature += 5;
		return true;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 2000;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq != LiquidStates.EMPTY;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

}
