/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Breeder;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityBreederCore extends TileEntityNuclearCore {

	private StepTimer timer2 = new StepTimer(10);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(temperature+":"+this, temperature > 700);

		timer2.update();

		if (timer2.checkCap()) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
				if (r == ReactorTiles.COOLANT) {
					TileEntityWaterCell w = (TileEntityWaterCell)world.getBlockTileEntity(dx, dy, dz);
					int T = w.getTemperature();
					int dT = temperature-T;
					if (dT > 0) {
						w.setTemperature(T+dT/4);
						temperature -= dT/4;
					}
				}
				if (r == ReactorTiles.SODIUMBOILER) {
					TileEntitySodiumHeater te = (TileEntitySodiumHeater)world.getBlockTileEntity(dx, dy, dz);
					int dTemp = temperature-te.getTemperature();
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.setTemperature(te.getTemperature()+dTemp/16);
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(temperature);

		//ReikaInventoryHelper.addToIInv(ReactorItems.BREEDERFUEL.getStackOf(), this);
	}

	@Override
	public boolean isFissile() {
		return ReikaInventoryHelper.locateInInventory(ReactorItems.BREEDERFUEL.getShiftedItemID(), inv) != -1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (inv[i] != null)
			return false;
		if (itemstack.itemID == ReactorItems.BREEDERFUEL.getShiftedItemID())
			return i < 4;
		return false;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 900;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (this.isPoisoned())
				return true;
			if (this.isFissile() && ReikaRandomHelper.doWithChance(25)) {
				if (ReikaRandomHelper.doWithChance(5)) {
					// Note: this will happen 1.25% of the time
					int slot = ReikaInventoryHelper.locateInInventory(ReactorItems.BREEDERFUEL.getShiftedItemID(), inv);
					if (slot != -1) {
						int dmg = inv[slot].getItemDamage();
						if (dmg == ReactorItems.BREEDERFUEL.getNumberMetadatas()-1) {
							inv[slot] = ReactorItems.PLUTONIUM.getStackOf();
							ReactorAchievements.PLUTONIUM.triggerAchievement(this.getPlacer());
						}
						else {
							inv[slot] = ReactorItems.BREEDERFUEL.getStackOfMetadata(dmg+1);
						}
						temperature += 50;
						this.spawnNeutronBurst(world, x, y, z);

						if (ReikaRandomHelper.doWithChance(10)) {
							this.addWaste();
						}
					}
				}
				else
					// Note: this will happen 23.75% of the time
					temperature += temperature >= 700 ? 30 : 20;
				return true;
			}
			// Note: 75% of the time, we let the neutron pass unhindered
		}
		return false;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		if (is.itemID == ReactorItems.PLUTONIUM.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return true;
		return false;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.BREEDER.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq == LiquidStates.SODIUM;
	}

}
