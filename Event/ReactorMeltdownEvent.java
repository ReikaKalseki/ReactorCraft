/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Event;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.Event;

public class ReactorMeltdownEvent extends Event {

	public final World world;
	public final int centerX;
	public final int centerY;
	public final int centerZ;

	public ReactorMeltdownEvent(World world, int x, int y, int z) {
		this.world = world;
		centerX = x;
		centerY = y;
		centerZ = z;
	}

}
