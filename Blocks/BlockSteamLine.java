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

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.TileEntitySteamLine;

public class BlockSteamLine extends BlockReactorTileModelled {

	public BlockSteamLine(int par1, Material par3Material) {
		super(par1, par3Material);
		this.setHardness(0F);
		this.setResistance(1F);
		this.setLightValue(0F);
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
	public int idDropped(int id, Random r, int fortune) {
		return 0;
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
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntitySteamLine te = (TileEntitySteamLine)world.getBlockTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntitySteamLine te = (TileEntitySteamLine)world.getBlockTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}
}
