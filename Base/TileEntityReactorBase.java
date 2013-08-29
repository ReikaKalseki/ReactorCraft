/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import Reika.DragonAPI.Base.TileEntityBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;

public abstract class TileEntityReactorBase extends TileEntityBase {

	@Override
	public int getTileEntityBlockID() {
		return ReactorBlocks.TILEENTITY.getBlockID();
	}

	@Override
	protected String getTEName() {
		return "Fuel Core";
	}
}
