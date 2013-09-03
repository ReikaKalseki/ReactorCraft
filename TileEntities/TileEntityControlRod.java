/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityControlRod extends TileEntityReactorBase implements ReactorCoreTE {

	private boolean lowered;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.CONTROL.ordinal();
	}

	public void toggle() {
		lowered = !lowered;
	}

	public boolean isActive() {
		return lowered;
	}

	@Override
	public void onNeutron(EntityNeutron e, World world, int x, int y, int z) {

	}

}
