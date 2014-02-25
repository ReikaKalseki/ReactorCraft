/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeTile.PipeType;

public abstract class TileEntityIntermediateBoiler extends TileEntityNuclearBoiler {

	protected StepTimer timer = new StepTimer(20);

	protected HybridTank output = new HybridTank(this.getName().toLowerCase()+"out", this.getCapacity());

	public abstract int getLiquidUsage();

	public abstract int getMinimumTemperature();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		timer.update();

		if (timer.checkCap()) {
			if (this.canHeat())
				this.heat();
		}
		//ReikaJavaLibrary.pConsole(temperature);
		//ReikaJavaLibrary.pConsole(output, !output.isEmpty());
		//ReikaJavaLibrary.pConsole(tank, Side.SERVER);

		this.transferFluid(world, x, y, z);
	}

	private void transferFluid(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y+1, z);
		if (r == this.getMachine()) {
			TileEntityIntermediateBoiler te = (TileEntityIntermediateBoiler)world.getBlockTileEntity(x, y+1, z);
			if (!te.tank.isFull() && !tank.isEmpty()) {
				int amt = Math.min(100, te.tank.getCapacity()-te.tank.getLevel());
				te.tank.addLiquid(amt, tank.getActualFluid());
				tank.removeLiquid(amt);
			}

			if (!te.output.isFull() && !output.isEmpty()) {
				int amt = Math.min(100, te.output.getCapacity()-te.output.getLevel());
				te.output.addLiquid(amt, output.getActualFluid());
				output.removeLiquid(amt);
			}
		}
	}

	protected void heat() {
		int amt = this.getLiquidUsage();
		double c = this.getFluidHeatCapacity();
		temperature -= amt*c;
		tank.removeLiquid(amt);
		output.addLiquid(amt, this.getOutputFluid());
	}

	protected abstract Fluid getOutputFluid();

	protected abstract double getFluidHeatCapacity();

	public boolean canHeat() {
		return temperature >= this.getMinimumTemperature() && tank.getLevel() >= this.getLiquidUsage() && !output.isFull() && tank.getActualFluid().equals(this.getInputFluid());
	}

	@Override
	public final boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public final void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		tank.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		tank.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	@Override
	public final FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return this.drain(from, resource.amount, doDrain);
	}

	@Override
	public final FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public final boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP;
	}

	@Override
	public final ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (with == ForgeDirection.UP)
			return ConnectOverride.CONNECT;
		return super.overridePipeConnection(type, with);
	}

	@Override
	public final Flow getFlowForSide(ForgeDirection side) {
		if (side == ForgeDirection.UP)
			return Flow.OUTPUT;
		return super.getFlowForSide(side);
	}

	@Override
	public final boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return side.offsetY != 0 && this.canConnectToPipe(p);
	}

}
