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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.ReactorCraft.Feedable;
import Reika.ReactorCraft.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityFuelRod extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Feedable {

	private ItemStack[] inv = new ItemStack[9];

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (par5Random.nextInt(20) == 0)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.FUEL.ordinal();
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack is) {
		inv[i] = is;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack is) {
		return is.itemID == ReactorItems.FUEL.getShiftedItemID();
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (this.isFissile() && ReikaMathLibrary.doWithChance(25)) {
			this.spawnNeutronBurst(world, x, y, z);
			double E = ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY);
			//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
			return true;
		}
		return false;
	}

	@Override
	public double getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(double T) {
		temperature = T;
	}

	public ForgeDirection getRandomDirection() {
		int r = 2+par5Random.nextInt(4);
		return ForgeDirection.values()[r];
	}

	private void spawnNeutronBurst(World world, int x, int y, int z) {
		for (int i = 0; i < 3; i++)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
	}

	public boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		int id = world.getBlockId(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = world.getBlockTileEntity(x, y-1, z);
		if (this.isThisTE(id, meta)) {
			TileEntityFuelRod te = (TileEntityFuelRod)tile;
			int slot =	this.getFirstFuelSlot();
			if (slot == -1)
				return false;
			if (ReikaInventoryHelper.addToIInv(inv[slot], te)) {
				ReikaInventoryHelper.decrStack(slot, inv);
				return true;
			}
			else
				return false;
		}
		else if (tile instanceof Feedable) {

		}
		return false;
	}

	private int getFirstFuelSlot() {
		int fuel = ReikaInventoryHelper.locateIDInInventory(ReactorItems.FUEL.getShiftedItemID(), this);
		return fuel;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public boolean isFissile() {
		return ReikaInventoryHelper.checkForItem(ReactorItems.FUEL.getShiftedItemID(), inv);
	}

	@Override
	public boolean feedIn(ItemStack is) {
		return ReikaInventoryHelper.addToIInv(is, this);
	}

	@Override
	public ItemStack feedOut() {
		if (this.isFissile())
			return inv[this.getFirstFuelSlot()];
		else
			return null;
	}
}
