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
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
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
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		return false;
	}

	@Override
	protected String getIconBaseName() {
		return "injector";
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		return icons[0];
	}

}
