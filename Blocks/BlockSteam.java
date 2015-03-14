/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ClearSteamCommand;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;

public class BlockSteam extends Block {

	/**
	 * Metadata Flag Map:
	 * 1 - do not decay when moving up
	 * 2 - can provide power to a turbine
	 * 4 - is ammonia gas
	 * 8 - "has moved horizontally"
	 */

	public BlockSteam(Material mat) {
		super(mat);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
		this.setTickRandomly(true);
		this.setResistance(3600000);
		this.setLightOpacity(0);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p5, int meta) {

	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		if (e != null) {
			world.setBlockMetadataWithNotify(x, y, z, 3, 3);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int maxh = 256;
		if (y > maxh || ClearSteamCommand.clearSteam()) {
			world.setBlockToAir(x, y, z);
			return;
		}
		int meta = world.getBlockMetadata(x, y, z);
		this.defaultMovement(world, x, y, z, rand, meta);
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
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
			world.setBlockToAir(x, y, z);
			world.setBlock(dx, dy, dz, this, meta, 3);
		}
		else
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
		world.markBlockForUpdate(x, y, z);
		world.markBlockForUpdate(dx, dy, dz);
	}

	private void defaultMovement(World world, int x, int y, int z, Random rand, int meta) {
		if (world.getBlock(x, y+1, z) == ReactorBlocks.MATS.getBlockInstance() && world.getBlockMetadata(x, y+1, z) == MatBlocks.SCRUBBER.ordinal()) {
			world.setBlockToAir(x, y, z);
			return;
		}
		if (ReactorTiles.getTE(world, x, y+1, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x, y+1, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlockToAir(x, y, z);
			}
			else if (world.getBlock(dx, dy, dz) == ReactorBlocks.GENERATORMULTI.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 11) {
				if (this.canMoveInto(world, te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2)) {
					world.setBlock(te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2, this, this.getTransmittedMetadata(meta, dir), 3);
					world.setBlockToAir(x, y, z);
				}
			}
			else {
				world.setBlockToAir(x, y, z);
			}
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x+1, y, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x+1, y, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlockToAir(x, y, z);
			}
			else if (world.getBlock(dx, dy, dz) == ReactorBlocks.GENERATORMULTI.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 11) {
				if (this.canMoveInto(world, te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2)) {
					world.setBlock(te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2, this, this.getTransmittedMetadata(meta, dir), 3);
					world.setBlockToAir(x, y, z);
				}
			}
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x-1, y, z) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x-1, y, z);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlockToAir(x, y, z);
			}
			else if (world.getBlock(dx, dy, dz) == ReactorBlocks.GENERATORMULTI.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 11) {
				if (this.canMoveInto(world, te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2)) {
					world.setBlock(te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2, this, this.getTransmittedMetadata(meta, dir), 3);
					world.setBlockToAir(x, y, z);
				}
			}
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x, y, z+1) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x, y, z+1);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlockToAir(x, y, z);
			}
			else if (world.getBlock(dx, dy, dz) == ReactorBlocks.GENERATORMULTI.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 11) {
				if (this.canMoveInto(world, te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2)) {
					world.setBlock(te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2, this, this.getTransmittedMetadata(meta, dir), 3);
					world.setBlockToAir(x, y, z);
				}
			}
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return;
		}
		else if (ReactorTiles.getTE(world, x, y, z-1) == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x, y, z-1);
			ForgeDirection dir = te.getSteamMovement();
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.canMoveInto(world, dx, dy, dz)) {
				world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir), 3);
				world.setBlockToAir(x, y, z);
			}
			else if (world.getBlock(dx, dy, dz) == ReactorBlocks.GENERATORMULTI.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 3) {
				if (this.canMoveInto(world, te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2)) {
					world.setBlock(te.xCoord+dir.offsetX*2, te.yCoord+3, te.zCoord+dir.offsetZ*2, this, this.getTransmittedMetadata(meta, dir), 3);
					world.setBlockToAir(x, y, z);
				}
			}
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(dx, dy, dz);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return;
		}
		else if (this.canMoveInto(world, x, y+1, z)) {
			//ReikaJavaLibrary.pConsole(meta+":"+this.getTransmittedMetadata(meta, ForgeDirection.UP), Side.SERVER);
			if (((meta&1) != 0) || ReikaRandomHelper.doWithChance(80))
				world.setBlock(x, y+1, z, this, this.getTransmittedMetadata(meta, ForgeDirection.UP), 3);
			world.setBlockToAir(x, y, z);
			world.markBlockForUpdate(x, y, z);
			world.markBlockForUpdate(x, y+1, z);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+">>"+x+","+(y+1)+","+z);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
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
					world.setBlock(dx, dy, dz, this, this.getTransmittedMetadata(meta, dir[i]), 3);
					world.setBlockToAir(x, y, z);
					//ReikaJavaLibrary.pConsole(x+","+y+","+z+"->"+dx+","+dy+","+dz);
					world.markBlockForUpdate(x, y, z);
					world.markBlockForUpdate(dx, dy, dz);
					world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
					return;
				}
			}
		}
	}

	public int getTransmittedMetadata(int original_meta, ForgeDirection dir) {
		if (dir == ForgeDirection.UP) {
			return (original_meta & 8) == 0 ? original_meta : 1+(original_meta&4);
		}
		return original_meta | 8;
	}

	public boolean canMoveInto(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z+" >> "+id+":"+world.getBlockMetadata(x, y, z));
		if (id == Blocks.air)
			return true;
		if (id == this)
			return false;
		if (id instanceof BlockLiquid || id instanceof BlockFluidBase)
			return false;
		return ReikaWorldHelper.softBlocks(world, x, y, z);
	}

	@Override
	public int tickRate(World world) {
		return 2;
	}

	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack is) {

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

	public IIcon getBlockTexture(IBlockAccess iba, int x, int y, int z) {
		return this.getIcon(0, 0);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		//return Blocks.wool.getIcon(s, meta);
		return blockIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("ReactorCraft:steam");
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.values()[side];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block id = iba.getBlock(dx, dy, dz);
		return id != this && id != ReactorBlocks.MODELREACTOR.getBlockInstance();
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
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
