/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.EnumSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Interfaces.PermaPotion;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class PotionRadiation extends Potion implements PermaPotion {

	public PotionRadiation(int par1) {
		super(par1, true, 0x111111);

		TickRegistry.instance.registerTickHandler(PotionMapCMEAvoidance.instance);
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		boolean p = e.worldObj.difficultySetting == EnumDifficulty.PEACEFUL;
		int c = p ? 75 : 50;
		if (level >= RadiationIntensity.HIGHLEVEL.ordinal()) {
			c *= 1.1;
		}
		if (level >= RadiationIntensity.LETHAL.ordinal()) {
			c *= 1.25;
		}
		if (ReikaRandomHelper.doWithChance(e.getHealth()/e.getMaxHealth()*c)) {
			int amt = p ? 2 : 1;
			e.attackEntityFrom(ReactorCraft.radiationDamage, amt);
		}

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;

			ReikaPlayerAPI.setPlayerWalkSpeed(ep, 0.075F);
			ReikaPlayerAPI.setFoodLevel(ep, 1);
			ReikaPlayerAPI.setSaturationLevel(ep, 0);
		}
	}

	@Override
	public boolean isReady(int time, int amp)
	{
		return time%20 == 5;
	}

	@Override
	public boolean canBeCleared(EntityLivingBase e, PotionEffect pot) {
		return e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode;
	}

	public static class PotionMapCMEAvoidance implements TickHandler {

		private static final PotionMapCMEAvoidance instance = new PotionMapCMEAvoidance();

		private PotionMapCMEAvoidance() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			EntityPlayer e = (EntityPlayer)tickData[0];
			if (e.isPotionActive(ReactorCraft.radiation)) {
				e.removePotionEffect(Potion.regeneration.id);
				e.removePotionEffect(Potion.field_76443_y.id);
				e.removePotionEffect(Potion.moveSpeed.id);
				e.removePotionEffect(Potion.digSpeed.id);
				e.removePotionEffect(Potion.damageBoost.id);
				e.removePotionEffect(Potion.heal.id);
				e.removePotionEffect(Potion.jump.id);
				e.removePotionEffect(Potion.fireResistance.id);
				e.removePotionEffect(Potion.resistance.id);
				e.removePotionEffect(Potion.nightVision.id);

				if (!e.isPotionActive(Potion.confusion))
					e.addPotionEffect(new PotionEffect(Potion.confusion.id, 120, 0));

				e.addPotionEffect(new PotionEffect(Potion.poison.id, 20, 0));
				e.addPotionEffect(new PotionEffect(Potion.jump.id, 20, -2));
			}
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.PLAYER);
		}

		@Override
		public boolean canFire(Phase p) {
			return true;
		}

		@Override
		public String getLabel() {
			return "Radiation Potion Control";
		}
	}

}
