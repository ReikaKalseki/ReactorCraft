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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.RotaryCraft.ClientProxy;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.ConfigRegistry;

public class BlockDuct extends BlockReactorTile {

	private static final IIcon[][] pipeIcons = new IIcon[2][2];
	private static IIcon glowIcon;

	public BlockDuct(Material mat) {
		super(mat);
		this.setHardness(MathHelper.clamp_float(ConfigRegistry.PIPEHARDNESS.getFloat(), 0, 1));
		this.setResistance(1F);
		this.setLightLevel(0F);
	}

	@Override
	public final int getRenderType() {
		return RotaryCraft.proxy.pipeRender;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(x, y, z);
		double d = 0.125;
		double[] dd = new double[6];
		for (int i = 0; i < 6; i++)
			dd[i] = te.isConnectedDirectly(ForgeDirection.VALID_DIRECTIONS[i]) ? 0 : d;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x+dd[4], y+dd[1], z+dd[2], x+1-dd[5], y+1-dd[0], z+1-dd[3]);
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(x, y, z);
		return te != null && te.getLevel() > 0 && te.getFluidType() != null ? te.getFluidType().getLuminosity(te.worldObj, x, y, z) : 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (ReactorTiles.getTE(world, x, y, z) == ReactorTiles.MAGNETPIPE) {
			TileEntityMagneticPipe te = (TileEntityMagneticPipe)world.getTileEntity(x, y, z);
			int charge = te.getCharge();
			if (charge > 0) {
				double sx = te.getAimX();
				double sy = te.getAimY();
				double sz = te.getAimZ();
				EntityDischarge ed = new EntityDischarge(world, sx, sy, sz, charge, e.posX, e.posY+e.getEyeHeight()/4, e.posZ);
				te.onDischarge(1, 1);
				if (!world.isRemote)
					world.spawnEntityInWorld(ed);
				e.attackEntityFrom(DamageSource.generic, 1);
			}
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		s = Math.min(s, 1);
		return pipeIcons[meta][s];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		pipeIcons[0][0] = Blocks.hardened_clay.getIcon(1, 0);
		pipeIcons[1][0] = Blocks.gold_block.getIcon(0, 0);

		pipeIcons[0][1] = Blocks.glass.getIcon(0, 0);
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
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		return 0;
	}

	public static IIcon getGlow() {
		return glowIcon;
	}
}
