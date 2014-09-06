/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorFuel;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityFuelRod extends TileEntityNuclearCore {

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.FUEL.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (inv[i] != null)
			return false;
		if (is.getItem() == ReactorItems.FUEL.getItemInstance())
			return i < 4;
		if (is.getItem() == ReactorItems.PLUTONIUM.getItemInstance())
			return i < 4;
		if (is.getItem() == ReactorItems.DEPLETED.getItemInstance())
			return i < 4;
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

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		super.onNeutron(e, world, x, y, z);
		if (!world.isRemote) {
			if (this.isPoisoned())
				return true;
			if (this.isFissile() && ReikaRandomHelper.doWithChance(25)) {
				ReactorAchievements.FISSION.triggerAchievement(this.getPlacer());
				int slot = -1;
				for (int i = 3; i >= 0; i--) {
					ItemStack is = inv[i];
					if (is != null && is.getItem() == ReactorItems.FUEL.getItemInstance()) {
						slot = i;
						i = -1;
					}
				}
				if (slot != -1) {
					if (ReikaRandomHelper.doWithChance(3)) {
						ItemStack is = inv[slot];
						inv[slot] = ReactorFuel.URANIUM.getFissionProduct(is);

						if (ReikaRandomHelper.doWithChance(5)) {
							this.addWaste();
						}
					}

					this.spawnNeutronBurst(world, x, y, z);
					//double E = Math.pow(ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY), 0.33);
					//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
					//storedEnergy += E;
					temperature += 20;
					return true;
				}
				else {
					slot = ReikaInventoryHelper.locateIDInInventory(ReactorItems.PLUTONIUM.getItemInstance(), this);
					if (slot != -1) {

						if (ReikaRandomHelper.doWithChance(4)) {
							inv[slot] = ReactorFuel.PLUTONIUM.getFissionProduct(inv[slot]);

							if (ReikaRandomHelper.doWithChance(10)) {
								this.addWaste();
							}
						}

						this.spawnNeutronBurst(world, x, y, z);
						//double E = Math.pow(ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY), 0.33);
						//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
						//storedEnergy += E;
						temperature += 30;
						return true;
					}
				}
			}
		}
		return false;
	}

	private int getFirstFuelSlot() {
		int fuel = ReikaInventoryHelper.locateIDInInventory(ReactorItems.FUEL.getItemInstance(), this);
		return fuel;
	}

	@Override
	public boolean isFissile() {
		for (int i = 0; i < ReactorFuel.fuelList.length; i++) {
			Item id = ReactorFuel.fuelList[i].getFuelItem().getItem();
			if (ReikaInventoryHelper.checkForItem(id, inv))
				return true;
		}
		return false;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		hydrogen = NBT.getInteger("h2");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("h2", hydrogen);
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq.isWater();
	}
}
