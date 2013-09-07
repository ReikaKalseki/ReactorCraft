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
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;

public class EntityRadiation extends InertEntity {

	public final int effectRange;

	public EntityRadiation(World par1World) {
		super(par1World);
		effectRange = 16;
	}

	public EntityRadiation(World world, int range) {
		super(world);
		effectRange = range;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {

	}

	@Override
	public void onUpdate()
	{
		this.onEntityUpdate();
		this.applyRadiation();
	}

	private void applyRadiation() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		Random r = new Random();
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x, y, z).expand(effectRange, effectRange, effectRange);
		List<EntityLiving> inbox = world.getEntitiesWithinAABB(EntityLiving.class, box);
		for (int i = 0; i < inbox.size(); i++) {
			EntityLiving e = inbox.get(i);
			double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
			if (dd <= effectRange) {
				RadiationEffects.applyEffects(e);
			}
		}

		if (r.nextInt(20) == 0) {
			int dx = (int)x-effectRange+r.nextInt(effectRange*2+1);
			int dy = (int)y-effectRange+r.nextInt(effectRange*2+1);
			int dz = (int)z-effectRange+r.nextInt(effectRange*2+1);
			RadiationEffects.transformBlock(world, dx, dy, dz);
		}
	}

}
