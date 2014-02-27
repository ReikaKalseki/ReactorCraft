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

import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCorium extends BlockStationary implements ILiquid {

	public BlockCorium(int par1, Material par2Material) {
		super(par1, par2Material);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);

	}

	@Override
	public int getRenderType() {
		return 4;
	}

	@Override
	public int stillLiquidId() {
		return ReactorBlocks.CORIUMSTILL.getBlockID();
	}

	@Override
	public boolean isMetaSensitive() {
		return false;
	}

	@Override
	public int stillLiquidMeta() {
		return 0;
	}

	@Override
	public boolean isBlockReplaceable(World world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		theIcon = new Icon[]{iconRegister.registerIcon("ReactorCraft:mat_slag"), iconRegister.registerIcon("ReactorCraft:slag_flow")};
	}

	/**
	 * Changes the block ID to that of an updating fluid.
	 */
	protected void setNotStationary(World world, int x, int y, int z)
	{
		if (world.isRaining() && ReikaRandomHelper.doWithChance(0.1)) {
			world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockID(), MatBlocks.SLAG.ordinal(), 3);
			return;
		}
		int l = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, ReactorBlocks.CORIUMFLOWING.getBlockID(), l, 2);
		world.scheduleBlockUpdate(x, y, z, ReactorBlocks.CORIUMFLOWING.getBlockID(), this.tickRate(world));
	}

	private void checkForHarden(World world, int x, int y, int z)
	{
		if (world.getBlockId(x, y, z) == blockID) {
			boolean flag = false;

			if (flag || world.getBlockMaterial(x, y, z - 1) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x, y, z + 1) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x - 1, y, z) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x + 1, y, z) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x, y + 1, z) == Material.water)
				flag = true;

			if (flag) {
				world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockID(), MatBlocks.SLAG.ordinal(), 3);
				this.triggerLavaMixEffects(world, x, y, z);
			}
		}
	}

}
