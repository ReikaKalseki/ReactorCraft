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

public class TileEntitySteamLine extends TileEntityReactorBase {

	//private double storedEnergy;
	private int steam;

	@Override
	public int getIndex() {
		return ReactorTiles.STEAMLINE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.drawFromBoiler(world, x, y, z);
		this.getPipeSteam(world, x, y, z);

		//ReikaJavaLibrary.pConsole(steam);
	}

	private void drawFromBoiler(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		if (r == ReactorTiles.BOILER) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(x, y-1, z);
			int s = te.removeSteam();
			steam += s;
		}
	}

	private void getPipeSteam(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id == this.getTileEntityBlockID() && meta == ReactorTiles.STEAMLINE.getBlockMetadata()) {
				TileEntitySteamLine te = (TileEntitySteamLine)world.getBlockTileEntity(dx, dy, dz);
				this.readPipe(te);
			}
		}
	}

	private void readPipe(TileEntitySteamLine te) {
		int dS = te.steam-steam;
		if (dS > 0) {
			steam++;
			te.steam--;
		}
	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.STEAMLINE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.BOILER.getBlockID() && meta == ReactorTiles.BOILER.getBlockMetadata() && dir == ForgeDirection.DOWN)
			return true;
		if (id == ReactorTiles.GRATE.getBlockID() && meta == ReactorTiles.GRATE.getBlockMetadata())
			return true;
		return false;
	}

	public int getSteam() {
		return steam;
	}

	protected int removeSteam() {
		int E = steam;
		steam = 0;
		return E;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);
	}

}
