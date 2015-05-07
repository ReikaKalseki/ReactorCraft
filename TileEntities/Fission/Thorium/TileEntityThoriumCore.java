/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Thorium;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityThoriumCore extends TileEntityNuclearCore {

	private StepTimer timer2 = new StepTimer(20);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(temperature+":"+this, temperature > 700);

		if (DragonAPICore.debugtest) {
			ReikaInventoryHelper.clearInventory(this);
			ReikaInventoryHelper.addToIInv(ReactorItems.THORIUM.getStackOf(), this);
		}

		timer2.update();

		if (timer2.checkCap()) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
				/*
				if (r == ReactorTiles.SODIUMBOILER) {
					TileEntitySodiumHeater te = (TileEntitySodiumHeater)world.getTileEntity(dx, dy, dz);
					int dTemp = temperature-te.getTemperature();
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.setTemperature(te.getTemperature()+dTemp/16);
					}
				}
				 */
			}
		}
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (inv[i] != null)
			return false;
		if (is.getItem() == ReactorItems.THORIUM.getItemInstance())
			return i < 4;
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		super.onNeutron(e, world, x, y, z);
		if (!world.isRemote) {
			if (this.checkPoisonedChance())
				return true;
			if (ReikaRandomHelper.doWithChance(25) && this.isFissile()) {
				int slot = ReikaInventoryHelper.locateInInventory(ReactorItems.THORIUM.getItemInstance(), inv);
				if (slot != -1) {
					if (e.getType() == NeutronType.THORIUM && ReikaRandomHelper.doWithChance(5)) {
						int dmg = inv[slot].getItemDamage();
						if (dmg == ReactorItems.THORIUM.getNumberMetadatas()-1) {
							inv[slot] = null;
						}
						else {
							inv[slot] = ReactorItems.THORIUM.getStackOfMetadata(dmg+1);
						}
						temperature += 50;
					}
					else {
						temperature += 20;
					}
					this.spawnNeutronBurst(world, x, y, z);

					if (ReikaRandomHelper.doWithChance(2.5)) {
						this.addWaste();
					}

					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isFissile() {
		return ReikaInventoryHelper.locateInInventory(ReactorItems.THORIUM.getItemInstance(), inv) != -1;
	}

	@Override
	public int getMaxTemperature() {
		return 1600;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		if (is.getItem() == ReactorItems.WASTE.getItemInstance())
			return true;
		return false;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.THORIUM.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
