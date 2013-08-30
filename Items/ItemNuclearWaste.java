/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.RadiationEffects;
import Reika.ReactorCraft.Base.ReactorItemBase;
import Reika.ReactorCraft.Entities.EntityNuclearWaste;

public class ItemNuclearWaste extends ReactorItemBase {

	public ItemNuclearWaste(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack)
	{
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityNuclearWaste ei = new EntityNuclearWaste(world, location.posX, location.posY, location.posZ, itemstack);
		ei.motionX = location.motionX;
		ei.motionY = location.motionY;
		ei.motionZ = location.motionZ;
		ei.delayBeforeCanPickup = 10;
		return ei;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int p4, boolean p5) {
		if (e instanceof EntityLiving)
			RadiationEffects.applyEffects((EntityLiving)e);
	}

}
