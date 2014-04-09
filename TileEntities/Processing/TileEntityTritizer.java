/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityTritizer extends TileEntityReactorBase implements ReactorCoreTE, PipeConnector, IFluidHandler {

	public static final int CAPACITY = 1000;

	private HybridTank input = new HybridTank("tritizerin", CAPACITY);
	private HybridTank output = new HybridTank("tritizerout", CAPACITY);

	@Override
	public int getIndex() {
		return ReactorTiles.TRITIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		//input.addLiquid(100, ReactorCraft.H2);
		//this.onNeutron(null, world, x, y, z);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		input.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		input.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (this.canMake() && ReikaRandomHelper.doWithChance(75)) {
			this.make();
			return true;
		}
		return false;
	}

	private void make() {
		int amt = 25;
		input.removeLiquid(amt);
		output.addLiquid(amt, FluidRegistry.getFluid("rc tritium"));
	}

	private boolean canMake() {
		return !input.isEmpty() && input.getLevel() >= 5 && !output.isFull() && input.getActualFluid().equals(FluidRegistry.getFluid("rc deuterium"));
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!this.canDrain(from, resource.getFluid()))
			return null;
		return output.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && fluid.equals(FluidRegistry.getFluid("rc deuterium"));
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.DOWN;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{input.getInfo(), output.getInfo()};
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side.offsetY != 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return input.fill(resource, doFill);
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		if (side == ForgeDirection.UP)
			return Flow.INPUT;
		if (side == ForgeDirection.DOWN)
			return Flow.OUTPUT;
		return Flow.NONE;
	}

}
