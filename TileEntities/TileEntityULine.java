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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.FuelNetwork;
import Reika.ReactorCraft.Base.TileEntityReactorBase;

public class TileEntityULine extends TileEntityReactorBase implements Feedable {

	private FuelNetwork network;

	@Override
	public int getIndex() {
		return 0;//return ReactorTiles.ITEMLINE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean feed() {
		return false;
	}

	@Override
	public boolean feedIn(ItemStack is) {
		return false;
	}

	@Override
	public ItemStack feedOut() {
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		TileEntity te = world.getBlockTileEntity(x, y+1, z);
		if (te instanceof IInventory) {
			IInventory ii = (IInventory)te;
			boolean flag = false;
			for (int i = 0; i < ii.getSizeInventory(); i++) {
				if (ii.getStackInSlot(i) != null) {
					network.addItem(ii.getStackInSlot(i));
					ii.setInventorySlotContents(i, null);
					flag = true;
				}
			}
			if (flag) {
				network.distribute();
				world.setBlock(x, y+1, z, 49);
			}
		}
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
	public void getOrCreateNetwork(World world, int x, int y, int z) {
		FuelNetwork ntw = new FuelNetwork();
		ntw.addPipeTile(this);
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

	@Override
	public FuelNetwork getNetwork() {
		return network;
	}

	@Override
	public void setNetwork(FuelNetwork fuel) {
		network = fuel;
	}

	public void deleteFromNetwork() {
		network.deletePipeTile(this);
	}

	public boolean isConnectedOnSide(ForgeDirection side) {
		TileEntity te = worldObj.getBlockTileEntity(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
		return te instanceof Feedable;
	}

}
