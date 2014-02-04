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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemPlutonium extends ReactorItemBase {

	public ItemPlutonium(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity ep, int slot, boolean flag) {
		if (ep instanceof EntityLivingBase) {
			((EntityLivingBase) ep).addPotionEffect(RadiationEffects.getRadiationEffect(1200));
		}
	}
}
