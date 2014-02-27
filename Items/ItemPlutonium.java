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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Base.ReactorItemBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;

public class ItemPlutonium extends ReactorItemBase {

	public ItemPlutonium(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean flag) {
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			if (!ep.capabilities.isCreativeMode) {
				if (!RadiationEffects.hasHazmatSuit(ep)) {
					ep.addPotionEffect(RadiationEffects.getRadiationEffect(1200));
					ReactorAchievements.PUPOISON.triggerAchievement(ep);
				}
			}
		}
	}
}
