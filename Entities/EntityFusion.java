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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.InertEntity;

public class EntityFusion extends InertEntity {

	public EntityFusion(World world) {
		super(world);
	}

	public EntityFusion(World world, double x, double y, double z) {
		super(world);
		this.setPosition(x, y, z);
		//world.setBlock(MathHelper.floor_double(x), MathHelper.floor_double(y)+1, MathHelper.floor_double(z), 51);
		worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 0.2F, 0.1F);
		this.spawnNeutrons(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
	}

	@Override
	protected void entityInit() {

	}

	private void spawnNeutrons(World world, int x, int y, int z) {
		world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
	}

	public ForgeDirection getRandomDirection() {
		int r = 2+rand.nextInt(4);
		return ForgeDirection.VALID_DIRECTIONS[r];
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
