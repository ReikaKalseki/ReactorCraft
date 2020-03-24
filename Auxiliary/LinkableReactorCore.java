/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;


public interface LinkableReactorCore extends TypedReactorCoreTE, BreakAction {

	public void link(TileEntityCPU te);

}
