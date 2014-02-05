/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Entities;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class EntityFusionNeutron extends EntityNeutron {

	public EntityFusionNeutron(World world) {
		super(world);
	}

	public EntityFusionNeutron(World world, int x, int y, int z, ForgeDirection f) {
		super(world, x, y, z, f);
	}

}
