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
import net.minecraft.util.Icon;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.MatBlocks;

public class BlockReactorMat extends Block {

	private Icon[][] icons = new Icon[16][6];

	public BlockReactorMat(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setHardness(1.5F);
		this.setResistance(10F);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < MatBlocks.matList.length; i++) {
			if (MatBlocks.matList[i].isMultiSidedTexture()){
				for (int j = 0; j < 6; j++) {
					icons[i][j] = ico.registerIcon("ReactorCraft:mat_"+MatBlocks.matList[i].name().toLowerCase()+"_"+j);
				}
			}
			else {
				for (int j = 0; j < 6; j++) {
					icons[i][j] = ico.registerIcon("ReactorCraft:mat_"+MatBlocks.matList[i].name().toLowerCase());
				}
			}
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}
