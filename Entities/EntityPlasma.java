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

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.RotaryCraft.API.Interfaces.CustomFanEntity;

public class EntityPlasma extends ParticleEntity implements CustomFanEntity {

	private int targetX;
	private int targetZ;

	public int magnetOrdinal = -1;

	private int escapeTicks = 0;

	private String placerOfInjector;

	public EntityPlasma(World world) {
		super(world);
	}

	public EntityPlasma(World world, int x, int y, int z, String placer) {
		super(world, x, y, z);

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
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, 1);
		List<EntityPlasma> li = worldObj.getEntitiesWithinAABB(EntityPlasma.class, box);
		if (li.size() >= this.getFusionThreshold() && !li.get(0).hasEscaped() && !li.get(li.size()-1).hasEscaped()) {
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
		int dmg = e instanceof EntityLivingBase && ((EntityLivingBase)e).isPotionActive(Potion.fireResistance) ? 4 : Integer.MAX_VALUE;
		e.attackEntityFrom(ReactorCraft.fusionDamage, dmg);
		if (e instanceof EntityPlayer) {
			if (e.isDead || ((EntityLivingBase)e).getHealth() <= 0) {
				ReactorAchievements.PLASMADIE.triggerAchievement((EntityPlayer)e);
			}
		}
	}

	@Override
	protected void onTick() {
		if (ticksExisted > 1200)
			;//this.setDead();
		if (!worldObj.isRemote && !this.hasEscapedSeverely() && rand.nextInt(this.hasEscaped() ? 48 : 12) == 0)
			this.checkFusion();
		motionY = 0;
		if (this.getSpawnLocation() != null)
			posY = this.getSpawnLocation().yCoord;

		escapeTicks++;
	}

	@Override
	public double getHitboxSize() {
		return 0.5;
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public long getBlowPower() {
		return 16777216;
	}

	@Override
	public double getMaxDeflection() {
		return 0.5;
	}

	public void resetEscapeTimer() {
		escapeTicks = 0;
	}

	public boolean hasEscaped() {
		return escapeTicks >= 6; //was 4
	}

	public boolean hasEscapedSeverely() {
		return escapeTicks >= 12; //was 8
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		return false;
	}

	@Override
	protected double getDespawnDistance() {
		return 100;
	}

}
