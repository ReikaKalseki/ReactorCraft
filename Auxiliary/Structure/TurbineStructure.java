package Reika.ReactorCraft.Auxiliary.Structure;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.ReactorStructureBase;
import Reika.ReactorCraft.Blocks.Multi.BlockTurbineMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;


public class TurbineStructure extends ReactorStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);

		array.fillFrom(((BlockTurbineMulti)ReactorBlocks.TURBINEMULTI.getBlockInstance()).getBlueprint(), x, y, z, dir);

		for (int i = 0; i <= 8; i++) {
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i+left.offsetZ*5;
			ReactorTiles r = i >= 7 ? ReactorTiles.STEAMLINE : ReactorTiles.BIGTURBINE;
			array.setBlock(dx, y+5, dz, r.getBlock(), r.getBlockMetadata());
		}

		return array;
	}

}
