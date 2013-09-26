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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerEmitter;

public class TileEntityTurbineCore extends TileEntityReactorBase implements ShaftPowerEmitter {

	private double storedEnergy;

	public static final int GEN_OMEGA = 512; //377 real
	public static final int TORQUE_CAP = 8388608;

	private int omega;
	private int iotick;

	private StepTimer accelTicker = new StepTimer(1);

	@Override
	public int getIndex() {
		return ReactorTiles.TURBINECORE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.accumulateEnergy(world, x, y, z, meta);
		this.useEnergy();
		this.updateSpeed();
	}

	private void updateSpeed() {
		accelTicker.update();
		accelTicker.setCap(this.getAccelDelay());
		if (this.getGenTorque() > 0 && storedEnergy > 0) {
			if (accelTicker.checkCap())
				omega = ReikaMathLibrary.extrema(omega+1, GEN_OMEGA, "absmin");
		}
		else {
			omega = ReikaMathLibrary.extrema(omega-1, 0, "max");
		}
	}

	private int getAccelDelay() {
		return (int)(1+ReikaMathLibrary.logbase(omega+1, 2));
	}

	private void useEnergy() {
		storedEnergy -= this.getConsumedEnergy();
	}

	private double getConsumedEnergy() {
		return storedEnergy/400;
	}

	private int getGenTorque() {
		return (int)(this.getGenPower()/GEN_OMEGA);
	}

	private long getGenPower() {
		return (long) (storedEnergy*this.getEfficiency());
	}

	private double getEfficiency() {
		return 0.2*this.getNumberStagesTotal();
	}

	public int getStage() {
		return zCoord%5;
	}

	public int getNumberStagesTotal() {
		return 5;
	}

	private void accumulateEnergy(World world, int x, int y, int z, int meta) {
		if (meta < 2) {
			int id = world.getBlockId(x+1, y, z);
			int bmeta = world.getBlockMetadata(x+1, y, z);
			if (id == ReactorTiles.WATERLINE.getBlockID() && bmeta == ReactorTiles.WATERLINE.getBlockMetadata()) {
				TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(x+1, y, z);
				storedEnergy += te.removeEnergy();
			}
			id = world.getBlockId(x-1, y, z);
			bmeta = world.getBlockMetadata(x-1, y, z);
			if (id == ReactorTiles.WATERLINE.getBlockID() && bmeta == ReactorTiles.WATERLINE.getBlockMetadata()) {
				TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(x-1, y, z);
				storedEnergy += te.removeEnergy();
			}
		}
		else {
			int id = world.getBlockId(x, y, z+1);
			int bmeta = world.getBlockMetadata(x, y, z+1);
			if (id == ReactorTiles.WATERLINE.getBlockID() && bmeta == ReactorTiles.WATERLINE.getBlockMetadata()) {
				TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(x, y, z+1);
				storedEnergy += te.removeEnergy();
			}
			id = world.getBlockId(x, y, z-1);
			bmeta = world.getBlockMetadata(x, y, z-1);
			if (id == ReactorTiles.WATERLINE.getBlockID() && bmeta == ReactorTiles.WATERLINE.getBlockMetadata()) {
				TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(x, y, z-1);
				storedEnergy += te.removeEnergy();
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		iotick -= 8;
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += 0.5F*ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	public double getEnergy() {
		return storedEnergy;
	}

	protected double removeEnergy() {
		double E = storedEnergy;
		storedEnergy = 0;
		return E;
	}

	@Override
	public int getOmega() {
		return this.isEmitting() ? omega : 0;
	}

	@Override
	public int getTorque() {
		return this.getGenTorque();
	}

	@Override
	public long getPower() {
		return this.getGenPower();
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
	public boolean canWriteToBlock(int x, int y, int z) {
		switch(this.getBlockMetadata()) {
		case 0:
			return x == xCoord-1 && y == yCoord && z == zCoord;
		case 1:
			return x == xCoord+1 && y == yCoord && z == zCoord;
		case 2:
			return x == xCoord && y == yCoord && z == zCoord-1;
		case 3:
			return x == xCoord && y == yCoord && z == zCoord+1;
		}
		return false;
	}

	@Override
	public boolean isEmitting() {
		return this.getGenPower() > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		storedEnergy = NBT.getDouble("energy");
		omega = NBT.getInteger("speed");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setDouble("energy", storedEnergy);
		NBT.setInteger("speed", omega);
	}

}
