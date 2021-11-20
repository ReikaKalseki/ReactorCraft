package Reika.ReactorCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.ReactorStructureBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;


public class GeneratorStructure extends ReactorStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.GENERATORMULTI.getBlockInstance();

		int l = TileEntityReactorGenerator.getGeneratorLength()-1;

		int dx = 0;
		int dz = 0;

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int i = 0; i < l; i++) {
			int seekmeta = i < 2 ? 3 : 1;
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				array.setBlock(ddx, dy, ddz, b, seekmeta);
				array.setBlock(ddx2, dy, ddz2, b, seekmeta);
				array.setBlock(dx, dy, dz, b, seekmeta);
			}
		}

		for (int i = 0; i < l; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			int seekmeta = 2;
			for (int k = -2; k <= 2; k += 4) {
				int dy = y+k;
				if (i == 1 && k == 2)
					seekmeta = 3;
				array.setBlock(ddx, dy, ddz, b, 2);
				array.setBlock(ddx2, dy, ddz2, b, 2);
				array.setBlock(dx, dy, dz, b, seekmeta);
			}

			ddx = dx+left.offsetX*2;
			ddx2 = dx-left.offsetX*2;
			ddz = dz+left.offsetZ*2;
			ddz2 = dz-left.offsetZ*2;

			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				array.setBlock(ddx, dy, ddz, b, 2);
				array.setBlock(ddx2, dy, ddz2, b, 2);
			}
		}

		dx = x+dir.offsetX*l;
		dz = z+dir.offsetZ*l;
		for (int k = -2; k <= 2; k++) {
			int dy = y+k;
			for (int m = -2; m <= 2; m++) {
				if ((Math.abs(k) != 2 || Math.abs(m) != 2) && (k != 0 || m != 0)) {
					int ddx = dx+left.offsetX*m;
					int ddz = dz+left.offsetZ*m;
					array.setBlock(ddx, dy, ddz, b, 2);
				}
			}
		}
		for (int i = 0; i < 2; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;

			int ddx = dx+left.offsetX*2;
			int ddz = dz+left.offsetZ*2;
			int ddx2 = dx-left.offsetX*2;
			int ddz2 = dz-left.offsetZ*2;
			array.setBlock(ddx, y+2, ddz, b, 2);
			array.setBlock(ddx2, y+2, ddz2, b, 2);

			array.setBlock(ddx, y-2, ddz, b, 2);
			array.setBlock(ddx2, y-2, ddz2, b, 2);
		}

		for (int i = 0; i < l; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			array.setBlock(dx, y, dz, b, 0);
		}

		dx = x+dir.offsetX*l;
		dz = z+dir.offsetZ*l;
		array.setBlock(dx, y, dz, ReactorTiles.GENERATOR.getBlock(), ReactorTiles.GENERATOR.getBlockMetadata());

		return array;
	}

}
