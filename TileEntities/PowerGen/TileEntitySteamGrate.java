/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.BlockSteam;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.RotaryCraft.API.Screwdriverable;

public class TileEntitySteamGrate extends TileEntityReactorBase implements Screwdriverable {

	private int steam;
	private boolean requireRedstone;

	private WorkingFluid fluid = WorkingFluid.EMPTY;

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getSteam(world, x, y, z);

		if (!world.isRemote && this.canMakeSteam(world, x, y, z)) {
			steam--;
			world.setBlock(x, y+1, z, ReactorBlocks.STEAM.getBlockInstance(), this.getSteamMetadata(), 3);
		}

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}

		if (DragonAPICore.debugtest)
			steam = 3;
		//fluid = WorkingFluid.AMMONIA;
		//ReikaJavaLibrary.pConsole(steam, Side.SERVER);
	}

	private boolean canMakeSteam(World world, int x, int y, int z) {
		if (steam <= 0)
			return false;
		if (world.isBlockIndirectlyGettingPowered(x, y, z) != requireRedstone)
			return false;
		if (InterfaceCache.IGALACTICWORLD.instanceOf(world.provider)) {
			IGalacticraftWorldProvider ig = (IGalacticraftWorldProvider)world.provider;
			if (ig.getSoundVolReductionAmount() > 1)
				return false;
		}
		return ((BlockSteam)ReactorBlocks.STEAM.getBlockInstance()).canMoveInto(world, x, y+1, z);
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
				TileEntitySteamLine te = (TileEntitySteamLine)world.getTileEntity(dx, dy, dz);
				if (this.canTakeInWorkingFluid(te.getWorkingFluid())) {
					fluid = te.getWorkingFluid();
					int ds = te.getSteam()-steam;
					if (ds > 0) {
						int rm = ds/4+1;
						steam += rm;
						te.removeSteam(rm);
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
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		steam = NBT.getInteger("energy");

		fluid = WorkingFluid.getFromNBT(NBT);

		requireRedstone = NBT.getBoolean("red");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("energy", steam);

		fluid.saveToNBT(NBT);

		NBT.setBoolean("red", requireRedstone);
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return requireRedstone = !requireRedstone;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

}
