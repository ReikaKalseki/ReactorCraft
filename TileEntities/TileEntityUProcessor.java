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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.ParallelTicker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityUProcessor extends TileEntityInventoriedReactorBase implements ITankContainer {

	public static final int ACID_PER_UNIT = 125;
	public static final int ACID_PER_FLUORITE = 250;

	private LiquidTank output = new LiquidTank(3000);
	private LiquidTank acid = new LiquidTank(3000);
	private LiquidTank water = new LiquidTank(3000);

	private ItemStack[] inv = new ItemStack[3];

	public int HF_timer;
	public int UF6_timer;

	public static final int ACID_TIME = 80;
	public static final int UF6_TIME = 400;

	private ParallelTicker timer = new ParallelTicker().addTicker("acid", ACID_TIME).addTicker("uf6", UF6_TIME);

	@Override
	public int getIndex() {
		return ReactorTiles.PROCESSOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getWaterBuckets();
		if (this.canMakeAcid()) {
			timer.updateTicker("acid");
			if (timer.checkCap("acid"))
				this.makeAcid();
		}
		else {
			timer.resetTicker("acid");
		}

		if (this.canMakeUF6()) {
			timer.updateTicker("uf6");
			if (timer.checkCap("uf6"))
				this.makeUF6();
		}
		else {
			timer.resetTicker("uf6");
		}

		if (!world.isRemote) {
			HF_timer = timer.getTickOf("acid");
			UF6_timer = timer.getTickOf("uf6");
		}
	}

	public boolean canMakeUF6() {
		return (this.hasUranium()) && this.getHF() > ACID_PER_UNIT && this.canAcceptMoreUF6(LiquidContainerRegistry.BUCKET_VOLUME);
	}

	private boolean hasUranium() {
		if (inv[2] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[2], ReactorOres.PITCHBLENDE.getProduct()))
			return true;
		ArrayList<ItemStack> ingots = OreDictionary.getOres("ingotUranium");
		return ReikaItemHelper.listContainsItemStack(ingots, inv[2]);
	}

	public boolean canMakeAcid() {
		return this.getWater() > 0 && inv[0] != null && inv[0].itemID == ReactorItems.FLUORITE.getShiftedItemID() && this.canAcceptMoreHF(ACID_PER_FLUORITE);
	}

	private void makeAcid() {
		ReikaInventoryHelper.decrStack(0, inv);
		this.addHF(ACID_PER_FLUORITE);
		water.drain(ACID_PER_FLUORITE, true);
	}

	private void makeUF6() {
		ReikaInventoryHelper.decrStack(2, inv);
		output.fill(LiquidDictionary.getLiquid("Uranium Hexafluoride", LiquidContainerRegistry.BUCKET_VOLUME), true);
		acid.drain(ACID_PER_UNIT, true);
	}

	public int getHFTimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("acid"));
	}

	public int getUF6TimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("uf6"));
	}

	public int getWaterScaled(int p) {
		return p*this.getWater()/water.getCapacity();
	}

	public int getHFScaled(int p) {
		return p*this.getHF()/acid.getCapacity();
	}

	public int getUF6Scaled(int p) {
		return p*this.getUF6()/output.getCapacity();
	}

	public int getWater() {
		return water.getLiquid() != null ? water.getLiquid().amount : 0;
	}

	public int getHF() {
		return acid.getLiquid() != null ? acid.getLiquid().amount : 0;
	}

	public int getUF6() {
		return output.getLiquid() != null ? output.getLiquid().amount : 0;
	}

	private void getWaterBuckets() {
		if (inv[1] != null && inv[1].itemID == Item.bucketWater.itemID && this.canAcceptMoreWater(LiquidContainerRegistry.BUCKET_VOLUME)) {
			water.fill(LiquidDictionary.getLiquid("Water", LiquidContainerRegistry.BUCKET_VOLUME), true);
			inv[1] = new ItemStack(Item.bucketEmpty);
		}
	}

	public boolean canAcceptMoreWater(int amt) {
		return water.getLiquid() == null || water.getLiquid().amount+amt <= water.getCapacity();
	}

	public boolean canAcceptMoreHF(int amt) {
		return acid.getLiquid() == null || acid.getLiquid().amount+amt <= acid.getCapacity();
	}

	public boolean canAcceptMoreUF6(int amt) {
		return output.getLiquid() == null || output.getLiquid().amount+amt <= output.getCapacity();
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
	public boolean isStackValidForSlot(int i, ItemStack is) {
		if (ReikaItemHelper.matchStacks(is, ReactorOres.PITCHBLENDE.getProduct()))
			return i == 2;
		if (ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotUranium"), is))
			return i == 2;
		if (is.itemID == ReactorItems.FLUORITE.getShiftedItemID())
			return i == 0;
		if (is.itemID == Item.bucketWater.itemID)
			return i == 1;
		return false;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return water.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[]{water, output};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		if (type.isLiquidEqual(LiquidDictionary.getCanonicalLiquid("Water"))) {
			return water;
		}
		return null;
	}

	public void addHF(int amt) {
		acid.fill(LiquidDictionary.getLiquid("Hydrofluoric Acid", amt), true);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		UF6_timer = NBT.getInteger("uf6");
		HF_timer = NBT.getInteger("hf");

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

		if (NBT.hasKey("internalwater")) {
			water.setLiquid(new LiquidStack(NBT.getInteger("waterId"), NBT.getInteger("internalwater")));
		}
		else if (NBT.hasKey("water")) {
			water.setLiquid(LiquidStack.loadLiquidStackFromNBT(NBT.getCompoundTag("water")));
		}
		if (NBT.hasKey("internalacid")) {
			acid.setLiquid(new LiquidStack(NBT.getInteger("acidId"), NBT.getInteger("internalacid")));
		}
		else if (NBT.hasKey("acid")) {
			acid.setLiquid(LiquidStack.loadLiquidStackFromNBT(NBT.getCompoundTag("acid")));
		}
		if (NBT.hasKey("internaloutput")) {
			output.setLiquid(new LiquidStack(NBT.getInteger("outputId"), NBT.getInteger("internaloutput")));
		}
		else if (NBT.hasKey("output")) {
			output.setLiquid(LiquidStack.loadLiquidStackFromNBT(NBT.getCompoundTag("output")));
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("uf6", UF6_timer);
		NBT.setInteger("hf", HF_timer);

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

		if (water.getLiquid() != null) {
			NBT.setTag("water", water.getLiquid().writeToNBT(new NBTTagCompound()));
		}
		if (acid.getLiquid() != null) {
			NBT.setTag("acid", acid.getLiquid().writeToNBT(new NBTTagCompound()));
		}
		if (output.getLiquid() != null) {
			NBT.setTag("output", output.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	public int getLiquid(LiquidStack liquid) {
		if (liquid.isLiquidEqual(LiquidDictionary.getCanonicalLiquid("Water")))
			return this.getWater();
		if (liquid.isLiquidEqual(ReactorCraft.HF))
			return this.getHF();
		if (liquid.isLiquidEqual(ReactorCraft.UF6))
			return this.getUF6();
		return 0;
	}
}
