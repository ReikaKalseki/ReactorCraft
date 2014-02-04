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
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockHeaterMulti extends BlockMultiBlock {

	public BlockHeaterMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 4;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	protected String getIconBaseName() {
		return "heater";
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		return icons[0];
	}

}
