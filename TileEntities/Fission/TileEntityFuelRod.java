/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorFuel;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityFuelRod extends TileEntityNuclearCore {

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}
	/*
	private int getSameCoreHeatConductionFraction() {
		return 12;
	}
	 */
	@Override
	public int getIndex() {
		return ReactorTiles.FUEL.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (inv[i] != null)
			return false;
		if (this.isFuel(is))
			return i < 4;
		if (is.getItem() == ReactorItems.DEPLETED.getItemInstance())
			return i < 4;
		return false;
	}

	private boolean isFuel(ItemStack is) {
		if (is.getItem() == ReactorItems.FUEL.getItemInstance())
			return true;
		//if (is.getItem() == ReactorItems.THORIUM.getItemInstance())
		//	return true;
		if (is.getItem() == ReactorItems.PLUTONIUM.getItemInstance())
			return true;
		return false;
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack is) {
		if (is.getItem() == ReactorItems.WASTE.getItemInstance())
			return true;
		if (is.getItem() == ReactorItems.DEPLETED.getItemInstance())
			return true;
		return false;
	}

	private ReactorFuel getFuel() {
		return ReactorFuel.getFrom(inv[3]);
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		super.onNeutron(e, world, x, y, z);
		if (!world.isRemote) {
			if (e.getType().canTriggerFission() && ReikaRandomHelper.doWithChance(e.getNeutronSpeed().getInteractionMultiplier())) {
				if (this.checkPoisonedChance())
					return true;
				if (this.isFissile()) {
					ReactorFuel f = this.getFuel();
					if (ReikaRandomHelper.doWithChance(f.fissionChance+f.voidCoefficient*(temperature-100))) {
						ReactorAchievements.FISSION.triggerAchievement(this.getPlacer());
						if (ReikaRandomHelper.doWithChance(f.consumeChance)) {
							ItemStack is = inv[3];
							inv[3] = f.getFissionProduct(is);
							if (inv[3] != null && inv[3].getItem() != is.getItem())
								this.tryPushSpentFuel(3);
							if (ReikaRandomHelper.doWithChance(f.wasteChance))
								this.addWaste();
						}
						this.spawnNeutronBurst(world, x, y, z);
						temperature += f.temperatureStep;
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isFissile() {
		return this.getFuel() != null;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq.isWater();
	}

	@Override
	public ReactorType getReactorType() {
		return ReactorType.FISSION;
	}
}
