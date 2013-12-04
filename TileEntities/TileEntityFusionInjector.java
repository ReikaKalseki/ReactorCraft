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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.PipeConnector;
import Reika.RotaryCraft.Auxiliary.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

//has two tanks, one for 2h and one for 3h, heats, and creates plasma entities
// fire into ring CERN-style, set target in a dir with length 1, overshoots
public class TileEntityFusionInjector extends TileEntityReactorBase implements IFluidHandler, PipeConnector, ShaftPowerReceiver, TemperatureTE {

	public static final int CAPACITY = 2000;

	public static final int HYDROGEN_PER_FUSION = 25;

	public static final int MINPOWER = 524288;
	public static final int MINSPEED = 2048;

	public static final int PLASMATEMP = 150000000;

	private HybridTank deuterium = new HybridTank("inject2h", CAPACITY);
	private HybridTank tritium = new HybridTank("inject3h", CAPACITY);

	private int omega;
	private int torque;
	private long power;
	private int iotick;

	private int temperature;

	private ForgeDirection facing;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	private boolean canMake() {
		if (tritium.getLevel() < HYDROGEN_PER_FUSION || deuterium.getLevel() < HYDROGEN_PER_FUSION)
			return false;
		if (power < MINPOWER || omega < MINSPEED)
			return false;
		if (temperature < PLASMATEMP)
			return false;
		return true;
	}

	private void make(World world, int x, int y, int z) {
		this.createPlasma(world, x, y, z);
	}

	private void createPlasma(World world, int x, int y, int z) {
		EntityPlasma e = new EntityPlasma(world);
		e.setPosition(x+0.5, y+0.5, z+0.5);
		e.setTarget(x+this.getFacing().offsetX, z+this.getFacing().offsetZ);
		if (!world.isRemote)
			world.spawnEntityInWorld(e);
	}

	public int[] getTarget() {
		int dx = xCoord+this.getFacing().offsetX;
		int dz = zCoord+this.getFacing().offsetZ;
		return new int[]{dx, yCoord, dz};
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
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
		int dx = xCoord+this.getFacing().offsetX;
		int dz = zCoord+this.getFacing().offsetZ;
		return x == dx && y == yCoord && z == dz;
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
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return true;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return Flow.INPUT;
	}

	private boolean isHydrogen(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("rc deuterium")))
			return true;
		if (f.equals(FluidRegistry.getFluid("rc tritium")))
			return true;
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc deuterium")))
			return deuterium.fill(resource, doFill);
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc tritium")))
			return tritium.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int amount, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.isHydrogen(fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{deuterium.getInfo(), tritium.getInfo()};
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

		deuterium.writeToNBT(NBT);
		tritium.writeToNBT(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		omega = NBT.getInteger("om");
		torque = NBT.getInteger("tq");
		temperature = NBT.getInteger("temp");
		iotick = NBT.getInteger("io");

		deuterium.readFromNBT(NBT);
		tritium.readFromNBT(NBT);

		facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];
	}

}
