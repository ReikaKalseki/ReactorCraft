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
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Shockable;

public class TileEntityIonizer extends TileEntityReactorBase implements Shockable, IFluidHandler {

	public static final int PLASMACHARGE = 600000;

	private int charge;

	private ForgeDirection facing;

	private HybridTank tank = new HybridTank("ionizer", 8000);

	@Override
	public int getIndex() {
		return ReactorTiles.IONIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("chg", charge);

		NBT.setInteger("face", this.getFacing().ordinal());

		tank.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		charge = NBT.getInteger("chg");

		facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];

		tank.readFromNBT(NBT);
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += charge;
	}

	@Override
	public int getMinDischarge() {
		return 16384;
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return tank.fill(resource, doFill);
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
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.isValidFluid(fluid);
	}

	private boolean isValidFluid(Fluid fluid) {
		if (fluid.equals(FluidRegistry.getFluid("rc deuterium")))
			return true;
		if (fluid.equals(FluidRegistry.getFluid("rc tritium")))
			return true;
		return false;
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
	public float getAimX() {
		return 0.5F;
	}

	@Override
	public float getAimY() {
		return 0.5F;
	}

	@Override
	public float getAimZ() {
		return 0.5F;
	}

}
