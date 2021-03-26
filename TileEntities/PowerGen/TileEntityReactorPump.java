/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.ReactorCraft.Base.TankedReactorPowerReceiver;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Piping.TileEntityPipe;

import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityReactorPump extends TankedReactorPowerReceiver {

	public static final long MINPOWER = 16384;
	public static final int MINTORQUE = 1024;

	private final HybridTank output = new HybridTank("pumpout", this.getCapacity());

	@Override
	public int getIndex() {
		return ReactorTiles.PUMP.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.canConvert())
			this.convertFluids();
		if (!output.isEmpty())
			this.dumpFluids(world, x, y, z);
		//ReikaJavaLibrary.pConsole(tank+":"+output);
	}

	private boolean canConvert() {
		if (!this.sufficientPower())
			return false;
		if (tank.isEmpty())
			return false;
		if (output.isEmpty())
			return true;
		if (output.isFull())
			return false;
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("rc lowpwater")))
			return output.getActualFluid().equals(FluidRegistry.WATER);
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("rc lowpammonia")))
			return output.getActualFluid().equals(FluidRegistry.getFluid("rc ammonia"));
		return false;
	}

	private void dumpFluids(World world, int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof TileEntityPipe) {
				TileEntityPipe p = (TileEntityPipe)te;
				if (p.canIntakeFluid(output.getActualFluid())) {
					int dL = output.getLevel()-p.getFluidLevel();
					//ReikaJavaLibrary.pConsole(dL);
					if (dL/4 > 0) {
						p.addFluid(dL/4);
						p.setFluid(output.getActualFluid());
						output.removeLiquid(dL/4);
					}
				}
			}
			else if (te instanceof IFluidHandler) {
				IFluidHandler fl = (IFluidHandler)te;
				if (fl.canFill(dir.getOpposite(), output.getActualFluid())) {
					int amt = fl.fill(dir.getOpposite(), output.getFluid(), true);
					if (amt > 0)
						output.removeLiquid(amt);
				}
			}
		}
	}

	private void convertFluids() {
		int amt = Math.min(tank.getLevel(), output.getRemainingSpace());
		if (amt <= 0)
			return;
		if (tank.getActualFluid().equals(FluidRegistry.getFluid("rc lowpwater"))) {
			output.addLiquid(amt, FluidRegistry.WATER);
		}
		else if (tank.getActualFluid().equals(FluidRegistry.getFluid("rc lowpammonia"))) {
			output.addLiquid(amt, FluidRegistry.getFluid("rc ammonia"));
		}
		tank.removeLiquid(amt);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		super.animateWithTick(world, x, y, z);
		if (this.getPower() > 0) {
			phi += 15F;
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		output.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		output.writeToNBT(NBT);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? output.drain(resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.canDrain(from, null) ? output.drain(maxDrain, doDrain) : null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0 && ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return side != ForgeDirection.DOWN && this.canConnectToPipe(p);
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return from == ForgeDirection.UP;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("rc lowpwater")))
			return true;
		if (f.equals(FluidRegistry.getFluid("rc lowpammonia")))
			return true;
		return false;
	}

	@Override
	public boolean canReadFrom(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection side) {
		return type == PipeType.FLUID ? (side != ForgeDirection.DOWN ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT) : ConnectOverride.DEFAULT;
	}

	@Override
	public int getMinTorque(int available) {
		return MINTORQUE;
	}

	@Override
	public int getMinTorque() {
		return MINTORQUE;
	}

	@Override
	public int getMinSpeed() {
		return 1;
	}

	@Override
	public long getMinPower() {
		return MINPOWER;
	}
}
