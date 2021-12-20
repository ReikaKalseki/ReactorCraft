/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks.Multi;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.BlockMatchFailCallback;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.BlockReCMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityReactorFlywheel;

public class BlockFlywheelMulti extends BlockReCMultiBlock {

	public BlockFlywheelMulti(Material par2Material) {
		super(par2Material);
	}

	@Override
	public int getNumberTextures() {
		return 4;
	}

	@Override
	public Boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir, BlockMatchFailCallback call) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		if (blocks.getSize() != 20)
			return false;
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.FLYWHEEL) {
			if (call != null)
				call.onBlockFailure(world, midX, midY, midZ, new BlockKey(ReactorTiles.FLYWHEEL));
			return false;
		}
		TileEntityReactorFlywheel te = (TileEntityReactorFlywheel)world.getTileEntity(midX, midY, midZ);
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(te.getFacing());

		for (int i = 1; i <= 2; i++) {
			int dx = midX+left.offsetX*i;
			int dz = midZ+left.offsetZ*i;
			int m = i == 1 ? 0 : 2;
			if (world.getBlock(dx, midY, dz) != this || world.getBlockMetadata(dx, midY, dz) != m) {
				if (call != null)
					call.onBlockFailure(world, dx, midY, dz, new BlockKey(this, m));
				return false;
			}
			dx = midX-left.offsetX*i;
			dz = midZ-left.offsetZ*i;
			if (world.getBlock(dx, midY, dz) != this || world.getBlockMetadata(dx, midY, dz) != m) {
				if (call != null)
					call.onBlockFailure(world, dx, midY, dz, new BlockKey(this, m));
				return false;
			}
			if (world.getBlock(midX, midY-i, midZ) != this || world.getBlockMetadata(midX, midY-i, midZ) != m) {
				if (call != null)
					call.onBlockFailure(world, midX, midY-i, midZ, new BlockKey(this, m));
				return false;
			}
			if (world.getBlock(midX, midY+i, midZ) != this || world.getBlockMetadata(midX, midY+i, midZ) != m) {
				if (call != null)
					call.onBlockFailure(world, midX, midY-i, midZ, new BlockKey(this, m));
				return false;
			}
		}

		int dx = midX+left.offsetX;
		int dz = midZ+left.offsetZ;
		if (world.getBlock(dx, midY+1, dz) != this || world.getBlockMetadata(dx, midY+1, dz) != 1) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+1, dz, new BlockKey(this, 1));
			return false;
		}
		if (world.getBlock(dx, midY-1, dz) != this || world.getBlockMetadata(dx, midY-1, dz) != 1) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-1, dz, new BlockKey(this, 1));
			return false;
		}
		dx = midX-left.offsetX;
		dz = midZ-left.offsetZ;
		if (world.getBlock(dx, midY+1, dz) != this || world.getBlockMetadata(dx, midY+1, dz) != 1) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+1, dz, new BlockKey(this, 1));
			return false;
		}
		if (world.getBlock(dx, midY-1, dz) != this || world.getBlockMetadata(dx, midY-1, dz) != 1) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-1, dz, new BlockKey(this, 1));
			return false;
		}

		dx = midX+left.offsetX;
		dz = midZ+left.offsetZ;
		if (world.getBlock(dx, midY+2, dz) != this || world.getBlockMetadata(dx, midY+2, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+2, dz, new BlockKey(this, 2));
			return false;
		}
		if (world.getBlock(dx, midY-2, dz) != this || world.getBlockMetadata(dx, midY-2, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-2, dz, new BlockKey(this, 2));
			return false;
		}
		dx = midX-left.offsetX;
		dz = midZ-left.offsetZ;
		if (world.getBlock(dx, midY+2, dz) != this || world.getBlockMetadata(dx, midY+2, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+2, dz, new BlockKey(this, 2));
			return false;
		}
		if (world.getBlock(dx, midY-2, dz) != this || world.getBlockMetadata(dx, midY-2, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-2, dz, new BlockKey(this, 2));
			return false;
		}

		dx = midX+left.offsetX*2;
		dz = midZ+left.offsetZ*2;
		if (world.getBlock(dx, midY+1, dz) != this || world.getBlockMetadata(dx, midY+1, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+1, dz, new BlockKey(this, 2));
			return false;
		}
		if (world.getBlock(dx, midY-1, dz) != this || world.getBlockMetadata(dx, midY-1, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-1, dz, new BlockKey(this, 2));
			return false;
		}
		dx = midX-left.offsetX*2;
		dz = midZ-left.offsetZ*2;
		if (world.getBlock(dx, midY+1, dz) != this || world.getBlockMetadata(dx, midY+1, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY+1, dz, new BlockKey(this, 2));
			return false;
		}
		if (world.getBlock(dx, midY-1, dz) != this || world.getBlockMetadata(dx, midY-1, dz) != 2) {
			if (call != null)
				call.onBlockFailure(world, dx, midY-1, dz, new BlockKey(this, 2));
			return false;
		}

		return true;
	}

	@Override
	public void breakMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x+1, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x-1, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y+1, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y-1, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z+1, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z-1, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int meta = c.getBlockMetadata(world);
			if (meta >= 8) {
				world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, meta-8, 3);
			}
		}
		int midX = blocks.getMidX();
		int midY = blocks.getMidY();
		int midZ = blocks.getMidZ();
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.FLYWHEEL) {
			TileEntityReactorFlywheel te = (TileEntityReactorFlywheel)world.getTileEntity(midX, midY, midZ);
			te.setHasMultiBlock(false);
		}
	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z, Boolean complete) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int meta = c.getBlockMetadata(world);
			if (meta < 8) {
				world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, meta+8, 3);
			}
		}
		int midX = blocks.getMidX();
		int midY = blocks.getMidY();
		int midZ = blocks.getMidZ();
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.FLYWHEEL) {
			TileEntityReactorFlywheel te = (TileEntityReactorFlywheel)world.getTileEntity(midX, midY, midZ);
			te.setHasMultiBlock(true);
		}
	}

	@Override
	public int getNumberVariants() {
		return 3;
	}

	@Override
	protected String getIconBaseName() {
		return "flywheel";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		return meta >= 8 ? 3 : meta;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		return meta&7;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return true;
	}

	@Override
	protected TileEntity getTileEntityForPosition(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-6, y-6, z-6, x+6, y+6, z+6, 1);
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.FLYWHEEL)
			return null;
		return world.getTileEntity(midX, midY, midZ);
	}

}
