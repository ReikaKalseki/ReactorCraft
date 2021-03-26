/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.NeutronBlock;
import Reika.ReactorCraft.Base.TileEntityLine;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityHeatPipe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSteamLine extends BlockReactorTileModelled implements NeutronBlock {

	public BlockSteamLine(Material par3Material) {
		super(par3Material);
		this.setHardness(0F);
		this.setResistance(1F);
		this.setLightLevel(0F);
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final int getRenderType() {
		return ReactorCraft.proxy.lineRender;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return null;
	}


	@Override
	public boolean canRenderInPass(int pass)
	{
		return pass == 0;
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	@Override
	protected boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		super.onNeighborBlockChange(world, x, y, z, id);
		TileEntityLine te = (TileEntityLine)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityLine te = (TileEntityLine)world.getTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).contract(d, d, d);
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		TileEntityLine te = (TileEntityLine)world.getTileEntity(x, y, z);
		te.onEntityCollided(e);
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z) {
		if (ReactorTiles.getTE(iba, x, y, z) == ReactorTiles.HEATPIPE) {
			TileEntityHeatPipe te = (TileEntityHeatPipe)iba.getTileEntity(x, y, z);
			float f = te.getBrightness();
			World world = Minecraft.getMinecraft().theWorld;
			int i1 = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
			int l = Math.max(this.getLightValue(iba, x, y, z), Math.round(15*f));
			int j1 = Math.max(l, world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z));
			return i1 << 20 | j1 << 4;
		}
		return super.getMixedBrightnessForBlock(iba, x, y, z);
	}
}
