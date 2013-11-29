/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Screwdriverable;

public class TileEntityMagnet extends TileEntityReactorBase implements Screwdriverable {

	private float angle = 0; //0 is +x(E), rotates to -z(N)
	private int alpha = 512;

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNET.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(2, 2, 2);
		double v = 2.5;
		double r = 10;
		double a = v*v/r;
		//int tx = 1091;
		//int tz = -463;
		//angle = (float)ReikaPhysicsHelper.cartesianToPolar(tx-x, 0, tz-z)[2]+90;
		a *= 0.49;
		double[] vec = ReikaPhysicsHelper.polarToCartesian(a, 0, -angle);
		double ax = vec[0];
		double az = vec[2];
		vec = ReikaPhysicsHelper.polarToCartesian(v, 0, -angle+82);
		double vx = vec[0];
		double vz = vec[2];
		List<EntityPlasma> li = world.getEntitiesWithinAABB(EntityPlasma.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityPlasma e = li.get(i);
			if (e.canAffect(this)) {
				e.setLocationAndAngles(x, y, z, 0, 0);
				//e.motionX = vx;
				//e.motionZ = vz;
				e.motionY = 0;
				e.addVelocity(ax, 0, az);
				e.velocityChanged = true;
			}
		}
		if (x == 1081 && z == -463) {
			EntityPlasma e = new EntityPlasma(world);
			e.setLocationAndAngles(x, y, z, 0, 0);
			e.addVelocity(0, 0, v);
			e.addVelocity(ax, 0, az);
			if (!world.isRemote)
				world.spawnEntityInWorld(e);

		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (alpha > 0)
			alpha -= 8;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		angle = NBT.getFloat("ang");
		alpha = NBT.getInteger("al");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setFloat("ang", angle);
		NBT.setInteger("al", alpha);
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z) {
		alpha = 512;
		angle -= 11.25F;
		if (angle < 0)
			angle += 360;
		return true;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z) {
		alpha = 512;
		angle += 11.25F;
		if (angle > 360)
			angle -= 360;
		return true;
	}

	public float getAngle() {
		return angle;
	}

	public int getAlpha() {
		return alpha;
	}
}
