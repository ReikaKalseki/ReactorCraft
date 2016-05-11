/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

public interface MultiBlockTile extends BreakAction {

	public boolean hasMultiBlock();

	public void setHasMultiBlock(boolean has);

}
