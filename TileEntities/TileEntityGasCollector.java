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

import net.minecraft.block.Block;
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

public class TileEntityGasCollector extends TileEntityReactorBase implements IFluidHandler {

	private HybridTank tank = new HybridTank("co2collector", 1000);

	private int readx;
	private int ready;
	private int readz;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getIOSides(world, x, y, z, meta);
		int id = world.getBlockId(readx, ready, readz);
		if (id == Block.furnaceBurning.blockID) {
			tank.addLiquid(10, FluidRegistry.getFluid("rc co2"));
		}
		//ReikaJavaLibrary.pConsole(id+":"+tank, Side.SERVER);
	}

	private void getIOSides(World world, int x, int y, int z, int meta) {
		switch(meta) {
		case 0:
			readx = x+1;
			readz = z;
			ready = y;
			break;
		case 1:
			readx = x-1;
			readz = z;
			ready = y;
			break;
		case 2:
			readz = z-1;
			readx = x;
			ready = y;
			break;
		case 3:
			readz = z+1;
			readx = x;
			ready = y;
			break;
		case 4:
			readx = x;
			readz = z;
			ready = y-1;
			break;
		case 5:
			readx = x;
			readz = z;
			ready = y+1;
			break;
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getIndex() {
		return ReactorTiles.COLLECTOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		if (NBT.hasKey("internalFluid")) {
			tank.setFluid(new FluidStack(NBT.getInteger("liquidId"), NBT.getInteger("internalFluid")));
		}
		else if (NBT.hasKey("tank")) {
			tank.setFluid(FluidStack.loadFluidStackFromNBT(NBT.getCompoundTag("tank")));
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		if (tank.getFluid() != null) {
			NBT.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
		}
	}

}
