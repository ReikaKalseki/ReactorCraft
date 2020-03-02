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

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Property;

import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.BoundedConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.ReactorCraft.ReactorCraft;

public enum ReactorOptions implements IntegerConfig, BooleanConfig, BoundedConfig, DecimalConfig, UserSpecificConfig {

	VISIBLENEUTRONS("Visible Neutrons", true),
	SILVERORE("Generate Silver Ore Even If Other Mods Do", true),
	MAGNETORE("Generate Magnetite Ore Even If Other Mods Do", true),
	CALCITEORE("Generate Calcite Ore Even If Other Mods Do", true),
	CADMIUMORE("Generate Cadmium Ore Even If Other Mods Do", true),
	INDIUMORE("Generate Indium Ore Even If Other Mods Do", true),
	RETROGEN("Retrogen Ores", false),
	RAINBOW("Rainbow Fluorite", false),
	TOROIDCHARGE("Toroid Spark Delay", 4),
	CHUNKLOADING("Fission Cores Chunkload When Active", true),
	OREDENSITY("Ore Density Percentage", 100),
	DISCRETE("Ore Discretization", 1),
	DYECRAFT("Allow Fluorite Recoloring", false),
	FASTNEUTRONS("Enable Fast and Thermal Neutrons", false),
	VERTNEUTRONS("Fission Neutrons Can Move Vertically", false),
	LODESTONERFMULT("Lodestone-Redstone RF Generation Multiplier", 1F);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;

	public static final ReactorOptions[] optionList = ReactorOptions.values();

	private ReactorOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private ReactorOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private ReactorOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)ReactorCraft.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)ReactorCraft.config.getControl(this.ordinal());
	}

	public float getFloat() {
		return (Float)ReactorCraft.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	public static int getToroidChargeRate() {
		int base = TOROIDCHARGE.getValue();
		return Math.max(Math.min(base, 20), 4);
	}

	public static float getOreMultiplier() {
		return MathHelper.clamp_float(OREDENSITY.getValue()/100F, 0.5F, 2F);
	}

	@Override
	public boolean isValueValid(Property p) {
		switch(this) {
			case DISCRETE:
				return p.getInt() > 0 && p.getInt() <= 100;
			default:
				return true;
		}
	}

	@Override
	public String getBoundsAsString() {
		switch(this) {
			case DISCRETE:
				return "(1-100)";
			default:
				return "";
		}
	}

	@Override
	public boolean isUserSpecific() {
		switch(this) {
			case VISIBLENEUTRONS:
				return true;
			default:
				return false;
		}
	}

}
