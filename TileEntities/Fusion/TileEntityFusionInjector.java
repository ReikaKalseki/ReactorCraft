/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

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
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityFusionInjector extends TileEntityReactorBase implements IFluidHandler, PipeConnector {

	public static final int PLASMA_PER_FUSION = 25;

	private HybridTank tank = new HybridTank("injector", 8000);

	private ForgeDirection facing;

	public boolean hasMultiBlock;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.canMake())
			this.make(world, x, y, z);
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
	}

	private boolean canMake() {
		if (!hasMultiBlock || tank.isEmpty())
			return false;
		return true;
	}

	private void make(World world, int x, int y, int z) {
		this.createPlasma(world, x, y, z);
		tank.removeLiquid(1);
	}

	private void createPlasma(World world, int x, int y, int z) {
		EntityPlasma e = new EntityPlasma(world, x+0.5, y+0.5, z+0.5);
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

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource.getFluid().equals(FluidRegistry.getFluid("fusion plasma")))
			return tank.fill(resource, doFill);
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
		return fluid.equals(FluidRegistry.getFluid("fusion plasma"));
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getIndex() {
		return ReactorTiles.INJECTOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		tank.writeToNBT(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setBoolean("multi", hasMultiBlock);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		tank.readFromNBT(NBT);

		facing = dirs[NBT.getInteger("face")];

		hasMultiBlock = NBT.getBoolean("multi");
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return side == this.getFacing() ? 0 : side.offsetY != 0 ? 2 : 2;
	}

}
