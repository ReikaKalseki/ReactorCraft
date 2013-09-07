/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import net.minecraft.world.World;
import Reika.ReactorCraft.Entities.EntityNeutron;

public interface ReactorCoreTE extends Temperatured {

	public abstract boolean onNeutron(EntityNeutron e, World world, int x, int y, int z);

}
