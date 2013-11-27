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

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.RotaryCraft.Auxiliary.ItemStacks;

public class EntityNeutron extends InertEntity {

	private int oldBlockX;
	private int oldBlockY;
	private int oldBlockZ;

	private static final double SPEED = 0.75;

	public EntityNeutron(World world, int x, int y, int z, ForgeDirection f) {
		super(world);
		this.setLocationAndAngles(x+0.5, y+0.5, z+0.5, 0, 0);
		motionX = f.offsetX*SPEED;
		motionY = f.offsetY*SPEED;
		motionZ = f.offsetZ*SPEED;
		oldBlockX = x;
		oldBlockY = y;
		oldBlockZ = z;
	}

	public EntityNeutron(World par1World) {
		super(par1World);
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {

	}

	@Override
	public void onUpdate()
	{
		this.onEntityUpdate();

		if (motionX == 0 && motionY == 0 && motionZ == 0 && ticksExisted > 20)
			this.setDead();
		if (posY > 256 && posY < 0)
			this.setDead();

		//ReikaJavaLibrary.pConsole(String.format("%d, %d, %d :: %d, %d, %d", oldBlockX, oldBlockY, oldBlockZ, this.getBlockX(), this.getBlockY(), this.getBlockZ()));
		//ReikaJavaLibrary.pConsole(this.getBlockX()+", "+this.getBlockY()+", "+this.getBlockZ());
		if (this.isNewBlock()) {
			if (this.onEnterBlock(worldObj, this.getBlockX(), this.getBlockY(), this.getBlockZ())) {
				this.setDead();
			}
		}

		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(posX, posY, posZ, posX, posY, posZ).expand(0.1, 0.1, 0.1);
		List<Entity> inbox = worldObj.getEntitiesWithinAABB(Entity.class, box);
		for (int i = 0; i < inbox.size(); i++) {
			this.applyEntityCollision(inbox.get(i));
		}
	}

	public int getBlockX() {
		return (int)Math.floor(posX);
	}

	public int getBlockY() {
		return (int)Math.floor(posY);
	}

	public int getBlockZ() {
		return (int)Math.floor(posZ);
	}

	@Override
	public void applyEntityCollision(Entity e)
	{
		if (ReikaRandomHelper.doWithChance(12.5))
			if (e instanceof EntityLivingBase) {
				RadiationEffects.applyPulseEffects((EntityLivingBase)e);
				this.setDead();
			}
	}

	public boolean isNewBlock() {
		int x = this.getBlockX();
		int y = this.getBlockY();
		int z = this.getBlockZ();
		return !this.compareBlocks(x, y, z);
	}

	public boolean compareBlocks(int x, int y, int z) {
		return x == oldBlockX && y == oldBlockY && z == oldBlockZ;
	}

	public boolean onEnterBlock(World world, int x, int y, int z) {
		oldBlockX = x;
		oldBlockY = y;
		oldBlockZ = z;

		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof ReactorCoreTE) {
			return ((ReactorCoreTE)te).onNeutron(this, world, x, y, z);
		}

		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		Random r = new Random();
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
		if (id != 0) {
			Block b = Block.blocksList[id];
			boolean flag =  b.isOpaqueCube() ? b.getExplosionResistance(null, world, x, y, z, x, y, z) >= 12 || r.nextInt((int)(12 - b.getExplosionResistance(null, world, x, y, z, x, y, z))) == 0 : 256-b.getLightOpacity(world, x, y, z) == 0 ? r.nextInt(b.getLightOpacity(world, x, y, z)) > 0 : r.nextInt(1000) == 0;
			if (flag) {
				if (ReikaRandomHelper.doWithChance(20)) {
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(8, 8, 8);
					List inbox = world.getEntitiesWithinAABB(EntityRadiation.class, box);
					if (inbox.size() < 10)
						RadiationEffects.contaminateArea(world, x, y, z, 1);
				}
				if (ReikaRandomHelper.doWithChance(50))
					RadiationEffects.transformBlock(world, x, y, z);
			}
			return flag;
		}

		return r.nextInt(1000) == 0;
	}

}
