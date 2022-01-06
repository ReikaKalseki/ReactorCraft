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

import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;

public enum ReactorType {

	FISSION(),
	BREEDER(),
	HTGR(),
	THORIUM(),
	FUSION(),
	SOLAR(),
	NONE();

	public double getHPTurbineMultiplier() {
		switch(this) {
			//case BREEDER:
			//	return 0.8F;
			case FUSION:
				return 1.5F;
			case HTGR:
				return 0.35F;
			case SOLAR:
				return 0.15F;
			case NONE:
				return 0;
			default:
				return 1;
		}
	}

	public float getControlCPUHeatEfficiency() {
		switch(this) {
			case HTGR:
			case SOLAR:
			case FUSION:
				return this.getTypeMismatchHeatEfficiency();
			default:
				return 1;
		}
	}

	/** For conducting heat to other reactor types. */
	public float getTypeMismatchHeatEfficiency() {
		switch(this) {
			case FISSION:
				return 1;
			case HTGR:
				return 0.0625F;
			case SOLAR:
			case FUSION:
				return 0;
			case THORIUM:
				return 0.25F;
			case NONE:
				return 0;
			default:
				return 0.5F;

		}
	}

	public NeutronType getNeutronType() {
		switch(this) {
			case BREEDER:
				return NeutronType.BREEDER;
			case FISSION:
				return NeutronType.FISSION;
			case FUSION:
				return NeutronType.FUSION;
			case THORIUM:
				return NeutronType.THORIUM;
			default:
				return null;
		}
	}
}
