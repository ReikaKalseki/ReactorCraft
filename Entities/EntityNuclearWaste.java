/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Entities;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;

public final class EntityNuclearWaste extends EntityItem {

	public static final int RANGE = 6;
	private int timer = 0;

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
		if (posY < 0) {
			//motionY = 0;
			//posY = 0;
			if (!worldObj.isRemote)
				velocityChanged = true;
			motionY = Math.abs(motionY); //try 8?
			posY = Math.max(posY, 0);

			if (timer%256 == 0) {
				AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, RANGE);
				List<EntityRadiation> li = worldObj.getEntitiesWithinAABB(EntityRadiation.class, box);
				if (li.size() < 100) {
					int ix = MathHelper.floor_double(posX);
					int iy = MathHelper.floor_double(posY);
					int iz = MathHelper.floor_double(posZ);
					RadiationEffects.instance.contaminateArea(worldObj, ix, iy, iz, RANGE*4, 2, 0, false, RadiationIntensity.HIGHLEVEL);
				}
			}
		}
		timer++;
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
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(RANGE, RANGE, RANGE);
		List<EntityLivingBase> inbox = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : inbox) {
			double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
			if (dd <= RANGE) {
				//if (!RadiationEffects.instance.hasHazmatSuit(e))
				RadiationEffects.instance.applyEffects(e, RadiationIntensity.HIGHLEVEL);
			}
		}

		int ix = MathHelper.floor_double(x);
		int iy = MathHelper.floor_double(y);
		int iz = MathHelper.floor_double(z);

		//Contaminate the area slightly every 10 min left in the world
		if (timer%12000 == 0 && timer >= 18000) {
			AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(12, 8, 12);
			List<EntityRadiation> near = world.getEntitiesWithinAABB(EntityRadiation.class, box);
			if (near.size() < 32) {
				RadiationEffects.instance.contaminateArea(world, ix, iy, iz, RANGE*4, 2, 0, false, RadiationIntensity.HIGHLEVEL); //no LOS to simulate groundwater/air particulates
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float dmg) {
		return false;
	}

	@Override
	public void setAgeToCreativeDespawnTime()
	{

	}

}
