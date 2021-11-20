package Reika.ReactorCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.ReactorCraft.Base.ReactorStructureBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;


public class PreheaterStructure extends ReactorStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.HEATERMULTI.getBlockInstance();

		for (int i = 0; i < 5; i++) {
			for (int k = 0; k < 5; k++) {
				for (int h = 0; h < 5; h++) {
					boolean corner = (i == 0 || i == 4) && (k == 0 || k == 4) && (h == 0 || h == 4);
					boolean edge = i == 0 || i == 4 || k == 0 || k == 4;
					int m = corner ? 2 : edge ? 3 : 4;
					array.setBlock(x+i, y+h, z+k, b, m);
					if (h > 0 && !edge) {
						array.setBlock(x+i, y+h, z+k, b, 1);
						if (i == 2 && k == 2 && h >= 2) {
							ReactorTiles r = h > 2 ? ReactorTiles.MAGNETPIPE : ReactorTiles.HEATER;
							array.setBlock(x+i, y+h, z+k, r.getBlock(), r.getBlockMetadata());
						}
					}
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				boolean corner = (i == 0 || i == 2) && (k == 0 || k == 2);
				boolean edge = i == 0 || i == 2 || k == 0 || k == 2;
				int m = corner ? 2 : edge ? 3 : 4;
				array.setBlock(x+1+i, y+5, z+1+k, b, m);
			}
		}

		for (int i = 1; i <= 3; i++) {
			for (int k = 1; k <= 3; k++) {
				array.setBlock(x+i, y+k, z+0, b, 4);
				array.setBlock(x+i, y+k, z+4, b, 4);
				array.setBlock(x+0, y+k, z+i, b, 4);
				array.setBlock(x+4, y+k, z+i, b, 4);
			}
		}

		array.setBlock(x+2, y+5, z+2, ReactorTiles.MAGNETPIPE.getBlock(), ReactorTiles.MAGNETPIPE.getBlockMetadata());
		array.setBlock(x+2, y+6, z+2, ReactorTiles.MAGNETPIPE.getBlock(), ReactorTiles.MAGNETPIPE.getBlockMetadata());

		for (int i = 0; i < 5; i++) {
			if (i != 2)
				array.setBlock(x+i, y+2, z+2, MachineRegistry.PIPE.getBlock(), MachineRegistry.PIPE.getBlockMetadata());
		}

		array.setBlock(x+2, y+2, z+3, b, 0);
		array.setBlock(x+2, y+2, z+4, b, 0);

		return array;
	}

}
