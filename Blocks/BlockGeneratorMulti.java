package Reika.ReactorCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockGeneratorMulti extends BlockMultiBlock {

	public BlockGeneratorMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberTextures() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCreateFullMultiBlock(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumberVariants() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String getIconBaseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemTextureIndex(int meta, int side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		// TODO Auto-generated method stub
		return false;
	}

}
