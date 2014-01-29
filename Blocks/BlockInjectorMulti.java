package Reika.ReactorCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockInjectorMulti extends BlockMultiBlock {

	public BlockInjectorMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 9;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	protected String getIconBaseName() {
		return "injector";
	}

	@Override
	protected int getTextureOrdinalForSide(int meta, ForgeDirection side) {
		switch(meta) {
		case 0:
			return 0;
		case 1:
			return side == ForgeDirection.UP ? 1 : 0;
		default:
			return 0;
		}
	}

	@Override
	protected int getTextureMetaForMeta(int meta) {
		return meta;
	}

}
