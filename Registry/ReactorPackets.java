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

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public enum ReactorPackets {

	CPU(0, 2);

	private int min;
	private int max;

	private ReactorPackets(int l, int h)
	{
		min = l;
		max = h;
	}

	private ReactorPackets(int id)
	{
		min = id;
		max = id;
	}

	public int getMinValue() {
		return min;
	}

	public int getMaxValue() {
		return max;
	}

	public boolean isLongPacket() {
		return false;
	}

	public int getNumberDataInts() {
		if (this == CPU)
			return 0;
		return 1;
	}

	public boolean hasOneID() {
		return (max == min);
	}

	public static ReactorPackets getEnum(int index) {
		for (ReactorPackets e : ReactorPackets.values()) {
			if (ReikaMathLibrary.isValueInsideBoundsIncl(e.getMinValue(), e.getMaxValue(), index))
				return e;
		}
		ReikaJavaLibrary.pConsole("Index "+index+" does not correspond to an existing packet classification!");
		return null;
	}

}