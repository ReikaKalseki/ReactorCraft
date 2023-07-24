/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.ReactorCraft.Auxiliary.SteamTile;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntitySteamDiffuser extends TileEntityTankedReactorMachine implements SteamTile {

	public static final int RATIO = calculateConversionRatio();

	private int steam;

	//613K at 14MPa to 373K at 0.1MPa
	private static int calculateConversionRatio() { //Moran & Shapiro
		double nuReact = 0.01272;
		double nuSATP = 1.696;
		double efficiency = 0.6;
		return (int)Math.ceil(nuSATP/nuReact*efficiency); //80
	}

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
			case 0:
				return ForgeDirection.WEST;
			case 1:
				return ForgeDirection.EAST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.SOUTH;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.DIFFUSER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getSteam(world, x, y, z);

		this.convertSteam();
	}

	private void convertSteam() {
		if (steam > 0) {
			Fluid f = FluidRegistry.getFluid("steam");
			if (f != null) {
				int amt = Math.min(1+steam/4, tank.getRemainingSpace()/RATIO);
				tank.addLiquid(amt*RATIO*1000, f);
				steam -= amt;
			}
		}
	}

	private void getSteam(World world, int x, int y, int z) {
		//for (int i = 0; i < 6; i++) {
		ForgeDirection dir = this.getFacing();//dirs[i];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		ReactorTiles rt = ReactorTiles.getTE(world, dx, dy, dz);
		if (rt == ReactorTiles.STEAMLINE) {
			TileEntitySteamLine te = (TileEntitySteamLine)world.getTileEntity(dx, dy, dz);
			int ds = te.getSteam()-steam;
			if (ds > 0) {
				int rm = ds/4+1;
				steam += rm*te.getWorkingFluid().efficiency;
				te.removeSteam(rm);
			}
		}
		//}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		steam = NBT.getInteger("energy");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("energy", steam);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.canDrain(from, null) ? tank.drain(maxDrain, doDrain) : null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == this.getFacing().getOpposite() && ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side == this.getFacing().getOpposite();
	}

	@Override
	public int getCapacity() {
		return 2500000;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return false;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == this.getFacing().getOpposite() ? Flow.OUTPUT : Flow.NONE;
	}

	@Override
	public int getSteam() {
		return steam;
	}

}
