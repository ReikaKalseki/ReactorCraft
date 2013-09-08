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

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLiquid extends Item {

	private final String iconName;

	public ItemLiquid(int ID, String icon) {
		super(ID);
		maxStackSize = 64;
		this.setCreativeTab(null);
		iconName = icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IconRegister ico) {
		itemIcon = ico.registerIcon("ReactorCraft:"+iconName);
	}

}
