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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityBreederCore extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Temperatured {

	private ItemStack[] inv = new ItemStack[8];

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && rand.nextInt(20) == 0)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
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
		}
	}

	private boolean isFissile() {
		return ReikaInventoryHelper.locateInInventory(ReactorItems.BREEDERFUEL.getShiftedItemID(), inv) != -1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.itemID == ReactorItems.BREEDERFUEL.getShiftedItemID();
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
		return 700;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (ReikaRandomHelper.doWithChance(5)) {
			int slot = ReikaInventoryHelper.locateInInventory(ReactorItems.BREEDERFUEL.getShiftedItemID(), inv);
			if (slot != -1) {
				inv[slot] = ReactorItems.PLUTONIUM.getStackOf();
				temperature += 20;
				this.spawnNeutronBurst(world, x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		return is.itemID == ReactorItems.PLUTONIUM.getShiftedItemID();
	}

	@Override
	public int getIndex() {
		return ReactorTiles.BREEDER.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

}
