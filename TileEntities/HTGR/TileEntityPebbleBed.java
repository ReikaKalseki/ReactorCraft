/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.HTGR;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityReactorBoiler;

public class TileEntityPebbleBed extends TileEntityInventoriedReactorBase implements Temperatured, Feedable {

	protected StepTimer tempTimer = new StepTimer(20);

	public static final int MINTEMP = 800;
	public static final int OVERTEMP = 1600;
	public static final int FAILTEMP = 4400;

	@Override
	public int getSizeInventory() {
		return 47;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && ReikaRandomHelper.doWithChance(7))
			this.runDecayCycle();
		//ReikaInventoryHelper.clearInventory(this);
		//ReikaInventoryHelper.addToIInv(ReactorItems.PELLET.getStackOf(), this);
		//ReikaJavaLibrary.pConsole(temperature, Side.SERVER);
		this.feed();

		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, x, y, z);
		}
	}

	private void runDecayCycle() {
		if (!worldObj.isRemote) {
			int slot = -1;
			for (int i = inv.length-1; i >= 0; i--) {
				ItemStack is = inv[i];
				if (is != null && is.itemID == ReactorItems.PELLET.getShiftedItemID()) {
					slot = i;
					i = -1;
				}
			}
			if (slot != -1) {
				if (ReikaRandomHelper.doWithChance(3)) {
					ItemStack is = inv[slot];
					inv[slot] = this.getFissionProduct(is);
				}
				temperature += 20;
			}
		}
	}

	private ItemStack getFissionProduct(ItemStack is) {
		if (is.getItemDamage() == ReactorItems.PELLET.getNumberMetadatas()-1)
			return ReactorItems.OLDPELLET.getStackOf();
		return ReactorItems.PELLET.getStackOfMetadata(is.getItemDamage()+1);
	}

	@Override
	protected void updateTemperature(World world, int x, int y, int z) {
		super.updateTemperature(world, x, y, z);
		int Tamb = ReikaBiomeHelper.getBiomeTemp(world, x, z);
		int dT = temperature-Tamb;

		if (dT != 0 && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, 0) != null)
			temperature -= (1+dT/32);

		if (dT > 0) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);

				if (r == this.getMachine()) {
					TileEntityPebbleBed te = (TileEntityPebbleBed)world.getBlockTileEntity(dx, dy, dz);
					int dTemp = temperature-te.temperature;
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.temperature += dTemp/16;
					}
				}
				else if (r == ReactorTiles.BOILER) {
					TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(dx, dy, dz);
					dT = te.getTemperature()-temperature;
					int t = Math.max(te.getTemperature(), temperature);
					int tr = te.getTemperature() >= 100 ? Math.max(t, 20) : 20;
					//ReikaJavaLibrary.pConsole(tr, Side.SERVER);
					int maxr = Math.min(tr, temperature-Tamb);
					if (maxr > 0) {
						temperature -= maxr;
					}
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return is.itemID == ReactorItems.PELLET.getShiftedItemID() || is.itemID == ReactorItems.OLDPELLET.getShiftedItemID();
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
		return is.itemID == ReactorItems.OLDPELLET.getShiftedItemID();
	}

	@Override
	public int getIndex() {
		return ReactorTiles.PEBBLEBED.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isFissile() {
		return ReikaInventoryHelper.checkForItem(ReactorItems.PELLET.getShiftedItemID(), inv);
	}

	public boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		int id = world.getBlockId(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = world.getBlockTileEntity(x, y-1, z);
		if (tile instanceof TileEntityPebbleBed) {
			if (((Feedable)tile).feedIn(inv[inv.length-1])) {
				for (int i = inv.length-1; i > 0; i--)
					inv[i] = inv[i-1];

				id = world.getBlockId(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = world.getBlockTileEntity(x, y+1, z);
				if (tile instanceof TileEntityPebbleBed) {
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
		for (int i = 0; i < inv.length; i++) {
			for (int k = inv.length-1; k > 0; k--) {
				if (inv[k] == null) {
					inv[k] = inv[k-1];
					inv[k-1] = null;
				}
			}
		}
	}

	@Override
	public boolean feedIn(ItemStack is) {
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

	@Override
	public ItemStack feedOut() {
		if (inv[inv.length-1] == null)
			return null;
		else {
			ItemStack is = inv[inv.length-1].copy();
			inv[inv.length-1] = null;
			return is;
		}
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
		return FAILTEMP;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return false;
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

}
