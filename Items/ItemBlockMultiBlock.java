/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemBlockMultiBlock extends ItemBlock {

	public ItemBlockMultiBlock(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	public BlockMultiBlock getMultiBlockInstance() {
		return (BlockMultiBlock)Block.blocksList[itemID];
	}

	private int getDataValues() {
		return this.getMultiBlockInstance().getNumberVariants();
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		return this.getMultiBlockInstance().getName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}
}
