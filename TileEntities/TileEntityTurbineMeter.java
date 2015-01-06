/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;

public class TileEntityTurbineMeter extends TileEntityReactorBase {

	private int turbineY = -1;
	private int oldlvl;
	private int lvl;

	@Override
	public int getIndex() {
		return ReactorTiles.TURBINEMETER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted() == 0 || world.getTotalWorldTime()%32 == 0) {
			this.remapTurbine(world, x, y, z);
		}

		TileEntityTurbineCore te = this.getTurbine();
		lvl = te != null ? this.getRedstoneFrom(te) : 0;

		if (this.getTicksExisted() == 0 || oldlvl != lvl) {
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		}

		oldlvl = lvl;
	}

	private void remapTurbine(World world, int x, int y, int z) {
		for (int i = y+1; i < world.provider.getHeight(); i++) {
			ReactorTiles r = ReactorTiles.getTE(world, x, i, z);
			if (r != null && r.isTurbine()) {
				turbineY = i;
				return;
			}
			else {
				Block b = world.getBlock(x, i, z);
				if (b != Blocks.air && b.getLightOpacity(world, x, i, z) > 0) {
					turbineY = -1;
					return;
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return lvl;
	}

	private int getRedstoneFrom(TileEntityTurbineCore te) {
		int max = te.getMaxSpeed();
		return 15*te.getOmega()/max;
	}

	public float getAnalogValue() {
		TileEntityTurbineCore te = this.getTurbine();
		return te != null ? (float)te.getOmega()/te.getMaxSpeed() : 0;
	}

	private TileEntityTurbineCore getTurbine() {
		ReactorTiles r = ReactorTiles.getTE(worldObj, xCoord, turbineY, zCoord);
		if (r == null || !r.isTurbine())
			return null;
		return (TileEntityTurbineCore)this.getTileEntity(xCoord, turbineY, zCoord);
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return side == ForgeDirection.UP ? 2 : side == ForgeDirection.DOWN ? 0 : 1;
	}

}
