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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityNuclearWaste extends EntityItem {

	public EntityNuclearWaste(World par1World) {
		super(par1World);
	}

	public EntityNuclearWaste(World world, double x, double y, double z, ItemStack is)
	{
		super(world, x, y, z, is);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		age = 0;
	}

	@Override
	public boolean isEntityInvulnerable()
	{
		return true;
	}

}
