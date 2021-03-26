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

import Reika.ReactorCraft.ReactorCraft;

public enum ReactorPackets {

	CPUTOGGLE(),
	CPURAISE(),
	CPULOWER(),
	ORERADIATION(),
	;

	private final int numInts;

	private static final ReactorPackets[] list = values();

	private ReactorPackets() {
		this(0);
	}

	private ReactorPackets(int ints) {
		numInts = ints;
	}

	public boolean isLongPacket() {
		return false;
	}

	public int getNumberDataInts() {
		return numInts;
	}

	public static ReactorPackets getEnum(int index) {
		if (index >= 0 && index < list.length)
			return list[index];
		ReactorCraft.logger.logError("Index "+index+" does not correspond to an existing packet classification!");
		return null;
	}

}
