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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorOres;

public class BlockReactorOre extends Block {

	private Icon[] icons = new Icon[ReactorOres.oreList.length];

	public BlockReactorOre(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setResistance(5);
		this.setHardness(2);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		ItemStack is = new ItemStack(ReactorBlocks.ORE.getBlockID(), 1, metadata);
		li.add(is);
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tgt, World world, int x, int y, int z)
	{
		ReactorOres ore = ReactorOres.getOre(world, x, y, z);
		return new ItemStack(ReactorBlocks.ORE.getBlockID(), 1, ore.getBlockMetadata());
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 1; i < ReactorOres.oreList.length; i++) {
			icons[i] = ico.registerIcon(ReactorOres.oreList[i].getTextureName());
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z)
	{
		return ReactorOres.getOre(world, x, y, z) != ReactorOres.ENDBLENDE;
	}
}
