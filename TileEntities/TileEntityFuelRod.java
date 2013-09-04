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
import Reika.DragonAPI.Libraries.ReikaMathLibrary;
import Reika.ReactorCraft.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityFuelRod extends TileEntityInventoriedReactorBase implements ReactorCoreTE {

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
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (ReikaMathLibrary.doWithChance(25)) {
			this.spawnNeutronBurst(world, x, y, z);
			return true;
		}
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
		if (id == this.getTileEntityBlockID() && meta == this.getIndex()) {
			TileEntityFuelRod te = (TileEntityFuelRod)world.getBlockTileEntity(x, y-1, z);
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
}
