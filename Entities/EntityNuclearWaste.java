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

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.RadiationEffects;

public class EntityNuclearWaste extends EntityItem {

	public static final double RANGE = 6;

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
		this.applyRadiation();
	}

	@Override
	public boolean isEntityInvulnerable()
	{
		return true;
	}

	private void applyRadiation() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x, y, z).expand(RANGE, RANGE, RANGE);
		List<EntityLiving> inbox = world.getEntitiesWithinAABB(EntityLiving.class, box);
		for (int i = 0; i < inbox.size(); i++) {
			EntityLiving e = inbox.get(i);
			double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
			if (dd <= RANGE) {
				RadiationEffects.applyEffects(e);
			}
		}
	}

}
