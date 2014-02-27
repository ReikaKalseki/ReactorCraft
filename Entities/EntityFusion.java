/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorSounds;

public class EntityFusion extends InertEntity {

	public EntityFusion(World world) {
		super(world);
	}

	public EntityFusion(World world, double x, double y, double z, String creator) {
		super(world);
		this.setPosition(x, y, z);
		//world.setBlock(MathHelper.floor_double(x), MathHelper.floor_double(y)+1, MathHelper.floor_double(z), 51);
		//worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 0.2F, 0.1F);
		for (int i = 0; i < 3; i++)
			this.spawnNeutrons(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
		ReactorSounds.FUSION.playSound(worldObj, posX, posY, posZ, 1, 1);

		if (creator != null && !creator.isEmpty())
			ReactorAchievements.FUSION.triggerAchievement(world.getPlayerEntityByName(creator));
	}

	@Override
	protected void entityInit() {

	}

	private void spawnNeutrons(World world, int x, int y, int z) {
		EntityFusionNeutron e = new EntityFusionNeutron(world, x, y, z, this.getRandomDirection());
		if (!world.isRemote)
			world.spawnEntityInWorld(e);
	}

	public ForgeDirection getRandomDirection() {
		int r = 2+rand.nextInt(4);
		return ForgeDirection.VALID_DIRECTIONS[r];
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 5)
			this.setDead();
	}

}
