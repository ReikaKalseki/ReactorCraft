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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.ReactorCraft.ReactorCraft;

public class BlockReactorMat extends Block {

	public BlockReactorMat(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

}
