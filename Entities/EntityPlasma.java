/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Entities;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPlasma extends ParticleEntity implements IEntityAdditionalSpawnData {

	private int targetX;
	private int targetZ;

	private double originX;
	private double originY;
	private double originZ;

	public int magnetOrdinal = -1;

	private String placerOfInjector;

	public EntityPlasma(World world) {
		super(world);
	}

	public EntityPlasma(World world, double x, double y, double z, String placer) {
		super(world);
		originX = x;
		originY = y;
		originZ = z;
		this.setPosition(x, y, z);

		placerOfInjector = placer;
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
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(1, 1, 1);
		List<EntityPlasma> li = worldObj.getEntitiesWithinAABB(EntityPlasma.class, box);
		if (li.size() >= this.getFusionThreshold()) {
			EntityFusion fus = new EntityFusion(worldObj, posX, posY, posZ, placerOfInjector);
			worldObj.spawnEntityInWorld(fus);
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
		if (!worldObj.isRemote && rand.nextInt(12) == 0)
			this.checkFusion();
		motionY = 0;
		posY = originY;

		if (this.getDistanceFromSpawn() > 100)
			this.setDead();
	}

	private double getDistanceFromSpawn() {
		return Math.abs(originX-posX)+Math.abs(originY-posY)+Math.abs(originZ-posZ);
	}

	@Override
	public double getHitboxSize() {
		return 0.5;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeDouble(originX);
		data.writeDouble(originY);
		data.writeDouble(originZ);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		originX = data.readDouble();
		originY = data.readDouble();
		originZ = data.readDouble();
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

}
