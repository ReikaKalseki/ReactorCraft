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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorOptions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockFluorite extends ItemBlock {

	public ItemBlockFluorite(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < this.getDataValues(ID); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	private int getDataValues(int id) {
		if (ReactorOptions.RAINBOW.getState() && id == ReactorBlocks.FLUORITEORE.getBlockID())
			return 1;
		return FluoriteTypes.colorList.length;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		if (this.getDataValues(is.itemID) <= 1)
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
