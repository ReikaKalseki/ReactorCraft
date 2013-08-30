/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Registry.ReactorItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReactorTab extends CreativeTabs {

	public ReactorTab(int position, String tabID) {
		super(position, tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return ReactorItems.WASTE.getStackOf();
	}

	@Override
	public String getTranslatedTabLabel() {
		return "ReactorCraft";
	}

}
