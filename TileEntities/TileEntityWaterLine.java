package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWaterLine extends TileEntityReactorBase {

	@Override
	public int getIndex() {
		return ReactorTiles.WATERLINE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

}
