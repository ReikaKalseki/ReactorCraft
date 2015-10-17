/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
@Strippable(value = {"buildcraft.api.transport.IPipeConnection"})
public abstract class TileEntityTankedReactorMachine extends TileEntityReactorBase implements IFluidHandler, PipeConnector, IPipeConnection {

	protected final HybridTank tank = new HybridTank(ReikaStringParser.stripSpaces(this.getTEName().toLowerCase()), this.getCapacity());

	public abstract int getCapacity();

	public abstract boolean canReceiveFrom(ForgeDirection from);

	public abstract Fluid getInputFluid();

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		//ReikaJavaLibrary.pConsole(tank, Side.SERVER);
		return new FluidTankInfo[]{tank.getInfo()};
	}

	public int getLevel() {
		return tank.getLevel();
	}

	public Fluid getContainedFluid() {
		return tank.getActualFluid();
	}

	public void addLiquid(int amt) {
		tank.addLiquid(amt, this.getInputFluid());
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid f) {
		return this.canReceiveFrom(from) && this.isValidFluid(f);
	}

	public boolean isValidFluid(Fluid f) {
		return f.equals(this.getInputFluid());
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return tank.fill(resource, doFill);
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canReceiveFrom(side) && this.canConnectToPipe(p);
	}

	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection side) {
		return type == PipeType.FLUID ? (this.canReceiveFrom(side) ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT) : ConnectOverride.DEFAULT;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return this.canReceiveFrom(side) ? Flow.INPUT : Flow.NONE;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
	}

}
