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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.RotaryCraft.ClientProxy;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Entities.EntityDischarge;

public class BlockDuct extends BlockReactorTile {

	private static final Icon[][] pipeIcons = new Icon[2][2];
	private static Icon glowIcon;

	public BlockDuct(int ID, Material mat) {
		super(ID, mat);
		this.setHardness(0F);
		this.setResistance(1F);
		this.setLightValue(0F);
	}

	@Override
	public final int getRenderType() {
		return RotaryCraft.proxy.pipeRender;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getBlockTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getBlockTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getBlockTileEntity(x, y, z);
		return te != null && te.getLevel() > 0 && te.getFluidType() != null ? te.getFluidType().getLuminosity(te.worldObj, x, y, z) : 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (ReactorTiles.getTE(world, x, y, z) == ReactorTiles.MAGNETPIPE) {
			TileEntityMagneticPipe te = (TileEntityMagneticPipe)world.getBlockTileEntity(x, y, z);
			int charge = te.getCharge();
			if (charge > 0) {
				double sx = te.getAimX();
				double sy = te.getAimY();
				double sz = te.getAimZ();
				EntityDischarge ed = new EntityDischarge(world, sx, sy, sz, charge, e.posX, e.posY+e.getEyeHeight()/4, e.posZ);
				te.onDischarge(-charge, 1);
				if (!world.isRemote)
					world.spawnEntityInWorld(ed);
			}
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		s = Math.min(s, 1);
		return pipeIcons[meta][s];
	}

	@Override
	public void registerIcons(IconRegister ico) {
		pipeIcons[0][0] = Block.hardenedClay.getIcon(1, 0);
		pipeIcons[1][0] = Block.blockGold.getIcon(0, 0);

		pipeIcons[0][1] = Block.glass.getIcon(0, 0);
		pipeIcons[1][1] = ico.registerIcon("rotarycraft:obsidiglass");

		glowIcon = ico.registerIcon("reactorcraft:glowgold");
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ClientProxy.pipe.renderPass = pass;
		return true;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return 0;
	}

	public static Icon getGlow() {
		return glowIcon;
	}
}
