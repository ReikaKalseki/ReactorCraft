/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.ItemReactorTool;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.API.Interfaces.ChargeableTool;

public class ItemRadiationCleaner extends ItemReactorTool implements ChargeableTool {

	private static final int CAPACITY = 4000;
	private static final int WATER_PER_TICK = 5;
	private static final int TICK_PER_KJ = 5;

	public ItemRadiationCleaner(int tex) {
		super(tex);
		hasSubtypes = false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote && this.getWater(is) < CAPACITY) {
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, true);
			if (mov != null) {
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;
				Block id = world.getBlock(x, y, z);
				if (id == Blocks.water && ReikaWorldHelper.isLiquidSourceBlock(world, x, y, z)) {
					this.addWater(is, 1000);
					world.setBlock(x, y, z, Blocks.air);
					return is;
				}
			}
		}

		ep.setItemInUse(is, this.getMaxItemUseDuration(is));

		return is;
	}

	@Override
	public void onUsingTick(ItemStack is, EntityPlayer ep, int count) {
		if (is.getItemDamage() > 0 && this.getWater(is) > 0) {
			double r = 1;
			double d = ReikaRandomHelper.getRandomPlusMinus(2.5, 2);
			Vec3 vec = ep.getLookVec();
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+ep.getEyeHeight()+vec.yCoord*d;
			double dz = ep.posZ+vec.zCoord*d;
			if (count%TICK_PER_KJ == 0) {
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(r, r, r);
				List<EntityRadiation> li = ep.worldObj.getEntitiesWithinAABB(EntityRadiation.class, box);
				for (EntityRadiation e : li) {
					e.clean();
				}
			}
			int n = ReikaRandomHelper.getRandomPlusMinus(8, 4);
			for (int i = 0; i < n; i++) {
				double v = ReikaRandomHelper.getRandomPlusMinus(0.1875, 0.0625);
				double vx = vec.xCoord*v;
				double vy = vec.yCoord*v;
				double vz = vec.zCoord*v;

				vx = ReikaRandomHelper.getRandomPlusMinus(vx, 0.001);
				vz = ReikaRandomHelper.getRandomPlusMinus(vz, 0.001);

				ReikaParticleHelper.RAIN.spawnAt(ep.worldObj, dx, dy, dz, vx, vy, vz);
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack is, World world, EntityPlayer ep, int ticksLeft) {
		int used = this.getMaxItemUseDuration(is)-ticksLeft;
		is.setItemDamage(Math.max(0, is.getItemDamage()-(used/TICK_PER_KJ)));
		this.addWater(is, -WATER_PER_TICK*used);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add(String.format("Water: %d/%d mB", this.getWater(is), CAPACITY));
	}

	private int getWater(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("water") : 0;
	}

	private void addWater(ItemStack is, int amt) {
		this.setWater(is, amt+this.getWater(is));
	}

	private void setWater(ItemStack is, int level) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("water", Math.min(CAPACITY, level));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is)
	{
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is)
	{
		return Math.min(72000, Math.min(is.getItemDamage()*TICK_PER_KJ, this.getWater(is)/WATER_PER_TICK));
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep)
	{
		return is;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs tab, List li) {
		li.add(ReactorItems.CLEANUP.getStackOf());

		ItemStack is = ReactorItems.CLEANUP.getStackOfMetadata(32000);
		this.setWater(is, CAPACITY);
		li.add(is);
	}

	@Override
	public int setCharged(ItemStack is, int charge, boolean strongcoil) {
		int ret = is.getItemDamage();
		is.setItemDamage(charge);
		return ret;
	}
}
