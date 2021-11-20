package Reika.ReactorCraft.Base;

import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Base.StructureBase;


public abstract class ReactorStructureBase extends StructureBase {

	public ForgeDirection dir;

	@Override
	protected void initDisplayData() {
		dir = ForgeDirection.EAST;
	}

	@Override
	protected void finishDisplayCall() {
		dir = null;
	}

}
