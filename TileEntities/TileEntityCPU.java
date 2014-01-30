/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityCPU extends TileEntityReactorBase {

	private final ReactorControlLayout layout = new ReactorControlLayout(this);
	private final BlockArray reactor = new BlockArray();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (reactor.isEmpty()) {
			int r = 8;
			reactor.recursiveAddWithBounds(world, x+1, y, z, ReactorBlocks.REACTOR.getBlockID(), x-r, y, z-r, x+r, y, z+r);
			reactor.recursiveAddWithBounds(world, x-1, y, z, ReactorBlocks.REACTOR.getBlockID(), x-r, y, z-r, x+r, y, z+r);
			reactor.recursiveAddWithBounds(world, x, y, z+1, ReactorBlocks.REACTOR.getBlockID(), x-r, y, z-r, x+r, y, z+r);
			reactor.recursiveAddWithBounds(world, x, y, z-1, ReactorBlocks.REACTOR.getBlockID(), x-r, y, z-r, x+r, y, z+r);
			for (int i = 0; i < reactor.getSize(); i++) {
				int[] xyz = reactor.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				int id2 = world.getBlockId(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				if (id2 == ReactorTiles.CONTROL.getBlockID() && meta2 == ReactorTiles.CONTROL.getBlockMetadata()) {
					TileEntityControlRod rod = (TileEntityControlRod)world.getBlockTileEntity(dx, dy, dz);
					layout.addControlRod(rod);
				}
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.CPU.ordinal();
	}

	public ReactorControlLayout getLayout() {
		return layout;
	}

}
