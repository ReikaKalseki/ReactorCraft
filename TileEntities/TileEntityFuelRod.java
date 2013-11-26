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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorFuel;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell.LiquidStates;

public class TileEntityFuelRod extends TileEntityNuclearCore {

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.FUEL.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (inv[i] != null)
			return false;
		if (is.itemID == ReactorItems.FUEL.getShiftedItemID())
			return i < 4;
		if (is.itemID == ReactorItems.DEPLETED.getShiftedItemID())
			return i < 4;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return i >= 4;
			return false;
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack is) {
		if (is.itemID == ReactorItems.FUEL.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.DEPLETED.getShiftedItemID())
			return true;
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (this.isPoisoned())
				return true;
			if (this.isFissile() && ReikaRandomHelper.doWithChance(25)) {
				if (ReikaRandomHelper.doWithChance(10)) {
					int slot = -1;
					for (int i = 3; i >= 0; i--) {
						ItemStack is = inv[i];
						if (is != null && is.itemID == ReactorItems.FUEL.getShiftedItemID()) {
							slot = i;
							i = -1;
						}
					}
					if (slot != -1) {
						ItemStack is = inv[slot];
						inv[slot] = ReactorFuel.URANIUM.getFissionProduct(is);

						if (ReikaRandomHelper.doWithChance(10)) {
							this.addWaste();
						}

						this.spawnNeutronBurst(world, x, y, z);
						//double E = Math.pow(ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY), 0.33);
						//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
						//storedEnergy += E;
						temperature += 10;
						return true;
					}
					else {
						slot = ReikaInventoryHelper.locateIDInInventory(ReactorItems.PLUTONIUM.getShiftedItemID(), this);
						if (slot != -1) {

							inv[slot] = ReactorFuel.PLUTONIUM.getFissionProduct(inv[slot]);

							if (ReikaRandomHelper.doWithChance(10)) {
								this.addWaste();
							}

							this.spawnNeutronBurst(world, x, y, z);
							//double E = Math.pow(ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY), 0.33);
							//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
							//storedEnergy += E;
							temperature += 10;
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private int getFirstFuelSlot() {
		int fuel = ReikaInventoryHelper.locateIDInInventory(ReactorItems.FUEL.getShiftedItemID(), this);
		return fuel;
	}

	@Override
	public boolean isFissile() {
		for (int i = 0; i < ReactorFuel.fuelList.length; i++) {
			int id = ReactorFuel.fuelList[i].getFuelItem().itemID;
			if (ReikaInventoryHelper.checkForItem(id, inv))
				return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		hydrogen = NBT.getInteger("h2");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("h2", hydrogen);
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq.isWater();
	}
}