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
import java.util.HashSet;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorOres;

public class ReactorConfig extends ControlledConfig {

	private DataElement<Integer> potionID;

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ReactorAchievements.class);
	private static final ArrayList<String> oreEntries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ReactorOres.class);

	public DataElement<Integer>[] achievementIDs = new DataElement[entries.size()];
	private DataElement<Boolean>[] ores = new DataElement[entries.size()];
	private DataElement<int[]> heavyWaterDimensions;

	private HashSet<Integer> heavyWaterDimensionSet;

	public ReactorConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		potionID = this.registerAdditionalOption("Other", "Radiation Effect ID", 140);

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			achievementIDs[i] = this.registerAdditionalOption("Achievement IDs", name, 72000+i);
		}

		for (int i = 0; i < oreEntries.size(); i++) {
			String name = oreEntries.get(i);
			ores[i] = this.registerAdditionalOption("Ore Control", name, true);
		}

		heavyWaterDimensions = this.registerAdditionalOption("Other Options", "Heavy Water Dimensions (Empty for All)", new int[0]);
	}

	public boolean isDimensionValidForHeavyWater(int dim) {
		if (heavyWaterDimensionSet == null) {
			heavyWaterDimensionSet = new HashSet();
			for (int val : heavyWaterDimensions.getData()) {
				heavyWaterDimensionSet.add(val);
			}
		}
		return heavyWaterDimensionSet.isEmpty() || heavyWaterDimensionSet.contains(dim);
	}

	public int getRadiationPotionID() {
		return potionID.getData();
	}

	public boolean isOreGenEnabled(ReactorOres ore) {
		return ores[ore.ordinal()].getData();
	}
}
