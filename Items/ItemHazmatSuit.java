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

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.ReactorCraft.ClientProxy;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorItems;

public class ItemHazmatSuit extends ItemArmor implements IndexedItemSprites {

	private int sprite;

	public ItemHazmatSuit(int ID, int ind, int render, int type) {
		super(ID, ReactorCraft.HAZ, ReactorCraft.proxy.hazmat, type);

		maxStackSize = 1;
		sprite = ind;
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
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

	@Override
	public final String getArmorTexture(ItemStack is, Entity entity, int slot, String type) {
		ReactorItems item = ReactorItems.getEntry(is);
		String sg = ClientProxy.getArmorTextureAsset(item);
		return sg;
	}

}
