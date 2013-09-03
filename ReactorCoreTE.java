/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.world.World;
import Reika.ReactorCraft.Entities.EntityNeutron;

public interface ReactorCoreTE {

	public abstract void onNeutron(EntityNeutron e, World world, int x, int y, int z);

}
