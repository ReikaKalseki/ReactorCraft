/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;

import net.minecraft.world.World;

public class TileEntityNeutronReflector extends TileEntityReactorBase implements ReactorCoreTE {

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (rand.nextInt(4) == 0) {
			e.motionX = -e.motionX;
			e.motionZ = -e.motionZ;
			e.velocityChanged = true;
			return false;
		}
		else
			return true;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.REFLECTOR.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}