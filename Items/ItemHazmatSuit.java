/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import Reika.DragonAPI.Interfaces.Item.IndexedItemSprites;
import Reika.ReactorCraft.ClientProxy;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorItems;

public class ItemHazmatSuit extends ItemArmor implements IndexedItemSprites, ISpecialArmor {

	private int sprite;

	public ItemHazmatSuit(int ind, int render, int type) {
		super(ReactorCraft.HAZ, ReactorCraft.proxy.hazmat, type);

		maxStackSize = 1;
		sprite = ind;
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctrItems);
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

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ReactorItems ir = ReactorItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource src, double damage, int slot) {
		ArmorProperties prop = new ArmorProperties(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
		return prop;
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 0;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

	}


}
