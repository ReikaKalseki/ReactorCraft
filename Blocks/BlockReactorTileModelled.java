package Reika.ReactorCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockReactorTileModelled extends BlockReactorTile {

	public BlockReactorTileModelled(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return 0;
	}

}
