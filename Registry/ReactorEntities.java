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

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Interfaces.Registry.EntityEnum;
import Reika.ReactorCraft.Entities.EntityFusion;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Entities.EntityRadiation;

public enum ReactorEntities implements EntityEnum {

	NEUTRON(EntityNeutron.class, "Neutron"),
	RADIATION(EntityRadiation.class, "Radiation"),
	PLASMA(EntityPlasma.class, "Plasma"),
	FUSION(EntityFusion.class, "Fusion");

	public final String entityName;
	private final Class entityClass;

	public static final ReactorEntities[] entityList = values();

	private ReactorEntities(Class<? extends Entity> c, String s) {
		entityClass = c;
		entityName = s;
	}

	@Override
	public String getBasicName() {
		return entityName;
	}

	@Override
	public boolean isDummiedOut() {
		return false;
	}

	@Override
	public Class getObjectClass() {
		return entityClass;
	}

	@Override
	public String getUnlocalizedName() {
		return entityName;
	}

	@Override
	public int getTrackingDistance() {
		return 64;
	}

	@Override
	public boolean sendsVelocityUpdates() {
		return true;
	}

	@Override
	public boolean hasSpawnEgg() {
		return false;
	}

	@Override
	public int eggColor1() {
		return 0;
	}

	@Override
	public int eggColor2() {
		return 0;
	}

}
