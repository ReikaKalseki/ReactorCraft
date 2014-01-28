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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;

public class BlockSteam extends Block {

	/**
	 * Metadata Flag Map:
	 * 1 - do not decay when moving up
	 * 2 - can provide power to a turbine
	 * 4 - is ammonia gas
	 * 8 - unused
	 */

	public BlockSteam(int par1, Material mat) {
		super(par1, mat);
		this.setCreativeTab(ReactorCraft.tabRctr);
		this.setTickRandomly(true);
		this.setResistance(3600000);
		this.setLightOpacity(0);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int p5, int meta) {

	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		if (e != null) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int maxh = 256;
		if (y > maxh) {
			world.setBlock(x, y, z, 0);
			return;
		}
		int meta = world.getBlockMetadata(x, y, z);
		this.defaultMovement(world, x, y, z, rand, meta);
		world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
	}

	private void directionalMovement(World world, int x, int y, int z, Random rand, int meta) {
		ForgeDirection dir;
		switch(meta-4) {
		case 0:
			dir = ForgeDirection.EAST;
			break;
		case 1:
			dir = ForgeDirection.WEST;
			break;
		case 2:
			dir = ForgeDirection.SOUTH;
			break;
		case 3:
			dir = ForgeDirection.NORTH;
			break;
		default:
			dir = ForgeDirection.UP;
			break;
		}

		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;

		if (this.canMoveInto(world, dx, dy, dz)) {
			world.setBlock(x, y, z, 0);
			world.setBlock(dx, dy, dz, blockID, meta, 3);
		}
		else
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
		world.markBlockForRenderUpdate(x, y, z);
		world.markBlockForRenderUpdate(dx, dy, dz);
	}

	private void defaultMovement(World world, int x, int y, int z, Random rand, int meta) {
		if (ReactorTiles.getTE(world, x, y+1, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(x, y+1, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlock(x, y, z, 0);
			}
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x+1, y, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(x+1, y, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlock(x, y, z, 0);
			}
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x-1, y, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(x-1, y, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlock(x, y, z, 0);
			}
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x, y, z+1) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(x, y, z+1);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlock(x, y, z, 0);
			}
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x, y, z-1) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(x, y, z-1);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlock(x, y, z, 0);
			}
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else if (this.canMoveInto(world, x, y+1, z)) {
			if (((meta&1) != 0) || ReikaRandomHelper.doWithChance(80))
				world.setBlock(x, y+1, z, blockID, this.getTransmittedMetadata(meta, ForgeDirection.UP), 3);
			world.setBlock(x, y, z, 0);
			world.markBlockForRenderUpdate(x, y, z);
			world.markBlockForRenderUpdate(x, y+1, z);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
			return;
		}
		else {
			ForgeDirection[] dir = new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.NORTH};
			ReikaArrayHelper.shuffleArray(dir);
			for (int i = 0; i < dir.length; i++) {
				int dx = x+dir[i].offsetX;
				int dy = y+dir[i].offsetY;
				int dz = z+dir[i].offsetZ;
				if (this.canMoveInto(world, dx, dy, dz)) {
					world.setBlock(dx, dy, dz, blockID, this.getTransmittedMetadata(meta, dir[i]), 3);
					world.setBlock(x, y, z, 0);
					//ReikaJavaLibrary.pConsole(x+","+y+","+z+"->"+dx+","+dy+","+dz);
					world.markBlockForRenderUpdate(x, y, z);
					world.markBlockForRenderUpdate(dx, dy, dz);
					world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
					return;
				}
			}
		}
	}

	public int getTransmittedMetadata(int original_meta, ForgeDirection dir) {
		if (dir == ForgeDirection.UP)
			return original_meta;
		return (original_meta&2) != 0 ? original_meta-2 : original_meta;
	}

	public boolean canMoveInto(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == 0)
			return true;
		if (id == blockID)
			return false;
		if (Block.blocksList[id] instanceof BlockFluid)
			return false;
		return ReikaWorldHelper.softBlocks(world, x, y, z);
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
	public int idDropped(int id, Random r, int fortune) {
		return 0;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack is) {

	}

	@Override
	public boolean canSilkHarvest() {
		return false;
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
		//return Block.cloth.getIcon(s, meta);
		return blockIcon;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		blockIcon = ico.registerIcon("ReactorCraft:steam");
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.values()[side];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = iba.getBlockId(dx, dy, dz);
		return id != blockID && id != ReactorBlocks.MODELREACTOR.getBlockID();
	}

	@Override
	public boolean isBlockReplaceable(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!(e instanceof EntityItem || e instanceof EntityXPOrb)) {
			e.attackEntityFrom(DamageSource.onFire, 1);
			int meta = world.getBlockMetadata(x, y, z);
			if ((meta&4) != 0) {
				if (e instanceof EntityLivingBase)
					((EntityLivingBase)e).addPotionEffect(new PotionEffect(Potion.poison.id, 200, 0));
			}
		}
	}

}
