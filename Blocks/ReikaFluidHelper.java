/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.List;

import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaFluidHelper extends DragonAPICore {

	/** Returns false positives! */
	public static boolean areFluidTankInfosEqual(FluidTankInfo ifo1, FluidTankInfo ifo2) {
		if (ifo1 == ifo2)
			return true;
		if (ifo1 == null || ifo2 == null)
			return false;
		if (ifo1.capacity != ifo2.capacity)
			return false;
		if (ifo1.fluid == ifo2.fluid)
			return true;
		if (ifo1.fluid == null || ifo2.fluid == null)
			return false;
		if (ifo1.fluid.amount != ifo2.fluid.amount)
			return false;
		return ifo1.fluid.equals(ifo2.fluid);
	}

	public static boolean listContainsFluidTankInfo(List<FluidTankInfo> li, FluidTankInfo ifo) {
		for (int i = 0; i < li.size(); i++) {
			FluidTankInfo ifo2 = li.get(i);
			if (areFluidTankInfosEqual(ifo, ifo2))
				return true;
		}
		return false;
	}
}
