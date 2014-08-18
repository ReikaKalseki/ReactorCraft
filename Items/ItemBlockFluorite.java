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

import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorOptions;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockFluorite extends ItemBlock {

	public ItemBlockFluorite(Block b) {
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

	private int getDataValues() {
		if (ReactorOptions.RAINBOW.getState() && this == Item.getItemFromBlock(ReactorBlocks.FLUORITEORE.getBlockInstance()))
			return 1;
		return FluoriteTypes.colorList.length;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		int d = is.getItemDamage()%8;
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		ReactorBlocks b = ReactorBlocks.FLUORITEORE.matchItem(itemstack) ? ReactorBlocks.FLUORITEORE : ReactorBlocks.FLUORITE;
		return b.getMultiValuedName(itemstack.getItemDamage());
	}
}
