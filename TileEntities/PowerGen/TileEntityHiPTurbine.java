package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;

public class TileEntityHiPTurbine extends TileEntityTurbineCore {

	public static final int GEN_OMEGA = 131072;
	public static final int TORQUE_CAP = 65536;

	@Override
	public int getMaxTorque() {
		return 65536;
	}

	@Override
	public int getMaxSpeed() {
		return 131072;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.BIGTURBINE.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getSteamMovement().getOpposite();
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;

		boolean flag = false;

		if (ReactorTiles.getTE(world, dx, dy, dz) == ReactorTiles.STEAMLINE) {
			TileEntitySteamLine te = (TileEntitySteamLine)this.getAdjacentTileEntity(dir);
			int s = te.getSteam();
			if (s > 8 && te.getWorkingFluid() == WorkingFluid.WATER) {
				int rm = s/8+1;
				steam += rm;
				te.removeSteam(rm);
				flag = true;
			}
		}

		return flag;
	}

}
