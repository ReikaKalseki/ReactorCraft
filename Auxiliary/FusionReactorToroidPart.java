/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import net.minecraft.world.World;

public interface FusionReactorToroidPart {

	public FusionReactorToroidPart getNextPart(World world, int x, int y, int z);

}
