/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ReactorItemBase extends Item implements IndexedItemSprites {

	private int index;

	public ReactorItemBase(int ID, int tex) {
		super(ID);
		index = tex;
		this.setCreativeTab(ReactorCraft.tabRctr);
		if (this.getDataValues() > 1) {
			hasSubtypes = true;
			this.setMaxDamage(0);
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return index+this.getTextureOffset(is);
	}

	public int getTextureOffset(ItemStack is) {
		return ReactorItems.getEntry(is).hasMetadataSprites() ? is.getItemDamage() : 0;
	}

	@Override
	public final void registerIcons(IconRegister ico) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		ReactorItems ri = ReactorItems.getEntryByID(ID);
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			if (ri.isAvailableInCreative(item))
				li.add(item);
		}
	}

	public final int getDataValues() {
		ReactorItems i = ReactorItems.getEntryByID(itemID);
		if (i == null)
			return 0;
		return i.getNumberMetadatas();
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		if (this.getDataValues() <= 1)
			return super.getUnlocalizedName(is);
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	public Class getTextureReferenceClass() {
		return ReactorCraft.class;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ReactorCraft/Textures/Items/items1.png";
	}



	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer ep) {
		this.checkAchievements(ep, is);
	}

	private void checkAchievements(EntityPlayer player, ItemStack item) {
		if (item.itemID == ReactorItems.DEPLETED.getShiftedItemID()) {
			ReactorAchievements.DEPLETED.triggerAchievement(player);
		}
		if (item.itemID == ReactorItems.PELLET.getShiftedItemID()) {
			ReactorAchievements.PEBBLE.triggerAchievement(player);
		}
	}
}
