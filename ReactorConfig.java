/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.util.ArrayList;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Registry.ReactorAchievements;

public class ReactorConfig extends ControlledConfig {

	private DataElement<Integer> potionID;

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ReactorAchievements.class);
	public DataElement<Integer>[] achievementIDs = new DataElement[entries.size()];

	public ReactorConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		potionID = this.registerAdditionalOption("Other", "Radiation Effect ID", 140);

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			achievementIDs[i] = this.registerAdditionalOption("Achievement IDs", name, 72000+i);
		}
	}

	public int getRadiationPotionID() {
		return potionID.getData();
	}

}
