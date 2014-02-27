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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockTurbineMulti extends BlockMultiBlock {

	public BlockTurbineMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberTextures() {
		return 1;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		return false;
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {

	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z) {

	}

	@Override
	public int getNumberVariants() {
		return 5;
	}

	@Override
	protected String getIconBaseName() {
		return "turbine";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		return 0;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		return 0;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return true;
	}

}
