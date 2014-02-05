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

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;

public class EntityPlasma extends ParticleEntity {

	private int targetX;
	private int targetZ;

	public EntityPlasma(World par1World) {
		super(par1World);
	}

	@Override
	public double getSpeed() {
		return 0.75;
	}

	@Override
	public boolean onEnterBlock(World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (ReikaWorldHelper.flammable(world, x, y, z))
				ReikaWorldHelper.ignite(world, x, y, z);
		}
		return false;
	}

	public void setTarget(int x, int z) {
		targetX = x;
		targetZ = z;
		double dx = targetX+0.5-posX;
		double dz = targetZ+0.5-posZ;
		double dd = ReikaMathLibrary.py3d(dx, 0, dz);
		double v = this.getSpeed();
		motionX = dx*v/dd;
		motionZ = dz*v/dd;
		//ReikaJavaLibrary.pConsole(motionX+":"+motionZ);
		velocityChanged = true;
	}

	private void checkFusion() {
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(posX, posY, posZ, posX, posY, posZ).expand(1, 1, 1);
		List<EntityPlasma> li = worldObj.getEntitiesWithinAABB(EntityPlasma.class, box);
		if (li.size() >= this.getFusionThreshold()) {
			EntityFusion fus = new EntityFusion(worldObj, posX, posY, posZ);
			worldObj.spawnEntityInWorld(fus);
			for (int i = 0; i < li.size(); i++)
				li.get(0).setDead();
			this.setDead();
		}
	}

	public int getFusionThreshold() {
		return 15+rand.nextInt(6);
	}

	@Override
	public void applyEntityCollision(Entity e) {
		e.attackEntityFrom(ReactorCraft.fusionDamage, Integer.MAX_VALUE);
	}

	@Override
	protected void onTick() {
		if (ticksExisted > 1200)
			;//this.setDead();
		if (rand.nextInt(5) == 0)
			this.checkFusion();
	}

	@Override
	public double getHitboxSize() {
		return 0.5;
	}

}
