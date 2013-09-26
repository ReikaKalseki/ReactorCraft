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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWaterLine extends TileEntityReactorBase {

	private double storedEnergy;

	@Override
	public int getIndex() {
		return ReactorTiles.WATERLINE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getEnergyFromCell(world, x, y, z);
		this.getPipeEnergies(world, x, y, z);
		storedEnergy += 40000000;
	}

	private void getEnergyFromCell(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y+1, z);
		int meta = world.getBlockMetadata(x, y+1, z);
		if (id == ReactorTiles.COOLANT.getBlockID() && meta == ReactorTiles.COOLANT.getBlockMetadata()) {
			TileEntityWaterCell te = (TileEntityWaterCell)world.getBlockTileEntity(x, y+1, z);
			storedEnergy += te.removeEnergy();
		}
	}

	private void getPipeEnergies(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id == this.getTileEntityBlockID() && meta == ReactorTiles.WATERLINE.getBlockMetadata()) {
				TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(dx, dy, dz);
				this.readPipe(te);
			}
		}
	}

	private void readPipe(TileEntityWaterLine te) {
		double E = te.storedEnergy;
		double dE = E-storedEnergy;
		if (dE > 0) {
			storedEnergy += dE/4D;
			te.storedEnergy -= dE/4D;
		}
	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.WATERLINE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.COOLANT.getBlockID() && meta == ReactorTiles.COOLANT.getBlockMetadata())
			return true;
		if (id == ReactorTiles.TURBINECORE.getBlockID() && meta == ReactorTiles.TURBINECORE.getBlockMetadata())
			return true;
		return false;
	}

	public double getEnergy() {
		return storedEnergy;
	}

	protected double removeEnergy() {
		double E = storedEnergy;
		storedEnergy = 0;
		return E;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		storedEnergy = NBT.getDouble("energy");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setDouble("energy", storedEnergy);
	}

}
