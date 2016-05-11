/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemBlockMultiBlock extends ItemBlock {

	public ItemBlockMultiBlock(Block b) {
		super(b);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	public BlockMultiBlock getMultiBlockInstance() {
		return (BlockMultiBlock)field_150939_a;
	}

	private int getDataValues() {
		return this.getMultiBlockInstance().getNumberVariants();
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return this.getMultiBlockInstance().getName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}
}
