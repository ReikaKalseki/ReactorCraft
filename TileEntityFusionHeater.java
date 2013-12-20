/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;

public class TileEntityFusionHeater extends TileEntityReactorBase implements ShaftPowerReceiver, TemperatureTE {

	public static final int MINPOWER = 524288;
	public static final int MINSPEED = 2048;

	private int omega;
	private int torque;
	private long power;
	private int iotick;

	private int temperature;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

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
		for (int i = 0; i < 6; i++) {
			int dx = xCoord+dirs[i].offsetX;
			int dy = yCoord+dirs[i].offsetY;
			int dz = zCoord+dirs[i].offsetZ;
			if (x == dx && y == yCoord && z == dz)
				return true;
		}
		return false;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.INJECTOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateTemperature(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return temperature/1000;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);
		NBT.setInteger("om", omega);
		NBT.setInteger("tq", torque);
		NBT.setInteger("io", iotick);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		omega = NBT.getInteger("om");
		torque = NBT.getInteger("tq");
		temperature = NBT.getInteger("temp");
		iotick = NBT.getInteger("io");
	}

}
