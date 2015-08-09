/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Registry.RadiationShield;

public class HydrogenExplosion extends Explosion {

	private static final Random rand = new Random();

	private World world;
	private final double scatterFraction;

	public HydrogenExplosion(World world, Entity e, double x, double y, double z, float power) {
		this(world, e, x, y, z, power, 1);
	}

	public HydrogenExplosion(World world, Entity e, double x, double y, double z, float power, double fraction) {
		super(world, e, x, y, z, power);
		this.world = world;
		scatterFraction = fraction;
	}

	@Override
	public void doExplosionA() {
		int r = (int)explosionSize+2;
		int x = (int)Math.floor(explosionX);
		int y = (int)Math.floor(explosionY);
		int z = (int)Math.floor(explosionZ);

		//scatterFraction = RadiationEffects.instance.contaminateArea(world, x, y, z, (int)(explosionSize*4), 8, 2, true);

		List<EntityFallingBlock> li = new ArrayList();
		if (!world.isRemote) {
			for (int i = x-r; i <= x+r; i++) {
				for (int j = y-r; j <= y+r; j++) {
					for (int k = z-r; k <= z+r; k++) {
						if (scatterFraction == 1 || ReikaRandomHelper.doWithChance(scatterFraction)) {
							Block id = world.getBlock(i, j, k);
							int meta = world.getBlockMetadata(i, j, k);
							RadiationShield rs = RadiationShield.getFrom(id, meta);
							if (rs == null || ReikaRandomHelper.doWithChance(rs.radiationDeflectChance)) {
								if (this.canEntitize(world, i, j, k, id, meta)) {
									EntityFallingBlock e = new EntityFallingBlock(world, i, j, k, id, meta);
									li.add(e);
									e.field_145812_b = -10000;
									e.field_145813_c = false;
									world.setBlockToAir(i, j, k);
									world.spawnEntityInWorld(e);
								}
							}
						}
					}
				}
			}
		}

		super.doExplosionA();

		for (EntityFallingBlock e : li) {
			double dx = e.posX-explosionX;
			double dy = e.posY-explosionY;
			double dz = e.posZ-explosionZ;

			double dd = 0.3;
			double vy = 1.5;

			e.motionX = dx*dd*rand.nextDouble();
			e.motionY = dy*dd*rand.nextDouble()+vy;
			e.motionZ = dz*dd*rand.nextDouble();

			e.velocityChanged = true;
		}
	}

	@Override
	public void doExplosionB(boolean fire) {
		super.doExplosionB(fire);
		/*
		int x = (int)Math.floor(explosionX);
		int y = (int)Math.floor(explosionY);
		int z = (int)Math.floor(explosionZ);
		RadiationEffects.instance.contaminateArea(world, x, y, z, (int)(explosionSize*4), 8, true);
		 */
	}

	private boolean canEntitize(World world, int x, int y, int z, Block id, int meta) {
		if (id == Blocks.air)
			return false;
		if (id == Blocks.bedrock)
			return false;
		if (id.hasTileEntity(meta))
			return false;
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		if (id.getRenderType() != 0) //To prevent weird looking flying sand entities
			return false;
		double dd = ReikaMathLibrary.py3d(x+0.5-explosionX, y+0.5-explosionY, z+0.5-explosionZ);
		return dd <= explosionSize+0.5;
	}

}
