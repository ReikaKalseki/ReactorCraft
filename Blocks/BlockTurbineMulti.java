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

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.SlicedBlockBlueprint;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.API.Transducerable;

public class BlockTurbineMulti extends BlockMultiBlock implements Transducerable {

	private final SlicedBlockBlueprint setup;

	public BlockTurbineMulti(int par1, Material par2Material) {
		super(par1, par2Material);
		setup = new SlicedBlockBlueprint();
		this.initMap();
	}

	@Override
	public int getNumberTextures() {
		return 6;
	}

	public int getThickness(int stage) {
		return setup.getHeight(stage)/2;
	}

	private void initMap() {
		setup.addMapping('t', blockID, 0);
		setup.addMapping('h', blockID, 1);
		setup.addMapping('e', blockID, 2);

		setup.addSlice(
				"---hhhhh---",
				"--hhhthhh--",
				"-hhttttthh-",
				"hhttttttthh",
				"hhttttttthh",
				"httttxtttth",
				"hhttttttthh",
				"hhttttttthh",
				"-hhttttthh-",
				"--hhhthhh--",
				"---hhhhh---"
				);

		setup.addSlice(
				"-----------",
				"---hhhhh---",
				"--hhttthh--",
				"-hhttttthh-",
				"-httttttth-",
				"-htttxttth-",
				"-httttttth-",
				"-hhttttthh-",
				"--hhttthh--",
				"---hhhhh---",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"----hhh----",
				"--hhhhhhh--",
				"--hhttthh--",
				"-hhttttthh-",
				"-hhttxtthh-",
				"-hhttttthh-",
				"--hhttthh--",
				"--hhhhhhh--",
				"----hhh----",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"-----------",
				"---hhhhh---",
				"--hhttthh--",
				"--httttth--",
				"--httxtth--",
				"--httttth--",
				"--hhttthh--",
				"---hhhhh---",
				"-----------",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"-----------",
				"----hhh----",
				"---hhhhh---",
				"--hhttthh--",
				"--hhtxthh--",
				"--hhttthh--",
				"---hhhhh---",
				"----hhh----",
				"-----------",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"-----------",
				"-----------",
				"---hhhhh---",
				"---hhthh---",
				"---htxth---",
				"---hhthh---",
				"---hhhhh---",
				"-----------",
				"-----------",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"-----------",
				"-----------",
				"----hhh----",
				"---hhhhh---",
				"---hhxhh---",
				"---hhhhh---",
				"----hhh----",
				"-----------",
				"-----------",
				"-----------"
				);

		setup.addSlice(
				"-----------",
				"-----------",
				"-----------",
				"-----------",
				"----eee----",
				"----exe----",
				"----eee----",
				"-----------",
				"-----------",
				"-----------",
				"-----------"
				);
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBounds(world, x, y, z, blockID, x-12, y-12, z-12, x+12, y+12, z+12);
		int sx;
		int sz;
		int n = this.checkForTurbines(world, x, y, z, dir, blocks); //only accept steam emitter at last turb stage
		if (n <= 0 || n > 7)
			return false;
		if (!this.checkForShape(world, x, y, z, dir, blocks, n))
			return false;
		return true;
	}

	private int checkForTurbines(World world, int x, int y, int z, ForgeDirection dir, StructuredBlockArray blocks) {
		int mx = blocks.getMinX()+blocks.getSizeX()/2;
		int my = blocks.getMinY()+blocks.getSizeY()/2;
		int mz = blocks.getMinZ()+blocks.getSizeZ()/2;
		int sx = dir.offsetX == 0 ? mx : dir.offsetX < 0 ? blocks.getMaxX() : blocks.getMinX();
		int sz = dir.offsetZ == 0 ? mz : dir.offsetZ < 0 ? blocks.getMaxZ() : blocks.getMinZ();
		int c = 0;
		for (int i = 0; i < setup.getLength(); i++) {
			int dx = sx+i*dir.offsetX;
			int dz = sz+i*dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, my, dz);
			if (r == ReactorTiles.BIGTURBINE) {
				c++;
				((TileEntityTurbineCore)world.getBlockTileEntity(dx, my, dz)).markForMulti();
			}
			else
				return c;
		}
		return c;
	}

	private boolean checkForShape(World world, int x, int y, int z, ForgeDirection dir, StructuredBlockArray blocks, int turbines) {
		int start = setup.getLength()-turbines-1;
		int mx = blocks.getMinX()+blocks.getSizeX()/2;
		int my = blocks.getMinY()+blocks.getSizeY()/2;
		int mz = blocks.getMinZ()+blocks.getSizeZ()/2;
		int sx = dir.offsetX == 0 ? mx : dir.offsetX < 0 ? blocks.getMaxX() : blocks.getMinX();
		int sz = dir.offsetZ == 0 ? mz : dir.offsetZ < 0 ? blocks.getMaxZ() : blocks.getMinZ();
		for (int i = start; i < setup.getLength(); i++) {
			int d = i-start;
			int dx = sx+d*dir.offsetX;
			int dz = sz+d*dir.offsetZ;
			boolean match = setup.checkAgainst(world, dx, my, dz, 5, 5, dir, i);
			if (!match)
				return false;
		}
		return true;
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		int tid = ReactorTiles.BIGTURBINE.getBlockID();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(blockID, tid), x-12, y-12, z-12, x+12, y+12, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int id = world.getBlockId(xyz[0], xyz[1], xyz[2]);
			if (id == blockID) {
				int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (meta >= 8) {
					world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta-8, 3);
				}
			}
			else if (id == tid) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(xyz[0], xyz[1], xyz[2]);
				te.setHasMultiBlock(false);
			}
		}
	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		int tid = ReactorTiles.BIGTURBINE.getBlockID();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(blockID, tid), x-12, y-12, z-12, x+12, y+12, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int id = world.getBlockId(xyz[0], xyz[1], xyz[2]);
			if (id == blockID) {
				int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (meta < 8) {
					world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta+8, 3);
				}
			}
			else if (id == tid) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(xyz[0], xyz[1], xyz[2]);
				te.setHasMultiBlock(true);
			}
		}
	}

	@Override
	public int getNumberVariants() {
		return 3;
	}

	@Override
	protected String getIconBaseName() {
		return "turbine";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		return meta >= 8 ? 5 : meta;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		return meta&7;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return meta <= 1;
	}

	@Override
	protected TileEntity getTileEntityForPosition(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBounds(world, x, y, z, blockID, x-12, y-12, z-12, x+12, y+12, z+12);
		int mx = blocks.getMinX()+blocks.getSizeX()/2;
		int my = blocks.getMinY()+blocks.getSizeY()/2;
		int mz = blocks.getMinZ()+blocks.getSizeZ()/2;
		return ReactorTiles.getTE(world, mx, my, mz) == ReactorTiles.BIGTURBINE ? world.getBlockTileEntity(mx, my, mz) : null;
	}

}
