/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

public enum ReactorType {

	FISSION(),
	BREEDER(),
	HTGR(),
	THORIUM(),
	FUSION(),
	SOLAR();

	public double getHPTurbineMultiplier() {
		switch(this) {
			//case BREEDER:
			//	return 0.8F;
			case FUSION:
				return 1.5F;
			case HTGR:
				return 0.25F;
			case SOLAR:
				return 0.4F;
			default:
				return 1;
		}
	}
}
