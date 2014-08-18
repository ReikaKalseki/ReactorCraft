/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public enum WorkingFluid {

	EMPTY(0, 0, ""),
	WATER(1F, 100, "water"),
	AMMONIA(2, -33, "rc ammonia");

	public final float efficiency;
	public final int boilingTemp;
	private final String fluidName;

	public static final WorkingFluid[] list = values();

	private WorkingFluid(float e, int boil, String f) {
		efficiency = e;
		boilingTemp = boil;
		fluidName = f;
	}

	public Fluid getFluid() {
		return FluidRegistry.getFluid(fluidName);
	}

	public Fluid getLowPressureFluid() {
		return FluidRegistry.getFluid("lowp"+this.name().toLowerCase());
	}

	public static WorkingFluid getFromNBT(NBTTagCompound NBT) {
		int val = NBT.getInteger("workingfluid");
		if (val >= 0 && val < list.length)
			return list[val];
		return EMPTY;
	}

	public void saveToNBT(NBTTagCompound NBT) {
		NBT.setInteger("workingfluid", this.ordinal());
	}

	public static boolean isWorkingFluid(Fluid f) {
		for (int i = 0; i < list.length; i++) {
			Fluid fl = list[i].getFluid();
			if (f.equals(fl))
				return true;
		}
		return false;
	}

}
