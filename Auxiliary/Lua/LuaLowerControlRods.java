package Reika.ReactorCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;

public class LuaLowerControlRods extends LuaMethod {

	public LuaLowerControlRods() {
		super("lowerRods", TileEntityCPU.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
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

}
