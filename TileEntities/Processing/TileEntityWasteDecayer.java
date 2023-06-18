/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.Isotopes.DecayData;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Base.TileEntityWasteUnit;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWasteDecayer extends TileEntityInventoriedReactorBase implements ReactorCoreTE {

	public static final int BASE_TEMP = 150;
	public static final int OPTIMAL_TEMP = 400;

	@Override
	public int getIndex() {
		return ReactorTiles.WASTEDECAYER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			this.feed();
		}

		thermalTicker.update();
		if (thermalTicker.checkCap()) {
			this.updateTemperature(world, x, y, z);
		}
	}

	private boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		Block id = world.getBlock(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (tile instanceof TileEntityWasteDecayer) {
			if (((TileEntityWasteDecayer)tile).feedIn(inv[inv.length-1])) {
				for (int i = inv.length-1; i > 0; i--)
					inv[i] = inv[i-1];

				id = world.getBlock(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = this.getAdjacentTileEntity(ForgeDirection.UP);
				if (tile instanceof TileEntityWasteDecayer) {
					inv[0] = ((TileEntityWasteDecayer) tile).feedOut();
				}
				else
					inv[0] = null;
			}
		}
		this.collapseInventory();
		return false;
	}

	private void collapseInventory() {
		for (int i = 0; i < inv.length; i++) {
			for (int k = inv.length-1; k > 0; k--) {
				if (inv[k] == null && inv[k-1] != null) {
					inv[k] = inv[k-1];
					inv[k-1] = null;
					return;
				}
				else if (ReikaItemHelper.areStacksCombinable(inv[k], inv[k-1], Integer.MAX_VALUE) && inv[k].stackSize < Math.min(inv[k].getMaxStackSize(), this.getInventoryStackLimit())) {
					inv[k].stackSize++;
					ReikaInventoryHelper.decrStack(k-1, inv);
					return;
				}
			}
		}
	}

	private boolean feedIn(ItemStack is) {
		if (is == null)
			return true;
		if (!this.isItemValidForSlot(0, is))
			return false;
		if (inv[0] == null) {
			inv[0] = is.copy();
			return true;
		}
		return false;
	}

	private ItemStack feedOut() {
		if (inv[inv.length-1] == null)
			return null;
		else {
			ItemStack is = inv[inv.length-1].copy();
			inv[inv.length-1] = null;
			return is;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		NeutronType type = e.getType();
		if (!world.isRemote && type.canIrradiateMaterials() && ReikaRandomHelper.doWithChance(50)) {
			if (ReikaRandomHelper.doWithChance(this.getDecayChance()*e.getNeutronSpeed().getWasteConversionMultiplier()))
				this.tryDecay();
			return true;
		}
		return false;
	}

	private double getDecayChance() {
		return temperature < BASE_TEMP ? ReikaMathLibrary.linterpolate(temperature, 20, BASE_TEMP, 0, 5) : ReikaMathLibrary.linterpolate(temperature, BASE_TEMP, OPTIMAL_TEMP, 5, 25);
	}

	private boolean tryDecay() {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null && TileEntityWasteUnit.isLongLivedWaste(is)) {
				//ReikaJavaLibrary.pConsole("Trying to decay "+Isotopes.getIsotope(is.getItemDamage())+" in slot #"+i);
				if (this.tryDecay(i, is)) {
					//ReikaJavaLibrary.pConsole("Success");
					return true;
				}
			}
		}
		return false;
	}

	private boolean tryDecay(int i, ItemStack is) {/*
		int slot = is.stackSize == 1 ? i : ReikaInventoryHelper.findEmptySlot(inv);
		if (slot >= 0) {
			if (is.stackSize > 1)
				is.stackSize--;
			inv[slot] = ReikaItemHelper.getSizedItemStack(Isotopes.getIsotope(is.getItemDamage()).getForcedFissionProduct(), 2);
			return true;
		}
		return false;*/
		DecayData split = Isotopes.getIsotope(is.getItemDamage()).getDecay();
		if (split == null) {
			ReikaInventoryHelper.decrStack(i, inv);
			//ReikaJavaLibrary.pConsole("Decayed into nothing");
			return true;
		}
		int amt = MathHelper.floor_double(split.amount);
		if (ReikaRandomHelper.doWithChance(split.amount-amt))
			amt++;
		if (amt == 0)
			return false;
		ItemStack add = this.getItem(split, amt);
		if (add == null) {
			return true;
		}
		//ReikaJavaLibrary.pConsole("Decayed into "+split.isotope+" x"+add.stackSize);
		if (ReikaInventoryHelper.addToIInv(add, this)) {
			ReikaInventoryHelper.decrStack(i, inv);
			return true;
		}
		return false;
	}

	private ItemStack getItem(DecayData split, int amt) {
		if (split.isotope.getChemicalSymbol().equalsIgnoreCase("pb"))
			return ModOreList.LEAD.existsInGame() ? ModOreList.LEAD.getFirstProduct() : null;
		else if (split.isotope instanceof Isotopes)
			return new ItemStack(ReactorItems.WASTE.getItemInstance(), amt, ((Isotopes)split.isotope).ordinal());
		else
			return null;
	}

	@Override
	public final int getTextureState(ForgeDirection side) {
		if (side.offsetY != 0)
			return 4;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		ReactorTiles src = this.getMachine();
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		ReactorTiles r2 = ReactorTiles.getTE(world, x, y+1, z);
		if (r2 == src && r == src)
			return 2;
		else if (r2 == src)
			return 1;
		else if (r == src)
			return 3;
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 15;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReactorItems.WASTE.matchWith(is);
	}

	@Override
	public boolean canItemEnterFromSide(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public boolean canItemExitToSide(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		return !TileEntityWasteUnit.isLongLivedWaste(is);
	}

	@Override
	public int getInventoryStackLimit() {
		return 8;
	}

}
