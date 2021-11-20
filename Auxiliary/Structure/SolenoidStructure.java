package Reika.ReactorCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.ReactorCraft.Base.ReactorStructureBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;


public class SolenoidStructure extends ReactorStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.SOLENOIDMULTI.getBlockInstance();

		for (int i = -1; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || j != 0 || k != 0) {
						array.setBlock(x+i, y+j, z+k, b, 5);
					}
				}
			}
		}

		for (int i = 2; i <= 7; i++) {
			array.setBlock(x+i, y, z, b, 4);
			array.setBlock(x-i, y, z, b, 4);
			array.setBlock(x, y, z+i, b, 4);
			array.setBlock(x, y, z-i, b, 4);

			if (i < 6) {
				array.setBlock(x+i, y, z+i, b, 4);
				array.setBlock(x-i, y, z+i, b, 4);
				array.setBlock(x+i, y, z-i, b, 4);
				array.setBlock(x-i, y, z-i, b, 4);
			}
		}

		array.setBlock(x-6, y+1, z-6, b, 1);
		array.setBlock(x-6, y, z-6, b, 3);
		array.setBlock(x-6, y-1, z-6, b, 1);

		array.setBlock(x+6, y+1, z-6, b, 1);
		array.setBlock(x+6, y, z-6, b, 3);
		array.setBlock(x+6, y-1, z-6, b, 1);

		array.setBlock(x-6, y+1, z+6, b, 1);
		array.setBlock(x-6, y, z+6, b, 3);
		array.setBlock(x-6, y-1, z+6, b, 1);

		array.setBlock(x+6, y+1, z+6, b, 1);
		array.setBlock(x+6, y, z+6, b, 3);
		array.setBlock(x+6, y-1, z+6, b, 1);

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 3 : 2;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y-1;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y+1;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		array.setBlock(array.getMidX(), 0, array.getMidZ(), ReactorTiles.SOLENOID.getBlock(), ReactorTiles.SOLENOID.getBlockMetadata());

		return array;
	}

}
