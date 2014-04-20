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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
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

		if (ReikaRandomHelper.doWithChance(e.getHealth()/e.getHealth()*50))
			e.attackEntityFrom(ReactorCraft.radiationDamage, 1);

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			ep.getFoodStats().addExhaustion(40);

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
