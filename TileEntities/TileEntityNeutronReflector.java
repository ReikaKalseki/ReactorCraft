/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;

import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityNeutronReflector extends TileEntityReactorBase implements ReactorCoreTE {

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		e.moderate();
		if (rand.nextInt(4) == 0) {
			e.motionX = -e.motionX;
			e.motionZ = -e.motionZ;
			e.velocityChanged = true;
			return false;
		}
		else
			return rand.nextBoolean();
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.REFLECTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected boolean isTickingTE() {
		return false;
	}

}
