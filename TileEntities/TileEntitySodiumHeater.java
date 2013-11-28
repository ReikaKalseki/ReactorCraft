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
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntitySodiumHeater extends TileEntityNuclearBoiler {

	public static final int COOL_USAGE = 100;

	private StepTimer coolTimer = new StepTimer(20);

	private HybridTank output = new HybridTank("sodiumboilerout", this.getCapacity());

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		coolTimer.update();

		if (coolTimer.checkCap()) {
			if (this.canCool())
				this.cool();
		}
		//ReikaJavaLibrary.pConsole(temperature);
		tank.empty();
	}

	private void cool() {
		int amt = COOL_USAGE;
		double c = ReikaThermoHelper.SODIUM_HEAT;
		temperature -= amt*c;
		tank.removeLiquid(amt);
		output.addLiquid(amt, FluidRegistry.getFluid("hotsodium"));
	}

	private boolean canCool() {
		return temperature > 300 && tank.getLevel() >= COOL_USAGE && !output.isFull() && tank.getActualFluid().equals(FluidRegistry.getFluid("sodium"));
	}

	@Override
	public int getMaxTemperature() {
		return 0;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.SODIUMBOILER.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");
		tank.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);

		tank.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("sodium");
	}

	@Override
	public final FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
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
	public Flow getFlowForSide(ForgeDirection side) {
		if (side == ForgeDirection.UP)
			return Flow.OUTPUT;
		return super.getFlowForSide(side);
	}

}
