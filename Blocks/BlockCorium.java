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

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCorium extends BlockStaticLiquid {

	private IIcon[] icon;

	public BlockCorium(Material par2Material) {
		super(par2Material);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);

	}

	@Override
	public int getRenderType() {
		return 4;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		icon = new IIcon[]{iconRegister.registerIcon("ReactorCraft:mat_slag"), iconRegister.registerIcon("ReactorCraft:slag_flow")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta)
	{
		return s != 0 && s != 1 ? icon[1] : icon[0];
	}

	/**
	 * Changes the block ID to that of an updating fluid.
	 */
	protected void setNotStationary(World world, int x, int y, int z)
	{
		if (world.isRaining() && ReikaRandomHelper.doWithChance(0.1)) {
			world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockInstance(), MatBlocks.SLAG.ordinal(), 3);
			return;
		}
		int l = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, ReactorBlocks.CORIUMFLOWING.getBlockInstance(), l, 2);
		world.scheduleBlockUpdate(x, y, z, ReactorBlocks.CORIUMFLOWING.getBlockInstance(), this.tickRate(world));
	}

	private void checkForHarden(World world, int x, int y, int z)
	{
		if (world.getBlock(x, y, z) == this) {
			boolean flag = false;

			if (flag || ReikaWorldHelper.getMaterial(world, x, y, z - 1) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x, y, z + 1) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x - 1, y, z) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x + 1, y, z) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x, y + 1, z) == Material.water)
				flag = true;

			if (flag) {
				world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockInstance(), MatBlocks.SLAG.ordinal(), 3);
				this.onNeighborBlockChange(world, x, y, z, this);
			}
		}
	}

}
