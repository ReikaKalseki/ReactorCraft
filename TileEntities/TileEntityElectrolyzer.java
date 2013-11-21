package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityElectrolyzer extends TileEntityReactorBase {

	public static final int SODIUM_MELT = 98;

	@Override
	public int getIndex() {
		return ReactorTiles.ELECTROLYZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

}
