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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.InertEntity;

public class EntityFusion extends InertEntity {

	public EntityFusion(World world) {
		super(world);
	}

	public EntityFusion(World world, double x, double y, double z) {
		super(world);
		this.setPosition(x, y, z);
		//world.setBlock(MathHelper.floor_double(x), MathHelper.floor_double(y)+1, MathHelper.floor_double(z), 51);
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 5)
			this.setDead();
	}

}
