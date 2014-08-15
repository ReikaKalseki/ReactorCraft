/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaTimeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Base.TileEntityWasteUnit;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.RangedEffect;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityWasteStorage extends TileEntityWasteUnit implements RangedEffect {

	@Override
	public int getSizeInventory() {
		return 12;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.sickenMobs(world, x, y, z);
		this.decayWaste();

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
	protected void decayWaste() {
		super.decayWaste();
		if (ReikaInventoryHelper.isEmpty(this))
			ReactorAchievements.DECAY.triggerAchievement(this.getPlacer());
	}

	private void sickenMobs(World world, int x, int y, int z) {
		int r = this.getRange();
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (!RadiationEffects.hasHazmatSuit(e)) {
				double dd = ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5);
				if (ReikaWorldHelper.canBlockSee(world, x, y, z, e.posX, e.posY, e.posZ, dd)) {
					RadiationEffects.applyEffects(e);
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return this.isLongLivedWaste(itemstack);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.STORAGE.ordinal();
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
		return i.getMCHalfLife() > ReikaTimeHelper.YEAR.getMinecraftDuration();
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
		return amt;
	}

	@Override
	public final int getInventoryStackLimit() {
		return 16;
	}

}