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

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.ReactorCraft.ReactorCraft;

public class ItemHazmatSuit extends ItemArmor implements IndexedItemSprites {

	private int sprite;

	public ItemHazmatSuit(int ID, int ind, int render, int type) {
		super(ID, EnumArmorMaterial.CLOTH, render, type);

		maxStackSize = 1;
		sprite = ind;
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return sprite;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ReactorCraft/Textures/Items/items1.png";
	}

	@Override
	public Class getTextureReferenceClass() {
		return ReactorCraft.class;
	}

}
