/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.ReactorCraft.Auxiliary.TypedReactorCoreTE;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

import buildcraft.api.transport.IPipeTile.PipeType;

public abstract class TileEntityNuclearBoiler extends TileEntityTankedReactorMachine implements TypedReactorCoreTE {

	protected int steam;
	protected Proportionality<ReactorType> type = new Proportionality();

	protected TileEntityNuclearBoiler() {
		this.setReactorType(this.getDefaultReactorType(), 1);
	}

	public abstract ReactorType getDefaultReactorType();

	public final void setReactorType(ReactorType t, double amt) {
		type.addValue(t, amt);
	}

	public final void setReactorTypes(Proportionality<ReactorType> p) {
		for (ReactorType r : p.getElements()) {
			this.setReactorType(r, p.getValue(r));
		}
	}

	public final ReactorType getReactorType() {
		return type.getLargestCategory();
	}

	public final Collection<ReactorType> getReactorTypeSet() {
		return type.getElements();
	}

	public final double getReactorTypeFraction(ReactorType r) {
		return type.getFraction(r);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();

		if (thermalTicker.checkCap() && !world.isRemote) {
			this.updateTemperature(world, x, y, z);
		}

		this.balanceFluid(world, x, y, z);
	}

	@Override
	protected final void updateTemperature(World world, int x, int y, int z) {
		super.updateTemperature(world, x, y, z);

		if (temperature > this.getMaxTemperature())
			this.overheat(world, x, y, z);
	}

	protected abstract void overheat(World world, int x, int y, int z);

	@Override
	public final int getTemperature() {
		return temperature;
	}

	@Override
	public final void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public final boolean canDumpHeatInto(LiquidStates liq) {
		return false;
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
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public final boolean canReceiveFrom(ForgeDirection from) {
		return from == ForgeDirection.DOWN;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

	protected void balanceFluid(World world, int x, int y, int z) {
		for (int i = 0; i < 2; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.TEList[this.getIndex()]) {
				TileEntityNuclearBoiler te = (TileEntityNuclearBoiler)world.getTileEntity(dx, dy, dz);
				if (te.tank.getLevel() < tank.getLevel() && (te.tank.isEmpty() || te.tank.getActualFluid() == tank.getActualFluid())) {
					int dl = tank.getLevel()-te.tank.getLevel();
					te.tank.addLiquid(dl/4+1, tank.getActualFluid());
					tank.removeLiquid(dl/4+1);
				}
			}
		}
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return type == PipeType.FLUID && with == ForgeDirection.DOWN ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
	}

	public final void addLiquid(int amt, Fluid fluid) {
		tank.addLiquid(amt, fluid);
	}

	@Override
	public final int getTextureState(ForgeDirection side) {
		if (side.offsetY != 0)
			return 0;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		ReactorTiles src = this.getMachine();
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		ReactorTiles r2 = ReactorTiles.getTE(world, x, y+1, z);
		if (r2 == src && r == src)
			return 2;
		else if (r2 == src)
			return 1;
		else if (r == src)
			return 3;
		return 0;
	}

	public final int removeSteam() {
		int s = steam;
		steam = 0;
		return s;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		type.readFromNBT(NBT.getCompoundTag("types"), (NBTIO<ReactorType>)ReikaNBTHelper.getEnumConverter(ReactorType.class));
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound tag = new NBTTagCompound();
		type.writeToNBT(tag, (NBTIO<ReactorType>)ReikaNBTHelper.getEnumConverter(ReactorType.class));
		NBT.setTag("types", tag);
	}
}
