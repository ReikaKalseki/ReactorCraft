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
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityCondenser extends TileEntityTankedReactorMachine {

	private int steam;

	@Override
	public int getIndex() {
		return ReactorTiles.CONDENSER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();
		//this.getSteam(world, x, y, z);
		if (world.getBlockId(x, y-1, z) == ReactorBlocks.STEAM.getBlockID()) {
			world.setBlock(x, y-1, z, 0);
			steam++;
		}

		if (thermalTicker.checkCap() && !world.isRemote) {
			if (temperature < 100 && steam > 0 && !tank.isFull()) {
				steam--;
				tank.addLiquid(TileEntityReactorBoiler.WATER_PER_STEAM, FluidRegistry.WATER);
			}
		}

		this.balance(world, x, y, z);
	}

	private void balance(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles rt = ReactorTiles.getTE(world, dx, dy, dz);
			if (rt == ReactorTiles.CONDENSER) {
				TileEntityCondenser te = (TileEntityCondenser)world.getBlockTileEntity(dx, dy, dz);
				if (te.steam > steam+1) {
					steam++;
					te.steam--;
				}
				int dL = te.tank.getLevel() - tank.getLevel();
				if (dL > 0) {
					tank.addLiquid(dL/4, FluidRegistry.WATER);
					te.tank.removeLiquid(dL/4);
				}
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int maxDrain = resource.amount;
		if (resource.getFluid().equals(FluidRegistry.WATER))
			return tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
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
		return false;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");
		tank.readFromNBT(NBT);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);
		tank.writeToNBT(NBT);
	}

}
