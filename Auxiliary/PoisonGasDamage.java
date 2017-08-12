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

import Reika.DragonAPI.Instantiable.CustomStringDamageSource;

public class PoisonGasDamage extends CustomStringDamageSource {

	public PoisonGasDamage() {
		super("would have fared better with a gas mask");
		this.setDamageBypassesArmor();
		this.setDamageIsAbsolute();
	}

	@Override
	public boolean isDifficultyScaled() {
		return false;
	}

	@Override
	public boolean isMagicDamage() {
		return false;
	}

	@Override
	public boolean isFireDamage() {
		return false;
	}

	@Override
	public boolean isExplosion() {
		return false;
	}

	@Override
	public boolean isProjectile() {
		return false;
	}

	@Override
	public float getHungerDamage() {
		return 0.1F;
	}

}
