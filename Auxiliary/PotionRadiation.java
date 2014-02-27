/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.ReactorCraft.ReactorCraft;

public class PotionRadiation extends Potion {

	public PotionRadiation(int par1, boolean par2) {
		super(par1, par2, 0x111111);
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		e.removePotionEffect(Potion.regeneration.id);
		e.removePotionEffect(Potion.field_76443_y.id);
		e.removePotionEffect(Potion.moveSpeed.id);
		e.removePotionEffect(Potion.digSpeed.id);
		e.removePotionEffect(Potion.damageBoost.id);
		e.removePotionEffect(Potion.heal.id);
		e.removePotionEffect(Potion.jump.id);
		e.removePotionEffect(Potion.regeneration.id);
		e.removePotionEffect(Potion.fireResistance.id);
		e.removePotionEffect(Potion.resistance.id);
		e.removePotionEffect(Potion.nightVision.id);
		Random r = new Random();
		float h = e.getHealth();
		float mh = e.getMaxHealth();
		float f = h/mh;
		if ((int)(h/4) <= 0)
			return;
		if (f >= 0.5) {
			if (r.nextInt((int)h/4) == 0)
				e.attackEntityFrom(ReactorCraft.radiationDamage, 1);
		}
		else if  (f > 0.25) {
			if (r.nextInt((int)h/2) == 0)
				e.attackEntityFrom(ReactorCraft.radiationDamage, 1);
		}
		else if (h > 1) {
			if (r.nextInt((int)h) == 0)
				e.attackEntityFrom(ReactorCraft.radiationDamage, 1);
		}

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			FoodStats st = ep.getFoodStats();
			st.addExhaustion(0.25F);

			ReikaPlayerAPI.setPlayerWalkSpeed(ep, 0.075F);
		}

		if (!e.isPotionActive(Potion.confusion))
			e.addPotionEffect(new PotionEffect(Potion.confusion.id, 120, 0));

		e.addPotionEffect(new PotionEffect(Potion.poison.id, 20, 0));
	}

	@Override
	public boolean isReady(int time, int amp)
	{
		return time%20 == 0;
	}

}
