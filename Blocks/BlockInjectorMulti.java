/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;

public class BlockInjectorMulti extends BlockMultiBlock {

	public BlockInjectorMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 8;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		return false;
	}

	@Override
	public void onCreateFullMultiBlock(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.recursiveAddWithBounds(world, x, y, z, blockID, x-8, y-5, z-8, x+8, y+5, z+8);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (meta < 8) {
				world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], meta+8, 3);
			}
			if (meta == 0) {
				for (int k = 2; k < 6; k++) {
					ForgeDirection dir = dirs[k];
					int dx = xyz[0]+dir.offsetX;
					int dy = xyz[1]+dir.offsetY;
					int dz = xyz[2]+dir.offsetZ;
					//ReikaJavaLibrary.pConsole(world.getBlockId(dx, dy, dz)+":"+world.getBlockMetadata(dx, dy, dz)+" from "+Arrays.toString(xyz));
					if (ReactorTiles.getTE(world, dx, dy, dz) == ReactorTiles.INJECTOR) {
						TileEntityFusionInjector te = (TileEntityFusionInjector)world.getBlockTileEntity(dx, dy, dz);
						te.hasMultiBlock = true;
					}
				}
			}
		}
	}

	@Override
	protected void breakMultiBlock(World world, int x, int y, int z) {

	}

	@Override
	protected String getIconBaseName() {
		return "injector";
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		if (meta == 0)
			return 0;
		ForgeDirection dir = dirs[side];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		return id == blockID ? 0 : 9;
	}

	@Override
	public int getItemTextureIndex(int meta) {
		return meta;
	}

	@Override
	public boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta) {
		return true;
	}

}
