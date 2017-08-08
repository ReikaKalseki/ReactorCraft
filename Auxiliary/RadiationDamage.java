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

public class RadiationDamage extends CustomStringDamageSource {

	public RadiationDamage() {
		super("died of radiation poisoning");
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
		return 200;
	}

}
