/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.BlockSteam;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;

public class TileEntitySteamGrate extends TileEntityReactorBase {

	private int steam;

	private WorkingFluid fluid = WorkingFluid.EMPTY;

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getSteam(world, x, y, z);

		int dx = x;
		int dy = y+1;
		int dz = z;

		if (!world.isRemote && steam > 0 && ((BlockSteam)ReactorBlocks.STEAM.getBlockVariable()).canMoveInto(world, dx, dy, dz)) {
			steam--;
			world.setBlock(dx, dy, dz, ReactorBlocks.STEAM.getBlockID(), this.getSteamMetadata(), 3);
			//ReikaJavaLibrary.pConsole(fluid+":"+this.getSteamMetadata(), Side.SERVER);
		}

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}
		//steam = 3;
		//ReikaJavaLibrary.pConsole(steam, Side.SERVER);
	}

	private ForgeDirection getFacing(int meta) {
		switch(meta) {
		case 0:
			return ForgeDirection.EAST;
		case 1:
			return ForgeDirection.WEST;
		case 2:
			return ForgeDirection.SOUTH;
		case 3:
			return ForgeDirection.NORTH;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}

	private int getSteamMetadata() {
		if (fluid == WorkingFluid.AMMONIA)
			return 7;
		return 3;
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

	private void getSteam(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles rt = ReactorTiles.getTE(world, dx, dy, dz);
			if (rt == ReactorTiles.STEAMLINE) {
				TileEntitySteamLine te = (TileEntitySteamLine)world.getBlockTileEntity(dx, dy, dz);
				if (this.canTakeInWorkingFluid(te.getWorkingFluid())) {
					fluid = te.getWorkingFluid();
					int ds = te.getSteam()-steam;
					if (ds > 0) {
						steam += ds/4+1;
						te.removeSteam(ds/2+1);
					}
				}
			}
		}
	}

	@Override
	public int getIndex() {
		return ReactorTiles.GRATE.ordinal();
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

}
