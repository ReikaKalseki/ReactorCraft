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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityReactorPump extends TileEntityTankedReactorMachine implements ShaftPowerReceiver {

	private static final long MINPOWER = 0;
	private static final int MINTORQUE = 0;

	private HybridTank output = new HybridTank("pumpout", this.getCapacity());

	private int omega;
	private int torque;
	private long power;
	private int iotick = 512;

	@Override
	public int getIndex() {
		return ReactorTiles.PUMP.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		iotick -= 8;
		if (this.canConvert())
			this.convertFluids();
		if (!output.isEmpty())
			this.dumpFluids(world, x, y, z);
		ReikaJavaLibrary.pConsole(tank+":"+output);
	}

	private boolean canConvert() {
		if (power < MINPOWER || torque < MINTORQUE)
			return false;
		if (tank.isEmpty())
			return false;
		if (output.isEmpty())
			return true;
		if (output.isFull())
			return false;
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("lowpwater")))
			return output.getActualFluid().equals(FluidRegistry.WATER);
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("lowpammonia")))
			return output.getActualFluid().equals(FluidRegistry.getFluid("ammonia"));
		return false;
	}

	private void dumpFluids(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y+1, z);
		if (id > 0) {
			TileEntity te = world.getBlockTileEntity(x, y+1, z);
			if (te instanceof IFluidHandler) {
				IFluidHandler fl = (IFluidHandler)te;
				if (fl.canFill(ForgeDirection.DOWN, output.getActualFluid())) {
					int amt = fl.fill(ForgeDirection.DOWN, output.getFluid(), true);
					output.removeLiquid(amt);
				}
			}
		}
	}

	private void convertFluids() {
		int amt = Math.min(tank.getLevel(), output.getRemainingSpace());
		if (amt <= 0)
			return;
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("lowpwater"))) {
			output.addLiquid(amt, FluidRegistry.WATER);
		}
		else if (tank.getActualFluid().equals(FluidRegistry.getFluid("lowpammonia"))) {
			output.addLiquid(amt, FluidRegistry.getFluid("ammonia"));
		}
		tank.removeLiquid(amt);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		tank.readFromNBT(NBT);
		output.readFromNBT(NBT);

		omega = NBT.getInteger("speed");
		torque = NBT.getInteger("trq");
		power = NBT.getLong("pwr");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		tank.writeToNBT(NBT);
		output.writeToNBT(NBT);

		NBT.setInteger("speed", omega);
		NBT.setInteger("trq", torque);
		NBT.setLong("pwr", power);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return false;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return from != ForgeDirection.UP;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("lowpwater")))
			return true;
		if (f.equals(FluidRegistry.getFluid("lowpammonia")))
			return true;
		return false;
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
		return false;
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
