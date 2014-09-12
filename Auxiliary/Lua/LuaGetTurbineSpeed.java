/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineMeter;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetTurbineSpeed extends LuaMethod {

	public LuaGetTurbineSpeed() {
		super("getTurbineSpeed", TileEntityTurbineMeter.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((TileEntityTurbineMeter)te).getAnalogValue()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the speed of the turbine.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
