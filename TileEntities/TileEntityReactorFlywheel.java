/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.API.Screwdriverable;
import Reika.RotaryCraft.API.ShaftPowerEmitter;

public class TileEntityReactorFlywheel extends TileEntityReactorBase implements ShaftPowerEmitter, Screwdriverable {

	private int iotick;
	private long power;
	private int omega;
	private int torque;

	private ForgeDirection facing;

	public boolean hasMultiBlock = true;

	//public static final int MAXSPEED = 8192;
	//public static final int MINTORQUE = 32768;
	public static final int MAXTORQUE = 12750;

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.FLYWHEEL.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		facing = this.setFacing(meta);
		int dx = x+this.getFacing().offsetX;
		int dy = y+this.getFacing().offsetY;
		int dz = z+this.getFacing().offsetZ;
		ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
		if (r != null && r.isTurbine()) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(dx, dy, dz);
			//if (te.getOmega() > omega && omega < MAXSPEED && te.getTorque() >= MINTORQUE) {
			//	omega++;
			//}
			//else if (omega > 0) {
			//	omega--;
			//}
			//torque = te.getTorque();
			//ReikaJavaLibrary.pConsole(torque+"/"+te.getTorque()+":"+omega+"/"+te.getOmega(), Side.SERVER);
			omega = te.getOmega();
			torque = Math.min(te.getTorque(), MAXTORQUE);
		}
		else {
			if (omega > 0)
				omega -= (omega/32)+1;
		}
		if (omega <= 0) {
			torque = 0;
			omega = 0;
		}
		power = (long)omega*(long)torque;
	}

	private ForgeDirection setFacing(int meta) {
		switch(meta) {
		case 0:
			return ForgeDirection.EAST;
		case 1:
			return ForgeDirection.WEST;
		case 2:
			return ForgeDirection.SOUTH;
		case 3:
			return ForgeDirection.NORTH;
		default:
			return null;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public long getMaxPower() {
		return Long.MAX_VALUE;
	}

	@Override
	public long getCurrentPower() {
		return power;
	}

	@Override
	public boolean canWriteTo(ForgeDirection from) {
		ForgeDirection dir = this.getFacing().getOpposite();
		return dir == from;
	}

	@Override
	public boolean isEmitting() {
		return hasMultiBlock;
	}

	@Override
	public int getEmittingX() {
		return xCoord+this.getFacing().getOpposite().offsetX;
	}

	@Override
	public int getEmittingY() {
		return yCoord+this.getFacing().getOpposite().offsetY;
	}

	@Override
	public int getEmittingZ() {
		return zCoord+this.getFacing().getOpposite().offsetZ;
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		int meta = this.getBlockMetadata();
		if (meta < 3)
			this.setBlockMetadata(meta+1);
		else
			this.setBlockMetadata(0);
		return true;
	}

}
