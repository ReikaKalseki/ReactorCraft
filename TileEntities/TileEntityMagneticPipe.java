package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.RenderableDuct;

public class TileEntityMagneticPipe extends TileEntityReactorBase implements RenderableDuct {


	//make share parent class with gas duct
	//make fusion plasma destroy every pipe but this one
	@Override
	public int getIndex() {
		return ReactorTiles.MAGNETPIPE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

}
