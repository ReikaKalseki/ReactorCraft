/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.ReactorCraft.Auxiliary.NeutronBlock;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.RotaryCraft.API.Transducerable;

public class BlockSolenoidMulti extends BlockMultiBlock implements Transducerable, NeutronBlock {

	public BlockSolenoidMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberTextures() {
		return 11;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.SOLENOID)
			return false;

		if (!this.checkUpper(world, x, y, z, midX, midY, midZ, dir, blocks))
			return false;
		if (!this.checkLower(world, x, y, z, midX, midY, midZ, dir, blocks))
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
						int id = world.getBlockId(midX+i, midY+j, midZ+k);
						int meta = world.getBlockMetadata(midX+i, midY+j, midZ+k);
						if (id != blockID)
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
			int id = world.getBlockId(midX+i, midY, midZ);
			int meta = world.getBlockMetadata(midX+i, midY, midZ);
			if (id != blockID)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlockId(midX-i, midY, midZ);
			meta = world.getBlockMetadata(midX-i, midY, midZ);
			if (id != blockID)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlockId(midX, midY, midZ+i);
			meta = world.getBlockMetadata(midX, midY, midZ+i);
			if (id != blockID)
				return false;
			if (meta != 4)
				return false;

			id = world.getBlockId(midX, midY, midZ-i);
			meta = world.getBlockMetadata(midX, midY, midZ-i);
			if (id != blockID)
				return false;
			if (meta != 4)
				return false;

			if (i < 6) {
				id = world.getBlockId(midX+i, midY, midZ+i);
				meta = world.getBlockMetadata(midX+i, midY, midZ+i);
				//ReikaJavaLibrary.pConsole(i+" > "+id+":"+meta);
				if (id != blockID)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlockId(midX-i, midY, midZ+i);
				meta = world.getBlockMetadata(midX-i, midY, midZ+i);
				if (id != blockID)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlockId(midX+i, midY, midZ-i);
				meta = world.getBlockMetadata(midX+i, midY, midZ-i);
				if (id != blockID)
					return false;
				if (meta != 4)
					return false;

				id = world.getBlockId(midX-i, midY, midZ-i);
				meta = world.getBlockMetadata(midX-i, midY, midZ-i);
				if (id != blockID)
					return false;
				if (meta != 4)
					return false;
			}
		}
		return true;
	}

	private boolean checkCorners(World world, int x, int y, int z, int midX, int midY, int midZ, ForgeDirection dir, StructuredBlockArray blocks) {
		for (int i = 6; i <= 6; i++) {
			int id = world.getBlockId(midX-i, midY+1, midZ-i);
			int meta = world.getBlockMetadata(midX-i, midY+1, midZ-i);
			if (id != blockID)
				return false;
			if (meta != 1)
				return false;

			id = world.getBlockId(midX-i, midY-1, midZ-i);
			meta = world.getBlockMetadata(midX-i, midY-1, midZ-i);
			if (id != blockID)
				return false;
			if (meta != 1)
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

			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dx = midX+d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dx = midX+i;
			dz = midZ+d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dz = midZ-d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
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

			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dx = midX+d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dx = midX+i;
			dz = midZ+d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;

			dz = midZ-d;
			id = world.getBlockId(dx, dy, dz);
			meta = world.getBlockMetadata(dx, dy, dz);
			if (id != blockID)
				return false;
			if (m != meta)
				return false;
		}
		return true;
	}

	@Override
	public void breakMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x+1, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x-1, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y+1, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y-1, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z+1, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z-1, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (meta >= 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta-8, 3);
			}
		}
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.SOLENOID) {
			TileEntitySolenoidMagnet te = (TileEntitySolenoidMagnet)world.getBlockTileEntity(midX, midY, midZ);
			te.hasMultiBlock = false;
		}
	}

	@Override
	public void onCreateFullMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (meta < 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta+8, 3);
			}
		}
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) == ReactorTiles.SOLENOID) {
			TileEntitySolenoidMagnet te = (TileEntitySolenoidMagnet)world.getBlockTileEntity(midX, midY, midZ);
			te.hasMultiBlock = true;
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
			boolean f = world.getBlockId(x+1, y, z) == blockID || world.getBlockId(x-1, y, z) == blockID;
			boolean f2 = world.getBlockId(x, y, z+1) == blockID || world.getBlockId(x, y, z-1) == blockID;
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
			return 2;
		}
		if (meta == 0 || meta == 1) {
			boolean f = world.getBlockId(x+1, y, z) == blockID || world.getBlockId(x-1, y, z) == blockID;
			boolean f2 = world.getBlockId(x, y, z+1) == blockID || world.getBlockId(x, y, z-1) == blockID;
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
		if (meta == 2 || meta == 3)
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
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBoundsRanged(world, x, y, z, blockID, x-20, y-3, z-20, x+20, y+3, z+20, 1);
		int midX = blocks.getMinX()+blocks.getSizeX()/2;
		int midY = blocks.getMinY()+blocks.getSizeY()/2;
		int midZ = blocks.getMinZ()+blocks.getSizeZ()/2;
		if (ReactorTiles.getTE(world, midX, midY, midZ) != ReactorTiles.SOLENOID)
			return li;
		TileEntitySolenoidMagnet te = (TileEntitySolenoidMagnet)world.getBlockTileEntity(midX, midY, midZ);
		li.addAll(te.getMessages(world, midX, midY, midZ, side));
		return li;
	}

}
