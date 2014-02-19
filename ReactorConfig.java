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

import java.util.ArrayList;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Registry.ReactorAchievements;

public class ReactorConfig extends ControlledConfig {

	private int potionID;

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ReactorAchievements.class);
	public int[] achievementIDs = new int[entries.size()]; //

	public ReactorConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] blocks, IDRegistry[] items, IDRegistry[] id, int cfg) {
		super(mod, option, blocks, items, id, cfg);
	}

	@Override
	protected void loadAdditionalData() {
		potionID = config.get("Other", "Radiation Effect ID", 31).getInt();

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			achievementIDs[i] = config.get("Achievement IDs", name, 72000+i).getInt();
		}
	}

	public int getRadiationPotionID() {
		return potionID;
	}

}
