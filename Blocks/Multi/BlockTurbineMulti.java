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

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.SlicedBlockBlueprint;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamInjector;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;

public class BlockTurbineMulti extends BlockMultiBlock {

	private final SlicedBlockBlueprint setup;

	public BlockTurbineMulti(Material par2Material) {
		super(par2Material);
		setup = new SlicedBlockBlueprint();
		this.initMap();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta%8 == 2;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		switch(meta%8) {
			case 2:
				return new TileEntitySteamInjector();
			default:
				return null;
		}
	}

	@Override
	public int getNumberTextures() {
		return 6;
	}

	public int getThickness(int stage) {
		return setup.getHeight(stage)/2;
	}

	private void initMap() {
		setup.addMapping('t', this, 0);
		setup.addMapping('h', this, 1);
		setup.addMapping('e', this, 2);
		setup.addAntiMapping('b', this);

		setup.addSlice(
				"xbbhhhhhbbx",
				"bbhhhthhhbb",
				"bhhttttthhb",
				"hhttttttthh",
				"hhttttttthh",
				"httttxtttth",
				"hhttttttthh",
				"hhttttttthh",
				"bhhttttthhb",
				"bbhhhthhhbb",
				"xbbhhhhhbbx"
				);

		setup.addSlice(
				"xxbbbbbbbxx",
				"xbbhhhhhbbx",
				"bbhhttthhbb",
				"bhhttttthhb",
				"bhttttttthb",
				"bhtttxttthb",
				"bhttttttthb",
				"bhhttttthhb",
				"bbhhttthhbb",
				"xbbhhhhhbbx",
				"xxbbbbbbbxx"
				);

		setup.addSlice(
				"xxxbbbbbxxx",
				"xbbbhhhbbbx",
				"xbhhhhhhhbx",
				"bbhhttthhbb",
				"bhhttttthhb",
				"bhhttxtthhb",
				"bhhttttthhb",
				"bbhhttthhbb",
				"xbhhhhhhhbx",
				"xbbbhhhbbbx",
				"xxxbbbbbxxx"
				);

		setup.addSlice(
				"xxxxxxxxxxx",
				"xxbbbbbbbxx",
				"xbbhhhhhbbx",
				"xbhhttthhbx",
				"xbhttttthbx",
				"xbhttxtthbx",
				"xbhttttthbx",
				"xbhhttthhbx",
				"xbbhhhhhbbx",
				"xxbbbbbbbxx",
				"xxxxxxxxxxx"
				);

		setup.addSlice(
				"xxxxxxxxxxx",
				"xxxbbbbbxxx",
				"xxbbhhhbbxx",
				"xbbhhhhhbbx",
				"xbhhttthhbx",
				"xbhhtxthhbx",
				"xbhhttthhbx",
				"xbbhhhhhbbx",
				"xxbbhhhbbxx",
				"xxxbbbbbxxx",
				"xxxxxxxxxxx"
				);

		setup.addSlice(
				"xxxxxxxxxxx",
				"xxxxxxxxxxx",
				"xxbbbbbbbxx",
				"xxbhhhhhbxx",
				"xxbhhthhbxx",
				"xxbhtxthbxx",
				"xxbhhthhbxx",
				"xxbhhhhhbxx",
				"xxbbbbbbbxx",
				"xxxxxxxxxxx",
				"xxxxxxxxxxx"
				);

		setup.addSlice(
				"xxxxxxxxxxx",
				"xxxxxxxxxxx",
				"xxxbbbbbxxx",
				"xxbbhhhbbxx",
				"xxbhhhhhbxx",
				"xxbhhxhhbxx",
				"xxbhhhhhbxx",
				"xxbbhhhbbxx",
				"xxxbbbbbxxx",
				"xxxxxxxxxxx",
				"xxxxxxxxxxx"
				);

		setup.addSlice(
				"xxxxxxxxxxx",
				"xxxxxxxxxxx",
				"xxxxxxxxxxx",
				"xxxbbbbbxxx",
				"xxxbeeebxxx",
				"xxxbexebxxx",
				"xxxbeeebxxx",
				"xxxbbbbbxxx",
				"xxxxxxxxxxx",
				"xxxxxxxxxxx",
				"xxxxxxxxxxx"
				);
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-12, y-12, z-12, x+12, y+12, z+12);
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
				((TileEntityTurbineCore)world.getTileEntity(dx, my, dz)).markForMulti();
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
		Block tid = ReactorTiles.BIGTURBINE.getBlock();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(this, tid), x-12, y-12, z-12, x+12, y+12, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			Block b = c.getBlock(world);
			int meta = c.getBlockMetadata(world);
			if (b == this) {
				if (meta >= 8) {
					world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, meta-8, 3);
				}
			}
			else if (b == tid && meta == ReactorTiles.BIGTURBINE.getBlockMetadata()) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
				te.setHasMultiBlock(false);
			}
		}
	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);
		Block tid = ReactorTiles.BIGTURBINE.getBlock();
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, Arrays.asList(this, tid), x-12, y-12, z-12, x+12, y+12, z+12);
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			Block b = c.getBlock(world);
			if (b == this) {
				int meta = c.getBlockMetadata(world);
				if (meta < 8) {
					world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, meta+8, 3);
				}
			}
			else if (b == tid) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
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
		blocks.recursiveAddWithBounds(world, x, y, z, this, x-12, y-12, z-12, x+12, y+12, z+12);
		int mx = blocks.getMinX()+blocks.getSizeX()/2;
		int my = blocks.getMinY()+blocks.getSizeY()/2;
		int mz = blocks.getMinZ()+blocks.getSizeZ()/2;
		return ReactorTiles.getTE(world, mx, my, mz) == ReactorTiles.BIGTURBINE ? world.getTileEntity(mx, my, mz) : null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public SlicedBlockBlueprint getBlueprint() {
		return setup.copy();
	}

}
