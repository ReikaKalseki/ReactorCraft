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
import Reika.ReactorCraft.Auxiliary.Temperatured;

public class LuaReactorGetTemperature extends LuaMethod {

	public LuaReactorGetTemperature() {
		super("getTemperature", Temperatured.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		return new Object[]{((Temperatured)te).getTemperature()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the machine temperature.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

}
