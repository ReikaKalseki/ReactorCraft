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
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityCPU extends TileEntityReactorBase implements ShaftPowerReceiver {

	private final ReactorControlLayout layout = new ReactorControlLayout(this);
	private final BlockArray reactor = new BlockArray();

	private int omega;
	private int torque;
	private long power;

	public static final int MINPOWER = 16384;
	public static final int SCRAMPOWER = 65536;

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

	public boolean canSCRAM() {
		return power >= SCRAMPOWER;
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

}
