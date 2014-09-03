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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.MatBlocks;

public class BlockReactorMat extends Block {

	private IIcon[][] icons = new IIcon[16][6];

	public BlockReactorMat(Material par2Material) {
		super(par2Material);
		this.setHardness(1.5F);
		this.setResistance(10F);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
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
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}
