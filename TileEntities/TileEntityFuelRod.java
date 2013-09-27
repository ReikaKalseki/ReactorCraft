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

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.FuelNetwork;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityFuelRod extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Feedable {

	private ItemStack[] inv = new ItemStack[4];

	private FuelNetwork network;

	public double storedEnergy = 0;
	private ArrayList<ItemStack> missingWaste = new ArrayList();

	@Override
	public void getOrCreateNetwork(World world, int x, int y, int z) {
		FuelNetwork ntw = new FuelNetwork();
		ntw.addFuelCell(this);
		boolean flag = false;
		for (int i = 0; i < 6; i++) {
			int dx = x+dirs[i].offsetX;
			int dy = y+dirs[i].offsetY;
			int dz = z+dirs[i].offsetZ;
			TileEntity te = world.getBlockTileEntity(dx, dy, dz);
			if (te instanceof Feedable) {
				FuelNetwork net = ((Feedable)te).getNetwork();
				//ReikaJavaLibrary.pConsole(te.toString()+" with "+net.toString());
				if (net != null) {
					net.merge(ntw);
					this.setNetwork(net);
					ntw = net;
					flag = true;
				}
			}
		}
		if (!flag)
			this.setNetwork(ntw);
	}

	public void deleteFromNetwork() {
		if (network != null)
			network.deleteFuelCell(this);
	}

	@Override
	public FuelNetwork getNetwork() {
		return network;
	}

	@Override
	public void setNetwork(FuelNetwork fuel) {
		network = fuel;
	}

	public boolean hasNetworkAdjacent(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			int dx = x+dirs[i].offsetX;
			int dy = y+dirs[i].offsetY;
			int dz = z+dirs[i].offsetZ;
			TileEntity te = world.getBlockTileEntity(dx, dy, dz);
			if (te instanceof Feedable) {
				FuelNetwork net = ((Feedable)te).getNetwork();
				if (net != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && par5Random.nextInt(20) == 0)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
		//ReikaInventoryHelper.addToIInv(ReactorItems.FUEL.getStackOf(), this);
		this.feed();
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
		if (inv[i] != null)
			return false;
		if (is.itemID == ReactorItems.FUEL.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.DEPLETED.getShiftedItemID())
			return true;
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack is, int j) {
		if (j != 0)
			return false;
		if (is.itemID == ReactorItems.FUEL.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.WASTE.getShiftedItemID())
			return true;
		if (is.itemID == ReactorItems.DEPLETED.getShiftedItemID())
			return true;
		return false;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return j == 1 && this.isStackValidForSlot(i, itemstack) && itemstack.itemID != ReactorItems.DEPLETED.getShiftedItemID();
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (!world.isRemote && this.isFissile() && ReikaMathLibrary.doWithChance(25)) {
			if (ReikaMathLibrary.doWithChance(5)) {
				int slot = ReikaInventoryHelper.locateIDInInventory(ReactorItems.FUEL.getShiftedItemID(), this);
				ItemStack is = inv[slot];
				if (is.getItemDamage() < ReactorItems.FUEL.getNumberMetadatas()-1)
					inv[slot] = new ItemStack(is.itemID, is.stackSize, 1+is.getItemDamage());
				else
					inv[slot] = ReactorItems.DEPLETED.getCraftedProduct(is.stackSize);

				if (ReikaMathLibrary.doWithChance(10)) {
					ItemStack waste = WasteManager.getRandomWasteItem();
					if (!ReikaInventoryHelper.addToIInv(waste, this))
						missingWaste.add(waste);
				}
			}
			this.spawnNeutronBurst(world, x, y, z);
			//double E = Math.pow(ReikaNuclearHelper.AVOGADRO*ReikaNuclearHelper.getEnergyJ(ReikaNuclearHelper.URANIUM_FISSION_ENERGY), 0.33);
			//temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.GRAPHITE_HEAT, ReikaEngLibrary.rhographite, E);
			//storedEnergy += E;

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
			if (((Feedable) tile).feedIn(inv[3])) {
				inv[3] = inv[2];
				inv[2] = inv[1];
				inv[1] = inv[0];

				id = world.getBlockId(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = world.getBlockTileEntity(x, y+1, z);
				if (tile instanceof Feedable) {
					inv[0] = ((Feedable) tile).feedOut();
				}
				else
					inv[0] = null;
			}
		}
		this.collapseInventory();
		return false;
	}

	private void collapseInventory() {
		for (int i = 0; i < 4; i++) {
			for (int k = 3; k > 0; k--) {
				if (inv[k] == null) {
					if (!missingWaste.isEmpty()) {
						inv[k] = missingWaste.get(0);
						missingWaste.remove(0);
					}
					else {
						inv[k] = inv[k-1];
						inv[k-1] = null;
					}
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
		if (inv[0] == null) {
			inv[0] = is.copy();
			return true;
		}
		return false;
	}

	@Override
	public ItemStack feedOut() {
		if (inv[3] == null)
			return null;
		else {
			ItemStack is = inv[3].copy();
			if (!missingWaste.isEmpty()) {
				inv[3] = missingWaste.get(0);
				missingWaste.remove(0);
			}
			else {
				inv[3] = null;
			}
			return is;
		}
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

		storedEnergy = NBT.getDouble("energy");
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

		NBT.setDouble("energy", storedEnergy);
	}

	@Override
	public int getMaxTemperature() {
		return 300;
	}

	private void onMeltdown(World world, int x, int y, int z) {

	}
}
