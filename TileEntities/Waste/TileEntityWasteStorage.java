/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Waste;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.ReactorCraft.Base.TileEntityWasteUnit;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.RangedEffect;

public class TileEntityWasteStorage extends TileEntityWasteUnit implements RangedEffect, Feedable {

	@Override
	public int getSizeInventory() {
		return 12;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (rand.nextInt(20) == 0)
			this.sickenMobs(world, x, y, z);

		if (!world.isRemote) {
			this.decayWaste();
			this.feed();
		}

		if (world.provider.isHellWorld || ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z) > 100) {
			if (this.hasWaste()) {
				ReikaParticleHelper.SMOKE.spawnAroundBlock(world, x, y, z, 3);
				if (rand.nextInt(4) == 0)
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
				if (rand.nextInt(200) == 0) {
					world.setBlockToAir(x, y, z);
					world.newExplosion(null, x+0.5, y+0.5, y+0.5, 4F, true, true);
				}
			}
		}
	}

	@Override
	protected boolean accountForOutGameTime() {
		return true;
	}

	@Override
	protected void onDecayWaste(int i) {
		super.onDecayWaste(i);
		if (ReikaInventoryHelper.isEmpty(this))
			ReactorAchievements.DECAY.triggerAchievement(this.getPlacer());
	}

	private void sickenMobs(World world, int x, int y, int z) {
		int r = this.getRange();
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			if (!RadiationIntensity.MODERATE.hasSufficientShielding(e)) {
				double dd = ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5);
				if (ReikaWorldHelper.canBlockSee(world, x, y, z, e.posX, e.posY, e.posZ, dd)) {
					RadiationEffects.instance.applyEffects(e, RadiationIntensity.MODERATE);
				}
			}
		}
	}

	@Override
	protected boolean isValidSlot(int i, ItemStack is) {
		return this.isAppropriateWasteSlot(is, i);
	}

	private boolean isAppropriateWasteSlot(ItemStack is, int slot) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (ReikaItemHelper.matchStacks(is, in)) {
				if (in.stackSize+is.stackSize <= Math.min(this.getInventoryStackLimit(), is.getMaxStackSize())) {
					return i == slot;
				}
			}
		}
		return inv[slot] == null;
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.STORAGE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean leaksRadiation() {
		return false;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		return false;
	}

	@Override
	public boolean isValidIsotope(Isotopes i) {
		return this.isLongLivedWaste(i);//i.getMCHalfLife() > ReikaTimeHelper.YEAR.getMinecraftDuration();
	}

	@Override
	public int getRange() {
		int amt = this.countWaste();
		return this.getRangeFromWasteCount(amt);
	}

	@Override
	public int getMaxRange() {
		int amt = this.getSizeInventory();
		return this.getRangeFromWasteCount(amt);
	}

	public int getRangeFromWasteCount(int amt) {
		return (int)Math.sqrt(amt);
	}

	@Override
	public final int getInventoryStackLimit() {
		return 16;
	}

	public boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		Block id = world.getBlock(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (tile instanceof TileEntityWasteStorage) {
			if (((Feedable)tile).feedIn(inv[inv.length-1])) {
				for (int i = inv.length-1; i > 0; i--)
					inv[i] = inv[i-1];

				id = world.getBlock(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = this.getAdjacentTileEntity(ForgeDirection.UP);
				if (tile instanceof TileEntityWasteStorage) {
					inv[0] = ((Feedable) tile).feedOut();
				}
				else
					inv[0] = null;
			}
		}
		this.collapseInventory();
		return false;
	}

	private void collapseInventory() {
		for (int i = 0; i < inv.length; i++) {
			for (int k = inv.length-1; k > 0; k--) {
				if (inv[k-1] != null) {
					if (inv[k] == null) {
						inv[k] = inv[k-1];
						inv[k-1] = null;
					}
					else if (ReikaItemHelper.matchStacks(inv[k], inv[k-1]) && ItemStack.areItemStackTagsEqual(inv[k], inv[k-1]) && inv[k].stackSize+inv[k-1].stackSize <= Math.min(this.getInventoryStackLimit(), inv[k].getMaxStackSize())) {
						inv[k].stackSize += inv[k-1].stackSize;
						inv[k-1] = null;
					}
				}
			}
		}
	}

	@Override
	public boolean feedIn(ItemStack is) {
		if (is == null)
			return true;
		if (!this.isItemValidForSlot(0, is))
			return false;
		if (inv[0] == null) {
			inv[0] = is.copy();
			return true;
		}
		return false;
	}

	@Override
	public ItemStack feedOut() {
		if (inv[inv.length-1] == null)
			return null;
		else {
			ItemStack is = inv[inv.length-1].copy();
			inv[inv.length-1] = null;
			return is;
		}
	}

	@Override
	protected boolean canBeAccelerated() {
		return true;
	}

	@Override
	protected double getBaseDecayRate() {
		return 1.75;
	}

}
