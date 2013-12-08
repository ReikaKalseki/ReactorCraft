package Reika.ReactorCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ReactorCraft.TileEntities.TileEntityGasDuct;
import Reika.RotaryCraft.RotaryCraft;

public class BlockDuct extends BlockReactorTileModelled {

	public BlockDuct(int ID, Material mat) {
		super(ID, mat);
		this.setHardness(0F);
		this.setResistance(1F);
		this.setLightValue(0F);
	}

	@Override
	public final int getRenderType() {
		return RotaryCraft.proxy.pipeRender;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityGasDuct te = (TileEntityGasDuct)world.getBlockTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntityGasDuct te = (TileEntityGasDuct)world.getBlockTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}
}
