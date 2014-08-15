/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGeneratorMulti extends BlockMultiBlock {

	public BlockGeneratorMulti(Material par2Material) {
		super(par2Material);
	}

	@Override
	public int getNumberTextures() {
		return 11;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		if (!this.checkCore(world, x, y, z, dir))
			return false;
		if (!this.checkWindings(world, x, y, z, dir))
			return false;
		if (!this.checkHousing(world, x, y, z, dir))
			return false;
		if (!this.checkEndCap(world, x, y, z, dir))
			return false;
		int l = TileEntityReactorGenerator.getGeneratorLength()-1;
		return ReactorTiles.getTE(world, x+dir.offsetX*l, y, z+dir.offsetZ*l) == ReactorTiles.GENERATOR;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	private boolean checkCore(World world, int x, int y, int z, ForgeDirection dir) {
		int l = TileEntityReactorGenerator.getGeneratorLength()-1;
		for (int i = 0; i < l; i++) {
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i;
			Block b = world.getBlock(dx, y, dz);
			int meta = world.getBlockMetadata(dx, y, dz);
			if (b != this || meta != 0) {
				return false;
			}
		}
		int dx = x+dir.offsetX*l;
		int dz = z+dir.offsetZ*l;
		TileEntity te = world.getTileEntity(dx, y, dz);
		if (te instanceof TileEntityReactorGenerator) {
			return dir == ((TileEntityReactorGenerator)te).getFacing().getOpposite();
		}
		return false;
	}

	private boolean checkWindings(World world, int x, int y, int z, ForgeDirection dir) {
		int l = TileEntityReactorGenerator.getGeneratorLength()-1;
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int i = 0; i < l; i++) {
			int seekmeta = i < 2 ? 3 : 1;
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				Block id = world.getBlock(ddx, dy, ddz);
				int meta = world.getBlockMetadata(ddx, dy, ddz);
				Block id2 = world.getBlock(ddx2, dy, ddz2);
				int meta2 = world.getBlockMetadata(ddx2, dy, ddz2);
				Block id3 = world.getBlock(dx, dy, dz);
				int meta3 = world.getBlockMetadata(dx, dy, dz);
				if (id != this || meta != seekmeta) {
					return false;
				}
				if (id2 != this || meta2 != seekmeta) {
					return false;
				}
				if (k != 0) {
					if (id3 != this || meta3 != seekmeta) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkHousing(World world, int x, int y, int z, ForgeDirection dir) {
		int l = TileEntityReactorGenerator.getGeneratorLength()-1;
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);

		for (int i = 0; i < l; i++) {
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			int seekmeta = 2;
			for (int k = -2; k <= 2; k += 4) {
				int dy = y+k;
				Block id = world.getBlock(ddx, dy, ddz);
				int meta = world.getBlockMetadata(ddx, dy, ddz);
				Block id2 = world.getBlock(ddx2, dy, ddz2);
				int meta2 = world.getBlockMetadata(ddx2, dy, ddz2);
				Block id3 = world.getBlock(dx, dy, dz);
				int meta3 = world.getBlockMetadata(dx, dy, dz);
				if (i == 1 && k == 2)
					seekmeta = 3;
				if (id != this || meta != 2) {
					return false;
				}
				if (id2 != this || meta2 != 2) {
					return false;
				}
				if (id3 != this || meta3 != seekmeta) {
					return false;
				}
			}

			ddx = dx+left.offsetX*2;
			ddx2 = dx-left.offsetX*2;
			ddz = dz+left.offsetZ*2;
			ddz2 = dz-left.offsetZ*2;

			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				Block id = world.getBlock(ddx, dy, ddz);
				int meta = world.getBlockMetadata(ddx, dy, ddz);
				Block id2 = world.getBlock(ddx2, dy, ddz2);
				int meta2 = world.getBlockMetadata(ddx2, dy, ddz2);
				if (id != this || meta != 2) {
					return false;
				}
				if (id2 != this || meta2 != 2) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean checkEndCap(World world, int x, int y, int z, ForgeDirection dir) {
		int l = TileEntityReactorGenerator.getGeneratorLength()-1;
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		int dx = x+dir.offsetX*l;
		int dz = z+dir.offsetZ*l;
		for (int k = -2; k <= 2; k++) {
			int dy = y+k;
			for (int m = -2; m <= 2; m++) {
				if ((Math.abs(k) != 2 || Math.abs(m) != 2) && (k != 0 || m != 0)) {
					int ddx = dx+left.offsetX*m;
					int ddz = dz+left.offsetZ*m;
					Block id = world.getBlock(ddx, dy, ddz);
					int meta = world.getBlockMetadata(ddx, dy, ddz);
					if (id != this || meta != 2) {
						return false;
					}
				}
			}
		}
		for (int i = 0; i < 2; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;

			int ddx = dx+left.offsetX*2;
			int ddz = dz+left.offsetZ*2;
			int ddx2 = dx-left.offsetX*2;
			int ddz2 = dz-left.offsetZ*2;
			Block id = world.getBlock(ddx, y+2, ddz);
			int meta = world.getBlockMetadata(ddx, y+2, ddz);
			Block id2 = world.getBlock(ddx2, y+2, ddz2);
			int meta2 = world.getBlockMetadata(ddx2, y+2, ddz2);
			if (id != this || meta != 2) {
				return false;
			}
			if (id2 != this || meta2 != 2) {
				return false;
			}

			id = world.getBlock(ddx, y-2, ddz);
			meta = world.getBlockMetadata(ddx, y-2, ddz);
			id2 = world.getBlock(ddx2, y-2, ddz2);
			meta2 = world.getBlockMetadata(ddx2, y-2, ddz2);
			if (id != this || meta != 2) {
				return false;
			}
			if (id2 != this || meta2 != 2) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		Block gid = ReactorTiles.GENERATOR.getBlock();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(this, gid), x-12, y-4, z-12, x+12, y+4, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (ReactorTiles.getTE(world, xyz[0], xyz[1], xyz[2]) == ReactorTiles.GENERATOR) {
				TileEntityReactorGenerator te = (TileEntityReactorGenerator)world.getTileEntity(xyz[0], xyz[1], xyz[2]);
				te.hasMultiblock = false;
			}
			else if (meta >= 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta-8, 3);
			}
		}
	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		Block gid = ReactorTiles.GENERATOR.getBlock();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(this, gid), x-12, y-4, z-12, x+12, y+4, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (ReactorTiles.getTE(world, xyz[0], xyz[1], xyz[2]) == ReactorTiles.GENERATOR) {
				TileEntityReactorGenerator te = (TileEntityReactorGenerator)world.getTileEntity(xyz[0], xyz[1], xyz[2]);
				te.hasMultiblock = true;
			}
			else if (meta < 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta+8, 3);
			}
		}
	}

	@Override
	public int getNumberVariants() {
		return 4;
	}

	@Override
	protected String getIconBaseName() {
		return "generator";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		if (meta >= 8)
			return 9;
		if (meta == 3)
			return 5;
		if (meta == 4)
			return 2;
		return meta;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		if (meta == -1)
			return 10;
		if (meta >= 8)
			return 9;
		if (meta == 3)
			return 5;
		return meta;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return meta == 0;
	}

	@Override
	protected TileEntity getTileEntityForPosition(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-12, y-4, z-12, x+12, y+4, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			if (ReactorTiles.getTE(world, xyz[0], xyz[1]+1, xyz[2]) == ReactorTiles.GENERATOR) {
				TileEntityReactorGenerator te = (TileEntityReactorGenerator)world.getTileEntity(xyz[0], xyz[1]+1, xyz[2]);
				return te;
			}
		}
		return null;
	}

}