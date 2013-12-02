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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.RotaryCraft.Auxiliary.ItemStacks;

public class EntityNeutron extends ParticleEntity {

	public EntityNeutron(World world, int x, int y, int z, ForgeDirection f) {
		super(world);
		this.setLocationAndAngles(x+0.5, y+0.5, z+0.5, 0, 0);
		motionX = f.offsetX*this.getSpeed();
		motionY = f.offsetY*this.getSpeed();
		motionZ = f.offsetZ*this.getSpeed();
		oldBlockX = x;
		oldBlockY = y;
		oldBlockZ = z;
	}

	public EntityNeutron(World par1World) {
		super(par1World);
	}

	@Override
	public void applyEntityCollision(Entity e)
	{
		if (ReikaRandomHelper.doWithChance(12.5)) {
			if (e instanceof EntityLivingBase) {
				RadiationEffects.applyPulseEffects((EntityLivingBase)e);
				this.setDead();
			}
		}
	}

	@Override
	public boolean onEnterBlock(World world, int x, int y, int z) {
		oldBlockX = x;
		oldBlockY = y;
		oldBlockZ = z;

		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		if (id != 0) {
			Block b = Block.blocksList[id];
			if (b.hasTileEntity(meta)) {
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (te instanceof ReactorCoreTE) {
					return ((ReactorCoreTE)te).onNeutron(this, world, x, y, z);
				}
			}

			if (ReikaItemHelper.matchStacks(ItemStacks.steelblock, new ItemStack(id, 1, meta))) {
				return ReikaRandomHelper.doWithChance(90);
			}
			if (id == ReactorBlocks.MATS.getBlockID() && meta == MatBlocks.CONCRETE.ordinal()) {
				return ReikaRandomHelper.doWithChance(60);
			}
			if ((id == ReactorBlocks.FLUORITE.getBlockID() || id == ReactorBlocks.FLUORITEORE.getBlockID()) && meta < FluoriteTypes.colorList.length) {
				world.setBlock(x, y, z, id, meta+8, 3);
				world.markBlockForRenderUpdate(x, y, z);
			}
			if (id == Block.waterMoving.blockID || id == Block.waterStill.blockID)
				return ReikaRandomHelper.doWithChance(30);

			boolean flag = b.isOpaqueCube() ? b.getExplosionResistance(null, world, x, y, z, x, y, z) >= 12 || rand.nextInt((int)(12 - b.getExplosionResistance(null, world, x, y, z, x, y, z))) == 0 : 256-b.getLightOpacity(world, x, y, z) == 0 ? rand.nextInt(b.getLightOpacity(world, x, y, z)) > 0 : rand.nextInt(1000) == 0;
			if (flag) {
				if (ReikaRandomHelper.doWithChance(20)) {
					//AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(8, 8, 8);
					//List inbox = world.getEntitiesWithinAABB(EntityRadiation.class, box);
					//if (inbox.size() < 10)
					//	RadiationEffects.contaminateArea(world, x, y, z, 1);
				}
				if (ReikaRandomHelper.doWithChance(20))
					RadiationEffects.transformBlock(world, x, y, z);
			}
			return flag;
		}

		return rand.nextInt(1000) == 0;
	}

	@Override
	public double getSpeed() {
		return 0.75;
	}

	@Override
	protected void onTick() {

	}

	@Override
	public double getHitboxSize() {
		return 0.1;
	}

}
