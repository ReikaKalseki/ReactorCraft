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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public class EntityRadiation extends InertEntity implements IEntityAdditionalSpawnData {

	private int effectRange;
	private RadiationIntensity intensity;

	public boolean requireLOS = false;

	public EntityRadiation(World par1World) {
		super(par1World);
	}

	public EntityRadiation(World world, int range, RadiationIntensity ri) {
		super(world);
		effectRange = range;
		intensity = ri;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {
		effectRange = NBT.getInteger("effrange");
		intensity = RadiationIntensity.radiationList[NBT.getInteger("intensity")];

		requireLOS = NBT.getBoolean("los");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {
		NBT.setInteger("effrange", effectRange);
		NBT.setInteger("intensity", intensity.ordinal());

		NBT.setBoolean("los", requireLOS);
	}

	@Override
	public void onUpdate() {
		this.onEntityUpdate();
		this.applyRadiation();

		if (effectRange <= 0)
			this.setDead();

		if (this.decays()) {
			if (rand.nextInt(360000) == 0) {
				this.clean();
			}
		}
		if (this.rainCleanable()) {
			if (worldObj.isRaining()) {
				if (rand.nextInt(36000) == 0) {
					if (worldObj.getBiomeGenForCoords(this.getBlockX(), this.getBlockZ()).canSpawnLightningBolt()) {
						this.clean();
					}
				}
			}
		}
	}

	protected boolean decays() {
		return true;
	}

	protected boolean rainCleanable() {
		return true;
	}

	public final int getBlockX() {
		return (int)Math.floor(posX);
	}

	public final int getBlockY() {
		return (int)Math.floor(posY);
	}

	public final int getBlockZ() {
		return (int)Math.floor(posZ);
	}

	protected void applyRadiation() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(effectRange, effectRange, effectRange);
		List<EntityLivingBase> inbox = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : inbox) {
			double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
			if (dd <= effectRange) {
				RadiationEffects.instance.applyEffects(e, intensity);
			}
		}

		int c = 1;//20
		//if (r.nextInt(c) == 0) {
		int dx = ReikaRandomHelper.getRandomPlusMinus(MathHelper.floor_double(x), effectRange);
		int dy = ReikaRandomHelper.getRandomPlusMinus(MathHelper.floor_double(y), effectRange);
		int dz = ReikaRandomHelper.getRandomPlusMinus(MathHelper.floor_double(z), effectRange);
		RadiationEffects.instance.transformBlock(world, dx, dy, dz, intensity);
		//}
	}

	public void clean() {
		if (effectRange > 0)
			effectRange--;
		else
			this.setDead();
	}

	public int getRange() {
		return effectRange;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(effectRange);
		data.writeInt(intensity.ordinal());
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		effectRange = data.readInt();
		intensity = RadiationIntensity.radiationList[data.readInt()];
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float par2) {
		if (src.isExplosion()) {
			RadiationEffects.instance.contaminateArea(worldObj, this.getBlockX(), this.getBlockY(), this.getBlockZ(), effectRange, 0.65F, 0.5, true, intensity);
			this.setDead();
			return true;
		}
		return super.attackEntityFrom(src, par2);
	}

}
