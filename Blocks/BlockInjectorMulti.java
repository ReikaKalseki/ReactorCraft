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

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockInjectorMulti extends BlockMultiBlock {

	public BlockInjectorMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 2;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void onCreateFullMultiBlock(World world, int x, int y, int z) {

	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {

	}

	@Override
	protected String getIconBaseName() {
		return "injector";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		return 0;
	}

	@Override
	public int getItemTextureIndex(int meta) {
		return meta;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return true;
	}

}
