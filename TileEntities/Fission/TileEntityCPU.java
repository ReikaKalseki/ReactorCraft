/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityCPU extends TileEntityReactorBase implements ShaftPowerReceiver, Temperatured {

	private final ReactorControlLayout layout = new ReactorControlLayout(this);
	private final BlockArray reactor = new BlockArray();

	private int omega;
	private int torque;
	private long power;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.updateTemperature(world, x, y, z);
		if (reactor.isEmpty()) {
			layout.clear();
			int r = 6;
			int id = ReactorBlocks.REACTOR.getBlockID();
			int id2 = ReactorBlocks.MODELREACTOR.getBlockID();
			for (int i = 2; i < 6; i++)
				reactor.recursiveMultiAddWithBounds(world, x+dirs[i].offsetX, y, z+dirs[i].offsetZ, x-r, y, z-r, x+r, y, z+r, id, id2);
			for (int i = 0; i < reactor.getSize(); i++) {
				int[] xyz = reactor.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				int idx = world.getBlockId(dx, dy, dz);
				int metax = world.getBlockMetadata(dx, dy, dz);
				if (idx == ReactorTiles.CONTROL.getBlockID() && metax == ReactorTiles.CONTROL.getBlockMetadata()) {
					TileEntityControlRod rod = (TileEntityControlRod)world.getBlockTileEntity(dx, dy, dz);
					layout.addControlRod(rod);
				}
			}
		}

		if ((world.getTotalWorldTime()&16) == 16)
			reactor.clear();

		if (power < layout.getMinPower())
			this.SCRAM();

		if (temperature > this.getMaxTemperature() && power >= layout.getMinPower()*4) {
			this.SCRAM();
		}
	}

	public void SCRAM() {
		layout.SCRAM();
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
		return 0;
	}

	@Override
	public void setIORenderAlpha(int io) {}

	@Override
	public int getMachineX() {
		return xCoord;
	}

	@Override
	public int getMachineY() {
		return yCoord;
	}

	@Override
	public int getMachineZ() {
		return zCoord;
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		return Math.abs(x-xCoord+y-yCoord+z-zCoord) == 1; //one block away
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		torque = omega = 0;
		power = 0;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 800;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq != LiquidStates.EMPTY;
	}

}
