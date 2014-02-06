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
import Reika.DragonAPI.Interfaces.SemiTransparent;
import Reika.ReactorCraft.Base.BlockMultiBlock;

public class BlockHeaterMulti extends BlockMultiBlock implements SemiTransparent {

	public BlockHeaterMulti(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getNumberVariants() {
		return 5;
	}

	@Override
	public boolean checkForFullMultiBlock(World world, int x, int y, int z, ForgeDirection dir) {
		return false;
	}

	@Override
	protected String getIconBaseName() {
		return "heater";
	}

	@Override
	public boolean isOpaque(int meta) {
		return meta != 0;
	}

	@Override
	public int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta) {
		if (meta <= 1)
			return meta;
		if (meta == 4)
			return 10;
		if (meta == 2) {
			switch(side) {
			case 0:
				if (world.getBlockId(x+1, y, z+1) == blockID && world.getBlockMetadata(x+1, y, z+1) == 4)
					return 2;
				if (world.getBlockId(x-1, y, z+1) == blockID && world.getBlockMetadata(x-1, y, z+1) == 4)
					return 3;
				if (world.getBlockId(x+1, y, z-1) == blockID && world.getBlockMetadata(x+1, y, z-1) == 4)
					return 5;
				if (world.getBlockId(x-1, y, z-1) == blockID && world.getBlockMetadata(x-1, y, z-1) == 4)
					return 4;
				break;
			case 1:
				if (world.getBlockId(x+1, y, z+1) == blockID && world.getBlockMetadata(x+1, y, z+1) == 4)
					return 2;
				if (world.getBlockId(x-1, y, z+1) == blockID && world.getBlockMetadata(x-1, y, z+1) == 4)
					return 3;
				if (world.getBlockId(x+1, y, z-1) == blockID && world.getBlockMetadata(x+1, y, z-1) == 4)
					return 5;
				if (world.getBlockId(x-1, y, z-1) == blockID && world.getBlockMetadata(x-1, y, z-1) == 4)
					return 4;
				break;
			case 2:
				if (world.getBlockId(x+1, y+1, z) == blockID && world.getBlockMetadata(x+1, y+1, z) == 4)
					return 4;
				if (world.getBlockId(x-1, y+1, z) == blockID && world.getBlockMetadata(x-1, y+1, z) == 4)
					return 5;
				if (world.getBlockId(x+1, y-1, z) == blockID && world.getBlockMetadata(x+1, y-1, z) == 4)
					return 3;
				if (world.getBlockId(x-1, y-1, z) == blockID && world.getBlockMetadata(x-1, y-1, z) == 4)
					return 2;
				break;
			case 3:
				if (world.getBlockId(x+1, y+1, z) == blockID && world.getBlockMetadata(x+1, y+1, z) == 4)
					return 5;
				if (world.getBlockId(x-1, y+1, z) == blockID && world.getBlockMetadata(x-1, y+1, z) == 4)
					return 4;
				if (world.getBlockId(x+1, y-1, z) == blockID && world.getBlockMetadata(x+1, y-1, z) == 4)
					return 2;
				if (world.getBlockId(x-1, y-1, z) == blockID && world.getBlockMetadata(x-1, y-1, z) == 4)
					return 3;
				break;
			case 4:
				if (world.getBlockId(x, y+1, z+1) == blockID && world.getBlockMetadata(x, y+1, z+1) == 4)
					return 5;
				if (world.getBlockId(x, y+1, z-1) == blockID && world.getBlockMetadata(x, y+1, z-1) == 4)
					return 4;
				if (world.getBlockId(x, y-1, z+1) == blockID && world.getBlockMetadata(x, y-1, z+1) == 4)
					return 2;
				if (world.getBlockId(x, y-1, z-1) == blockID && world.getBlockMetadata(x, y-1, z-1) == 4)
					return 3;
				break;
			case 5:
				if (world.getBlockId(x, y+1, z+1) == blockID && world.getBlockMetadata(x, y+1, z+1) == 4)
					return 4;
				if (world.getBlockId(x, y+1, z-1) == blockID && world.getBlockMetadata(x, y+1, z-1) == 4)
					return 5;
				if (world.getBlockId(x, y-1, z+1) == blockID && world.getBlockMetadata(x, y-1, z+1) == 4)
					return 3;
				if (world.getBlockId(x, y-1, z-1) == blockID && world.getBlockMetadata(x, y-1, z-1) == 4)
					return 2;
				break;
			}
		}
		if (meta == 3) {
			switch(side) {
			case 0:
			case 1:
				if (world.getBlockId(x+1, y, z) == blockID && world.getBlockMetadata(x+1, y, z) == 4)
					return 8;
				if (world.getBlockId(x-1, y, z) == blockID && world.getBlockMetadata(x-1, y, z) == 4)
					return 7;
				if (world.getBlockId(x, y, z+1) == blockID && world.getBlockMetadata(x, y, z+1) == 4)
					return 9;
				if (world.getBlockId(x, y, z-1) == blockID && world.getBlockMetadata(x, y, z-1) == 4)
					return 6;
				break;
			case 2:
				if (world.getBlockId(x+1, y, z) == blockID && world.getBlockMetadata(x+1, y, z) == 4)
					return 7;
				if (world.getBlockId(x-1, y, z) == blockID && world.getBlockMetadata(x-1, y, z) == 4)
					return 8;
				if (world.getBlockId(x, y+1, z) == blockID && world.getBlockMetadata(x, y+1, z) == 4)
					return 6;
				if (world.getBlockId(x, y-1, z) == blockID && world.getBlockMetadata(x, y-1, z) == 4)
					return 9;
				break;
			case 3:
				if (world.getBlockId(x+1, y, z) == blockID && world.getBlockMetadata(x+1, y, z) == 4)
					return 8;
				if (world.getBlockId(x-1, y, z) == blockID && world.getBlockMetadata(x-1, y, z) == 4)
					return 7;
				if (world.getBlockId(x, y+1, z) == blockID && world.getBlockMetadata(x, y+1, z) == 4)
					return 6;
				if (world.getBlockId(x, y-1, z) == blockID && world.getBlockMetadata(x, y-1, z) == 4)
					return 9;
				break;
			case 4:
				if (world.getBlockId(x, y, z+1) == blockID && world.getBlockMetadata(x, y, z+1) == 4)
					return 8;
				if (world.getBlockId(x, y, z-1) == blockID && world.getBlockMetadata(x, y, z-1) == 4)
					return 7;
				if (world.getBlockId(x, y+1, z) == blockID && world.getBlockMetadata(x, y+1, z) == 4)
					return 6;
				if (world.getBlockId(x, y-1, z) == blockID && world.getBlockMetadata(x, y-1, z) == 4)
					return 9;
				break;
			case 5:
				if (world.getBlockId(x, y, z+1) == blockID && world.getBlockMetadata(x, y, z+1) == 4)
					return 7;
				if (world.getBlockId(x, y, z-1) == blockID && world.getBlockMetadata(x, y, z-1) == 4)
					return 8;
				if (world.getBlockId(x, y+1, z) == blockID && world.getBlockMetadata(x, y+1, z) == 4)
					return 6;
				if (world.getBlockId(x, y-1, z) == blockID && world.getBlockMetadata(x, y-1, z) == 4)
					return 9;
				break;
			}
		}
		return 10;
	}

	@Override
	public int getItemTextureIndex(int meta) {
		return meta <= 1 ? meta : 11;
	}

}
