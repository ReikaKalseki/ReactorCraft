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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityFuelRod extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Feedable {

	private ItemStack[] inv = new ItemStack[4];

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && par5Random.nextInt(20) == 0)
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
		return 4;
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
		if (is.itemID == ReactorItems.FUEL.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.DEPLETED.getShiftedItemID())
			return true;
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (!world.isRemote && this.isFissile() && ReikaMathLibrary.doWithChance(25)) {
			int slot = ReikaInventoryHelper.locateIDInInventory(ReactorItems.FUEL.getShiftedItemID(), this);
			ItemStack is = inv[slot];
			if (is.getItemDamage() < ReactorItems.FUEL.getNumberMetadatas()-1)
				inv[slot] = new ItemStack(is.itemID, is.stackSize, 1+is.getItemDamage());
			else
				inv[slot] = ReactorItems.DEPLETED.getCraftedProduct(is.stackSize);
			this.spawnNeutronBurst(world, x, y, z);
			double E = ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY);
			//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);

			if (ReikaMathLibrary.doWithChance(10)) {
				ReikaInventoryHelper.addToIInv(WasteManager.getRandomWasteItem(), this);
			}

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
		return dirs[r];
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
		if (tile instanceof Feedable) {
			((Feedable) tile).feed();
			if (((Feedable) tile).feedIn(inv[3])) {
				inv[3] = inv[2];
				inv[2] = inv[1];
				inv[1] = inv[0];

				id = world.getBlockId(x, y-1, z);
				meta = world.getBlockMetadata(x, y-1, z);
				tile = world.getBlockTileEntity(x, y-1, z);
				if (tile instanceof Feedable) {
					inv[0] = ((Feedable) tile).feedOut();
				}
				else
					inv[0] = null;
			}
		}

		return false;
	}

	//seems to be duping items, but have no idea why
	//may be an inter-Te reaction
	private void collapseInventory() {
		for (int i = 0; i < 4; i++) {
			for (int k = 3; k > 0; k--) {
				if (inv[k] == null) {
					inv[k] = inv[k-1];
					inv[k-1] = null;
				}
			}
		}
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
		if (is == null)
			return true;
		if (!this.isStackValidForSlot(0, is))
			return false;
		if (inv[0] == null || inv[0].stackSize+is.stackSize <= inv[0].getMaxStackSize()) {
			if (inv[0] == null) {
				inv[0] = is.copy();
			}
			else {
				inv[0].stackSize += is.stackSize;
			}
			return true;
		}
		return false;
	}

	@Override
	public ItemStack feedOut() {
		return inv[3];
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		NBTTagList nbttaglist = NBT.getTagList("Items");
		inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length)
			{
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

	@Override
	public int getMaxTemperature() {
		return 0;
	}

	private void onMeltdown(World world, int x, int y, int z) {

	}
}
