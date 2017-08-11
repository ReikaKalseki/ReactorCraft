package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;


public interface LinkableReactorCore extends ReactorCoreTE, BreakAction {

	public void link(TileEntityCPU te);

}
