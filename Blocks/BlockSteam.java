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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;

public class BlockSteam extends Block {

	public BlockSteam(int par1, Material mat) {
		super(par1, mat);
		this.setCreativeTab(ReactorCraft.tabRctr);
		this.setTickRandomly(true);
		this.setResistance(0);
		this.setLightOpacity(0);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		//world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int p5, int meta) {

	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {/*
		int id = world.getBlockId(x, y+1, z);
		if (ReikaWorldHelper.softBlocks(world, x, y+1, z) && id != blockID) {
			if (ReikaMathLibrary.doWithChance(160)) {
				world.setBlock(x, y+1, z, blockID);
				world.setBlock(x, y, z, 0);
			}
			else {
				world.setBlock(x, y, z, 0);
			}
			return;
		}
		else {
			ForgeDirection[] dir = new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.NORTH};
			ReikaArrayHelper.shuffleArray(dir);
			for (int i = 0; i < dir.length; i++) {
				int dx = x+dir[i].offsetX;
				int dy = y+dir[i].offsetY;
				int dz = z+dir[i].offsetZ;
				int id2 = world.getBlockId(dx, dy, dz);
				if (ReikaWorldHelper.softBlocks(world, dx, dy, dz) && id2 != blockID) {
					world.setBlock(dx, dy, dz, blockID);
					world.setBlock(x, y, z, 0);
					world.markBlockForRenderUpdate(x, y, z);
					world.markBlockForRenderUpdate(dx, dy, dz);
					return;
				}
			}
			int dx = x+ForgeDirection.DOWN.offsetX;
			int dy = y+ForgeDirection.DOWN.offsetY;
			int dz = z+ForgeDirection.DOWN.offsetZ;
			int id2 = world.getBlockId(dx, dy, dz);
			if (ReikaWorldHelper.softBlocks(world, dx, dy, dz) && id2 != blockID) {
				//world.setBlock(dx, dy, dz, blockID);
				//world.setBlock(x, y, z, 0);
				world.markBlockForRenderUpdate(x, y, z);
				world.markBlockForRenderUpdate(dx, dy, dz);
				return;
			}
		}
		world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));*/
	}

	@Override
	public int tickRate(World world) {
		return 2;
	}

	@Override
	public boolean isAirBlock(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2) {
		return false;
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
	public int getRenderType() {
		return 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	public Icon getBlockTexture(IBlockAccess iba, int x, int y, int z) {
		return this.getIcon(0, 0);
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return this.blockIcon;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		blockIcon = ico.registerIcon("ReactorCraft:steam");
	}

}
