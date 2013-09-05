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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.Feedable;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityULine extends TileEntityReactorBase implements Feedable {

	@Override
	public int getIndex() {
		return ReactorTiles.ITEMLINE.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean feed() {
		return false;
	}

	@Override
	public boolean feedIn(ItemStack is) {
		return false;
	}

	@Override
	public ItemStack feedOut() {
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

}
