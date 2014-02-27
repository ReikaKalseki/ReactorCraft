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

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReactorTileModelled extends BlockReactorTile {

	public BlockReactorTileModelled(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getRenderType() {
		return -1;
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

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff)
	{
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ReactorCraft/Textures/TileEntity/", world, x, y, z, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ReactorCraft.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addBlockHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff)
	{
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ReactorCraft/Textures/TileEntity/", world, tg, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ReactorCraft.class);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		if (r == ReactorTiles.MAGNET) {
			TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getBlockTileEntity(x, y, z);
			float ang = te.getAngle();
			double a = 1.5*Math.abs(Math.cos(Math.toRadians(ang)));
			double b = 1.5*Math.abs(Math.sin(Math.toRadians(ang)));
			return ReikaAABBHelper.getBlockAABB(x, y, z).expand(b, 1.5, a);
		}
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

}
