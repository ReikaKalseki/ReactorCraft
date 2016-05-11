/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import dan200.computercraft.api.lua.LuaException;

public class LuaLowerControlRods extends LuaMethod {

	public LuaLowerControlRods() {
		super("lowerRods", TileEntityCPU.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		((TileEntityCPU)te).lowerAllRods();
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Lowers all the reactor control rods.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
