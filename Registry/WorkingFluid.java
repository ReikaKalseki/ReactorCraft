/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
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

	EMPTY(0, 0),
	WATER(0.5F, 100),
	AMMONIA(1, -33);

	public final float efficiency	;
	public final int boilingTemp;

	public static final WorkingFluid[] list = values();

	private WorkingFluid(float e, int boil) {
		efficiency = e;
		boilingTemp = boil;
	}

	public Fluid getFluid() {
		return FluidRegistry.getFluid(this.name().toLowerCase());
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
