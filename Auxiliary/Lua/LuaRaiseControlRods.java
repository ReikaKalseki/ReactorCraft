package Reika.ReactorCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;

public class LuaRaiseControlRods extends LuaMethod {

	public LuaRaiseControlRods() {
		super("raiseRods", TileEntityCPU.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		((TileEntityCPU)te).raiseAllRods();
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Raises all the reactor control rods.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

}
