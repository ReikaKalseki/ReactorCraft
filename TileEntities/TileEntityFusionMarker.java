/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import java.util.ArrayList;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet.Aim;

public class TileEntityFusionMarker extends TileEntityReactorBase {

	@Override
	public int getIndex() {
		return ReactorTiles.MARKER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean renderLines() {
		return this.hasRedstoneSignal();
	}

	public ArrayList<Aim> getAimPoints() {
		ArrayList li = new ArrayList();
		li.add(Aim.N);
		li.add(Aim.N);
		li.add(Aim.NNW1);
		li.add(Aim.NNW2);
		li.add(Aim.NNW3);
		li.add(Aim.NW);
		li.add(Aim.WNW1);
		li.add(Aim.WNW2);
		li.add(Aim.WNW3);
		li.add(Aim.W);
		li.add(Aim.W);
		li.add(Aim.W);
		li.add(Aim.WSW1);
		li.add(Aim.WSW2);
		li.add(Aim.WSW3);
		li.add(Aim.SW);
		li.add(Aim.SSW1);
		li.add(Aim.SSW2);
		li.add(Aim.SSW3);
		li.add(Aim.S);
		li.add(Aim.S);
		li.add(Aim.S);
		li.add(Aim.SSE1);
		li.add(Aim.SSE2);
		li.add(Aim.SSE3);
		li.add(Aim.SE);
		li.add(Aim.ESE1);
		li.add(Aim.ESE2);
		li.add(Aim.ESE3);
		li.add(Aim.E);
		li.add(Aim.E);
		li.add(Aim.E);
		li.add(Aim.ENE1);
		li.add(Aim.ENE2);
		li.add(Aim.ENE3);
		li.add(Aim.NE);
		li.add(Aim.NNE1);
		li.add(Aim.NNE2);
		li.add(Aim.NNE3);
		li.add(Aim.N);
		return li;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

}
