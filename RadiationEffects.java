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

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.PotionEffect;

public class RadiationEffects {

	public static void applyEffects(EntityLiving e) {
		if (!e.isPotionActive(ReactorCraft.radiation))
			e.addPotionEffect(new PotionEffect(ReactorCraft.radiation.id, 12000, 0));
	}

	public static void applyPulseEffects(EntityLiving e) {
		if (!e.isPotionActive(ReactorCraft.radiation))
			e.addPotionEffect(new PotionEffect(ReactorCraft.radiation.id, 20, 0));
	}

}
