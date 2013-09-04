/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Registry.MatBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockReactorMat extends ItemBlock {

	public ItemBlockReactorMat(int ID) {
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

	private int getDataValues() {
		return MatBlocks.matList.length;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		if (this.getDataValues() <= 1)
			return super.getUnlocalizedName(is);
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
}
