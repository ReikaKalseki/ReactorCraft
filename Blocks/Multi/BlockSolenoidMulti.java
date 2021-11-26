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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.BlockMatchFailCallback;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.ReactorCraft.Auxiliary.NeutronBlock;
import Reika.ReactorCraft.Base.BlockReCMultiBlock;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.RotaryCraft.API.Interfaces.Transducerable;

public class BlockSolenoidMulti extends BlockReCMultiBlock implements Transducerable, NeutronBlock {

	public BlockSolenoidMulti(Material par2Material) {
		super(par2Material);
	}

	@Override
	public int getNumberTextures() {
		return 12;
	}

	@Override
	public Boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir, BlockMatchFailCallback call) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.SOLENOID)
			return false;

		if (!this.checkUpper(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkLower(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkMiddle(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkCorners(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkSpokes(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkCore(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;

		return true;
	}

	private boolean checkCore(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = -1; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || j != 0 || k != 0) {
						Block id = world.getBlock(midX+i, midY+j, midZ+k);
						int meta = world.getBlockMetadata(midX+i, midY+j, midZ+k);
						if (id != this)
							return false;
						if (meta != 5)
							return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkSpokes(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = 2; i <= 7; i++) {
			Block id = world.getBlock(midX+i, midY, midZ);
			int meta = world.getBlockMetadata(midX+i, midY, midZ);
			if (id != this)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlock(midX-i, midY, midZ);
			meta = world.getBlockMetadata(midX-i, midY, midZ);
			if (id != this)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlock(midX, midY, midZ+i);
			meta = world.getBlockMetadata(midX, midY, midZ+i);
			if (id != this)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlock(midX, midY, midZ-i);
			meta = world.getBlockMetadata(midX, midY, midZ-i);
			if (id != this)
				return false;
			if (meta != 4)
				return false;

			if (i < 6) {
				id = world.getBlock(midX+i, midY, midZ+i);
				meta = world.getBlockMetadata(midX+i, midY, midZ+i);
				//ReikaJavaLibrary.pConsole(i+" > "+id+":"+meta);
				if (id != this)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlock(midX-i, midY, midZ+i);
				meta = world.getBlockMetadata(midX-i, midY, midZ+i);
				if (id != this)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlock(midX+i, midY, midZ-i);
				meta = world.getBlockMetadata(midX+i, midY, midZ-i);
				if (id != this)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlock(midX-i, midY, midZ-i);
				meta = world.getBlockMetadata(midX-i, midY, midZ-i);
				if (id != this)
					return false;
				if (meta != 4)
					return false;
			}
		}
		return true;
	}

	private boolean checkCorners(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = 6; i <= 6; i++) {
			Block id = world.getBlock(midX-i, midY+1, midZ-i);
			int meta = world.getBlockMetadata(midX-i, midY+1, midZ-i);
			if (id != this)
				return false;
			if (meta != 1)
				return false;

			id = world.getBlock(midX-i, midY-1, midZ-i);
			meta = world.getBlockMetadata(midX-i, midY-1, midZ-i);
			if (id != this)
				return false;
			if (meta != 1)
				return false;
		}
		return true;
	}

	private boolean checkMiddle(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = midX-d;
			int dy = midY;
			int dz = midZ+i;
			int m = Math.abs(i) >= 3 ? 3 : 2;

			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+i;
			dz = midZ+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dz = midZ-d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;
		}
		return true;
	}

	private boolean checkLower(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = midX-d;
			int dy = midY-1;
			int dz = midZ+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+i;
			dz = midZ+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dz = midZ-d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;
		}
		return true;
	}

	private boolean checkUpper(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = midX-d;
			int dy = midY+1;
			int dz = midZ+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dx = midX+i;
			dz = midZ+d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;

			dz = midZ-d;
			id = world.getBlock(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != this)
				return false;
			if (m != meta)
				return false;
		}
		return true;
	}

	@Override
	public void breakMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x+1, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x-1, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y+1, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y-1, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z+1, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z-1, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
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
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.SOLENOID) {
			TileEntitySolenoidMagnet te = (TileEntitySolenoidMagnet)world.getTileEntity(midX, midY, midZ);
			te.setHasMultiBlock(false);
		}
	}

	@Override
	public void onCreateFullMultiBlock(World world, int x, int y, int z, Boolean complete) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
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
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.SOLENOID) {
			TileEntitySolenoidMagnet te = (TileEntitySolenoidMagnet)world.getTileEntity(midX, midY, midZ);
			te.setHasMultiBlock(true);
		}
	}

	@Override
	public int getNumberVariants() {
		return 6;
	}

	@Override
	protected String getIconBaseName() {
		return "solenoid";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		if (meta >= 8)
			return 10;
		if (meta == 4) {
			boolean f = world.getBlock(x+1, y, z) == this || world.getBlock(x-1, y, z) == this;
			boolean f2 = world.getBlock(x, y, z+1) == this || world.getBlock(x, y, z-1) == this;
			if (side > 1)
				return 8;
			if (f)
				return 5;
			else if (f2)
				return 4;
			else
				return 9;
		}
		if (meta == 5) {
			return side > 1 ? 6 : 3;
		}
		if (meta == 3 || meta == 2) {
			if (side < 2)
				return this.getTextureIndex(world, x, y, z, side, 0);
			return meta == 2 ? 11 : 2;
		}
		if (meta == 0 || meta == 1) {
			boolean f = world.getBlock(x+1, y, z) == this || world.getBlock(x-1, y, z) == this;
			boolean f2 = world.getBlock(x, y, z+1) == this || world.getBlock(x, y, z-1) == this;
			if (side > 1)
				return (f || f2) ? 0 : 3;
			if (f)
				return 0;
			else if (f2)
				return 1;
			else
				return 3;
		}
		return meta;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		meta = meta&7;
		if (side < 2) {
			if (meta < 4 || meta == 5)
				return 3;
			else
				return 9;
		}
		if (meta == 2)
			return 11;
		if (meta == 3)
			return 2;
		if (meta == 5)
			return 6;
		if (meta == 4)
			return 8;
		return meta&7;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return true;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

	@Override
	protected TileEntity getTileEntityForPosition(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, this, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.SOLENOID)
			return null;
		return world.getTileEntity(midX, midY, midZ);
	}

}
