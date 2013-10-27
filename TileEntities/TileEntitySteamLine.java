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
import Reika.ReactorCraft.Registry.WorkingFluid;

public class TileEntitySteamLine extends TileEntityReactorBase {

	//private double storedEnergy;
	private int steam;

	private WorkingFluid fluid = WorkingFluid.EMPTY;

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
		//steam = 0;

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}

		//ReikaJavaLibrary.pConsole(steam+":"+fluid, Side.SERVER);
	}

	private void drawFromBoiler(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		if (r == ReactorTiles.BOILER) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(x, y-1, z);
			if (this.canTakeInWorkingFluid(te.getWorkingFluid())) {
				fluid = te.getWorkingFluid();
				int s = te.removeSteam();
				//ReikaJavaLibrary.pConsole(steam+"+"+s+"="+(steam+s));
				steam += s;
			}
		}
	}

	private boolean canTakeInWorkingFluid(WorkingFluid f) {
		if (f == WorkingFluid.EMPTY)
			return false;
		if (fluid == WorkingFluid.EMPTY)
			return true;
		if (fluid == f)
			return true;
		return false;
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
				if (this.canTakeInWorkingFluid(te.fluid))
					this.readPipe(te);
			}
		}
	}

	private void readPipe(TileEntitySteamLine te) {
		int dS = te.steam-steam;
		if (dS > 0) {
			//ReikaJavaLibrary.pConsole(steam+":"+te.steam);
			steam += dS/4+1;
			te.steam -= dS/4+1;
			fluid = te.fluid;
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

	protected void removeSteam(int amt) {
		steam -= amt;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");

		fluid = WorkingFluid.getFromNBT(NBT);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);

		fluid.saveToNBT(NBT);
	}

	public WorkingFluid getWorkingFluid() {
		return fluid;
	}

}
