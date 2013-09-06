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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWasteContainer extends TileEntityInventoriedReactorBase {

	public static final int WIDTH = 9;
	public static final int HEIGHT = 3;

	private ItemStack[] inv = new ItemStack[WIDTH*HEIGHT];

	@Override
	public int getIndex() {
		return ReactorTiles.WASTECONTAINER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();

		if (thermalTicker.checkCap()) {
			int waste = this.countWaste();
			temperature += ReikaNuclearHelper.getWasteDecayHeat();
			this.distributeHeat(world, x, y, z);
		}

		this.leakRadiation(world, x, y, z);

		if (!world.isRemote)
			this.decayWaste();

		//this.fill();
	}

	private void distributeHeat(World world, int x, int y, int z) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world.getBiomeGenForCoords(x, z));
		int side = ReikaWorldHelper.checkForAdjSourceBlock(world, x, y, z, Material.water);
		if (side != -1) {
			temperature -= 0.0115*ReikaThermoHelper.getTemperatureIncrease(1, 15000, ReikaThermoHelper.WATER_BLOCK_HEAT);
			if (temperature < Tamb)
				temperature = Tamb;
			//ReikaJavaLibrary.pConsole(temperature);
			ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.waterMoving.blockID, 6);
		}
	}

	private void fill() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] == null) {
				ItemStack is = ReactorItems.WASTE.getStackOf();
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("iso", Isotopes.I131.ordinal());
				inv[i] = is;
			}
		}
	}

	private void leakRadiation(World world, int x, int y, int z) {

	}

	private void decayWaste() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null && inv[i].itemID == ReactorItems.WASTE.getShiftedItemID() && inv[i].stackTagCompound != null) {
				Isotopes atom = Isotopes.getIsotope(inv[i].stackTagCompound.getInteger("iso"));
				//ReikaJavaLibrary.pConsole(ReikaNuclearHelper.getDecayChanceFromHalflife(atom.getMCHalfLife()));
				if (ReikaNuclearHelper.shouldDecay(atom)) {
					inv[i] = null;
				}
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean hasWaste() {
		return this.countWaste() > 0;
	}

	public int countWaste() {
		int count = 0;
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == ReactorItems.WASTE.getShiftedItemID()) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return itemstack.itemID == ReactorItems.DEPLETED.getShiftedItemID();
	}

	@Override
	public int getSizeInventory() {
		return WIDTH*HEIGHT;
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
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return itemstack.itemID == ReactorItems.WASTE.getShiftedItemID();
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

}
