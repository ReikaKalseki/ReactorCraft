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
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.TemperatureTE;

public class TileEntityWasteContainer extends TileEntityInventoriedReactorBase implements TemperatureTE {

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
			temperature += waste*ReikaNuclearHelper.getWasteDecayHeat();
			this.updateTemperature(world, x, y, z, meta);
		}

		if (!world.isRemote)
			this.decayWaste();

		//this.fill();
	}

	private void distributeHeat(World world, int x, int y, int z) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world.getBiomeGenForCoords(x, z));
		//ReikaJavaLibrary.pConsole(temperature);
		if (temperature > Tamb) {
			ForgeDirection side = ReikaWorldHelper.checkForAdjSourceBlock(world, x, y, z, Material.water);
			if (side != null) {
				temperature -= ReikaThermoHelper.getTemperatureIncrease(1, 15000, ReikaThermoHelper.WATER_BLOCK_HEAT);
				//ReikaJavaLibrary.pConsole(temperature);
				if (temperature > 100)
					ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, 0, 0);
				else
					ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.waterMoving.blockID, 6);
			}
		}
		//ReikaJavaLibrary.pConsole(temperature);
		if (temperature < Tamb)
			temperature = Tamb;
		if (temperature > this.getMaxTemperature()) {
			this.overheat(world, x, y, z);
		}
		else if (temperature > this.getMaxTemperature()/2 && rand.nextInt(6) == 0) {
			world.spawnParticle("smoke", x+rand.nextDouble(), y+1, z+rand.nextDouble(), 0, 0, 0);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
		}
		else if (temperature > this.getMaxTemperature()/4 && rand.nextInt(20) == 0) {
			world.spawnParticle("smoke", x+rand.nextDouble(), y+1, z+rand.nextDouble(), 0, 0, 0);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
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
		ForgeDirection dir = dirs[rand.nextInt(dirs.length)];
		world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, dir));
	}

	private void decayWaste() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null && inv[i].itemID == ReactorItems.WASTE.getShiftedItemID() && inv[i].stackTagCompound != null) {
				Isotopes atom = Isotopes.getIsotope(inv[i].stackTagCompound.getInteger("iso"));
				if (ReikaRandomHelper.doWithChance(0.125*ReikaNuclearHelper.getDecayChanceFromHalflife(Math.log(atom.getMCHalfLife())))) {
					//ReikaJavaLibrary.pConsole("Radiating from "+atom);
					this.leakRadiation(worldObj, xCoord, yCoord, zCoord);
				}
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
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
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

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void addTemperature(int T) {
		temperature += T;
	}

	public int getMaxTemperature() {
		return 600;
	}

	public void onMeltdown(World world, int x, int y, int z) {
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 9, true);
		RadiationEffects.contaminateArea(world, x, y, z, 9);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public void updateTemperature(World world, int x, int y, int z, int meta) {
		this.distributeHeat(world, x, y, z);
	}

	@Override
	public int getThermalDamage() {
		return temperature/100;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {
		this.onMeltdown(world, x, y, z);
	}

}
