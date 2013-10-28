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

import net.minecraft.util.StatCollector;

public enum MatBlocks {

	CONCRETE("block.concrete"),
	SLAG("block.slag");

	private String name;

	public static final MatBlocks[] matList = values();

	private MatBlocks(String n) {
		name = n;
	}

	public String getName() {
		return StatCollector.translateToLocal(name);
	}

	public boolean isMultiSidedTexture() {
		return false;
	}

}
