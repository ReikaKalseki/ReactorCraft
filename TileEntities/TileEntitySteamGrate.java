package Reika.ReactorCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.BlockSteam;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntitySteamGrate extends TileEntityReactorBase {

	private int steam;
	private ForgeDirection facingDir = ForgeDirection.UNKNOWN;

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getSteam(world, x, y, z);
		steam++;
		facingDir = ForgeDirection.DOWN;//this.getFacing(meta);

		int dx = x-facingDir.offsetX;
		int dy = y-facingDir.offsetY;
		int dz = z-facingDir.offsetZ;

		if (steam > 0 && ((BlockSteam)ReactorBlocks.STEAM.getBlockVariable()).canMoveInto(world, dx, dy, dz)) {
			steam--;
			world.setBlock(dx, dy, dz, ReactorBlocks.STEAM.getBlockID(), 3+this.getSteamMetadataFlags(), 3);
		}
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

	private int getSteamMetadataFlags() {
		switch(facingDir) {
		case EAST:
			return 2;
		case NORTH:
			return 3;
		case SOUTH:
			return 4;
		case WEST:
			return 1;
		default:
			return 0;
		}
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
				steam += te.removeSteam();
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
