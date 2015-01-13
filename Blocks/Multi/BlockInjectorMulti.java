/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks.Multi;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;

public class BlockInjectorMulti extends BlockMultiBlock {

	public BlockInjectorMulti(Material par2Material) {
		super(par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 8;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-8, y-5, z-8, x+8, y+5, z+8);
		while (world.getBlock(x, y-1, z) == this && world.getBlockMetadata(x, y-1, z) == 5)
			y--;
		if (!this.checkTop(world, x, y, z, dir, left, blocks))
			return false;
		if (!this.checkBottom(world, x, y, z, dir, left, blocks))
			return false;
		if (!this.checkSides(world, x, y, z, dir, left, blocks))
			return false;
		if (!this.checkCorners(world, x, y, z, dir, left, blocks))
			return false;
		if (!this.checkFiller(world, x, y, z, dir, left, blocks))
			return false;
		if (!this.checkPipes(world, x, y, z, dir, left, blocks))
			return false;
		return true;
	}

	private boolean checkCorners(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 0; i <= 4; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y+3, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y+3, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 4)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y+3, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y+3, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 4)
				return false;
		}
		for (int i = 5; i <= 6; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 4)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 4)
				return false;
		}
		for (int i = 7; i <= 8; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 4)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 4)
				return false;
		}

		for (int i = 0; i <= 8; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y-1, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y-1, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 1)
				return false;
		}

		return true;
	}

	private boolean checkTop(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 0; i <= 4; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y+3, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y+3, z+dir.offsetZ*i);
			if (b != this || meta != 3)
				return false;
		}
		for (int i = 5; i <= 6; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y+2, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y+2, z+dir.offsetZ*i);
			if (b != this || meta != 3)
				return false;
		}
		for (int i = 7; i <= 8; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y+1, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y+1, z+dir.offsetZ*i);
			if (b != this || meta != 3)
				return false;
		}
		return true;
	}

	private boolean checkBottom(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 0; i <= 8; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y-1, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y-1, z+dir.offsetZ*i);
			if (b != this || meta != 0)
				return false;
		}
		return true;
	}

	private boolean checkSides(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 1; i <= 1; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 2)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 2)
				return false;
		}
		for (int i = 3; i <= 7; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 2)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 2)
				return false;
		}
		for (int i = 1; i <= 6; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 2)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 2)
				return false;
		}
		for (int i = 1; i <= 4; i++) {
			Block b = world.getBlock(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ);
			int meta = world.getBlockMetadata(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ);
			if (b != this || meta != 2)
				return false;

			b = world.getBlock(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ);
			meta = world.getBlockMetadata(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ);
			if (b != this || meta != 2)
				return false;
		}
		for (int i = 0; i <= 2; i++) {
			Block b = world.getBlock(x, y+i, z);
			int meta = world.getBlockMetadata(x, y+i, z);
			if (b != this || meta != 5)
				return false;
		}
		return true;
	}

	private boolean checkFiller(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 1; i <= 1; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y, z+dir.offsetZ*i);
			if (b != this || meta != 7)
				return false;
		}
		for (int i = 1; i <= 6; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y+1, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y+1, z+dir.offsetZ*i);
			if (b != this || meta != 7)
				return false;
		}
		for (int i = 1; i <= 4; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y+2, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y+2, z+dir.offsetZ*i);
			if (b != this || meta != 7)
				return false;
		}
		return true;
	}

	private boolean checkPipes(World world, int x, int y, int z, ForgeDirection dir, ForgeDirection left, StructuredBlockArray blocks) {
		for (int i = 3; i <= 8; i++) {
			Block b = world.getBlock(x+dir.offsetX*i, y, z+dir.offsetZ*i);
			int meta = world.getBlockMetadata(x+dir.offsetX*i, y, z+dir.offsetZ*i);
			if (ReactorTiles.getMachineFromIDandMetadata(b, meta) != ReactorTiles.MAGNETPIPE)
				return false;
		}
		if (ReactorTiles.getTE(world, x+dir.offsetX*2, y, z+dir.offsetZ*2) != ReactorTiles.INJECTOR)
			return false;
		return true;
	}

	@Override
	public void onCreateFullMultiBlock(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-8, y-5, z-8, x+8, y+5, z+8);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (meta < 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta+8, 3);
			}
			if (meta == 0) {
				if (ReactorTiles.getTE(world, xyz[0], xyz[1]+1, xyz[2]) == ReactorTiles.INJECTOR) {
					TileEntityFusionInjector te = (TileEntityFusionInjector)world.getTileEntity(xyz[0], xyz[1]+1, xyz[2]);
					te.hasMultiBlock = true;
				}
			}
		}
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-8, y-5, z-8, x+8, y+5, z+8);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (meta >= 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta-8, 3);
			}
			if (meta == 8) {
				if (ReactorTiles.getTE(world, xyz[0], xyz[1]+1, xyz[2]) == ReactorTiles.INJECTOR) {
					TileEntityFusionInjector te = (TileEntityFusionInjector)world.getTileEntity(xyz[0], xyz[1]+1, xyz[2]);
					te.hasMultiBlock = false;
				}
			}
		}
	}

	@Override
	protected String getIconBaseName() {
		return "injector";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		if (meta >= 8) {
			if (meta == 13)
				return 0;
			int index = 10+this.getTextureIndex(world, x, y, z, side, meta-8);
			if (side > 1 && (index == 19 || index == 23 || index == 24 || index == 25))
				index = 9;
			if (meta == 12 && side == 1) {
				boolean s = world.getBlock(x+1, y, z) == this && world.getBlockMetadata(x+1, y, z) == 11;
				boolean s0 = world.getBlock(x-1, y, z) == this && world.getBlockMetadata(x-1, y, z) == 11;
				boolean s1 = world.getBlock(x, y, z+1) == this && world.getBlockMetadata(x, y, z+1) == 11;
				int a = 25;
				int b = 23;
				if (!s && !s0) {
					a = 19;
					b = 24;
				}
				index = (s || s1) ? a : b;
			}
			if (meta == 11 && side == 1)
				index = 9;
			return index;
		}
		switch(meta) {
		case 0:
			if (side == 0)
				return 9;
			return side < 2 ? 0 : 4;
		case 1:
			if (side == 1)
				return 9;
			if (side == 3 || side == 2) {
				if (world.getBlock(x+1, y, z) == this && (world.getBlockMetadata(x+1, y, z)&7) == 0)
					return side == 3 ? 5 : 6;
				if (world.getBlock(x-1, y, z) == this && (world.getBlockMetadata(x-1, y, z)&7) == 0)
					return side == 3 ? 6 : 5;
			}
			if (side == 4 || side == 5) {
				if (world.getBlock(x, y, z+1) == this && (world.getBlockMetadata(x, y, z+1)&7) == 0)
					return side == 4 ? 5 : 6;
				if (world.getBlock(x, y, z-1) == this && (world.getBlockMetadata(x, y, z-1)&7) == 0)
					return side == 4 ? 6 : 5;
			}
			return 9;
		case 2:
			ForgeDirection dir = dirs[side];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == Blocks.air)
				return 9;
			return b == ReactorTiles.MAGNETPIPE.getBlock() ? 0 : 9;
		case 3:
			if (side == 1)
				return 9;
			return side < 2 ? 0 : 3;
		case 4:
			if (side == 1)
				return 9;
			if (side == 3 || side == 2) {
				if (world.getBlock(x+1, y, z) == this && (world.getBlockMetadata(x+1, y, z)&7) == 3)
					return side == 3 ? 8 : 7;
				if (world.getBlock(x-1, y, z) == this && (world.getBlockMetadata(x-1, y, z)&7) == 3)
					return side == 3 ? 7 : 8;
			}
			if (side == 4 || side == 5) {
				if (world.getBlock(x, y, z+1) == this && (world.getBlockMetadata(x, y, z+1)&7) == 3)
					return side == 4 ? 8 : 7;
				if (world.getBlock(x, y, z-1) == this && (world.getBlockMetadata(x, y, z-1)&7) == 3)
					return side == 4 ? 7 : 8;
			}
			return 9;
		case 5:
			return 22;
		case 6:
			if (world.getBlock(x+1, y, z) == this && (world.getBlockMetadata(x+1, y, z)&7) == 5) {
				if (side == 3)
					return 2;
				if (side == 2)
					return 1;
			}
			if (world.getBlock(x-1, y, z) == this && (world.getBlockMetadata(x-1, y, z)&7) == 5) {
				if (side == 3)
					return 1;
				if (side == 2)
					return 2;
			}
			if (world.getBlock(x, y, z+1) == this && (world.getBlockMetadata(x, y, z+1)&7) == 5) {
				if (side == 5)
					return 1;
				if (side == 4)
					return 2;
			}
			if (world.getBlock(x, y, z-1) == this && (world.getBlockMetadata(x, y, z-1)&7) == 5) {
				if (side == 4)
					return 1;
				if (side == 5)
					return 2;
			}

			if (world.getBlock(x+1, y, z) != this && world.getBlock(x-1, y, z) != this) {
				if (ReactorTiles.getTE(world, x+1, y, z) == ReactorTiles.MAGNETPIPE) {
					if (side == 3)
						return 2;
					if (side == 2)
						return 1;
					if (side == 5)
						return 0;
				}
				if (ReactorTiles.getTE(world, x-1, y, z) == ReactorTiles.MAGNETPIPE) {
					if (side == 3)
						return 1;
					if (side == 2)
						return 2;
					if (side == 4)
						return 0;
				}
			}
			if (world.getBlock(x, y, z+1) != this && world.getBlock(x, y, z-1) != this) {
				if (ReactorTiles.getTE(world, x, y, z+1) == ReactorTiles.MAGNETPIPE) {
					if (side == 5)
						return 1;
					if (side == 4)
						return 2;
					if (side == 3)
						return 0;
				}
				if (ReactorTiles.getTE(world, x, y, z-1) == ReactorTiles.MAGNETPIPE) {
					if (side == 4)
						return 1;
					if (side == 5)
						return 2;
					if (side == 2)
						return 0;
				}
			}
			return 9;
		case 7:
			return 0;
		default:
			return 0;
		}
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		meta = meta&7;
		if (meta == 0) {
			return side == 0 ? 9 : side == 1 ? 0 : 4;
		}
		if (meta == 1) {
			return side == 1 ? 0 : side == 0 ? 9 : 28;
		}
		if (meta == 4) {
			return side == 0 ? 0 : side == 1 ? 9 : 27;
		}
		if (meta == 3) {
			return side == 1 ? 9 : side == 0 ? 0 : 3;
		}
		if (meta == 6) {
			return side < 2 ? 21 : 26;
		}
		if (meta == 2)
			return 9;
		if (meta == 7)
			return 0;
		if (meta == 5)
			return 22;
		if (meta == 5 || meta == 7 || meta == 3 || meta == 0 || meta == 2)
			return 22;
		return 21;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return meta == 5;
	}

	@Override
	public int getNumberTextures() {
		return 29;
	}

	@Override
	protected TileEntity getTileEntityForPosition(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-8, y-5, z-8, x+8, y+5, z+8);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			if (ReactorTiles.getTE(world, xyz[0], xyz[1]+1, xyz[2]) == ReactorTiles.INJECTOR) {
				TileEntityFusionInjector te = (TileEntityFusionInjector)world.getTileEntity(xyz[0], xyz[1]+1, xyz[2]);
				return te;
			}
		}
		return null;
	}

}
