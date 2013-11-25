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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWaterCell extends TileEntityReactorBase implements ReactorCoreTE, Temperatured {

	private LiquidStates internalLiquid;
	private double storedEnergy;

	public TileEntityWaterCell() {
		this.setLiquidState(LiquidStates.EMPTY);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();
		int id = world.getBlockId(x, y-1, z);
		int metadata = world.getBlockMetadata(x, y-1, z);
		if (id == this.getTileEntityBlockID() && metadata == ReactorTiles.COOLANT.getBlockMetadata()) {
			TileEntityWaterCell te = (TileEntityWaterCell)world.getBlockTileEntity(x, y-1, z);
			if (te.getLiquidState() == LiquidStates.EMPTY && this.getLiquidState() != LiquidStates.EMPTY) {
				te.setLiquidState(this.getLiquidState());
				this.setLiquidState(LiquidStates.EMPTY);
			}
		}

		if (thermalTicker.checkCap() && !world.isRemote) {
			this.updateTemperature(world, x, y, z);
		}

		if (this.getLiquidState() == LiquidStates.EMPTY) {
			TileEntity te = world.getBlockTileEntity(x, y+1, z);
			if (te instanceof IFluidHandler) {
				IFluidHandler ic = (IFluidHandler)te;
				FluidStack liq = ic.drain(ForgeDirection.DOWN, FluidContainerRegistry.BUCKET_VOLUME, false);
				if (liq != null && liq.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
					ic.drain(ForgeDirection.DOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
					if (liq.getFluid().equals(FluidRegistry.WATER)) {
						this.setLiquidState(LiquidStates.WATER);
					}
					else if (liq.getFluid().equals(ReactorCraft.D2O)) {
						this.setLiquidState(LiquidStates.HEAVY);
					}
				}
			}
		}
	}

	private void transferEnergy(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity te = world.getBlockTileEntity(x, y-1, z);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.COOLANT.getBlockMetadata()) {
			TileEntityWaterCell wc = (TileEntityWaterCell)te;
			wc.storedEnergy += storedEnergy;
			storedEnergy = 0;
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	public double getEnergy() {
		return storedEnergy;
	}

	protected double removeEnergy() {
		double E = storedEnergy;
		storedEnergy = 0;
		return E;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.COOLANT.ordinal();
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {/*
		if (ReikaMathLibrary.doWithChance(this.getChanceToStop())) {
			temperature += ReikaThermoHelper.getTemperatureIncrease(ReikaThermoHelper.WATER_HEAT, 1000, ReikaNuclearHelper.getUraniumFissionNeutronE());
			storedEnergy += ReikaNuclearHelper.getUraniumFissionNeutronE(); //3.8kJ per neutron (kinetic energy)
			return true;
		}*/
		return false;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	public int getChanceToStop() {
		if (internalLiquid == null)
			return 0;
		if (internalLiquid == LiquidStates.HEAVY) {
			return 75;
		}
		if (internalLiquid == LiquidStates.WATER) {
			return 50;
		}
		return 0;
	}

	public enum LiquidStates {
		EMPTY(),
		WATER(),
		HEAVY(),
		SODIUM();

		public static final LiquidStates[] list = values();

		public boolean isWater() {
			return this == WATER || this == HEAVY;
		}
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return this.getLiquidState().ordinal();
	}

	public LiquidStates getLiquidState() {
		return internalLiquid;
	}

	public void setLiquidState(LiquidStates liq) {
		internalLiquid = liq;
	}

	@Override
	public int getMaxTemperature() {
		return 0;
	}

	private void onMeltdown(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		storedEnergy = NBT.getDouble("energy");

		this.setLiquidState(LiquidStates.list[NBT.getInteger("liq")]);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setDouble("energy", storedEnergy);

		NBT.setInteger("liq", this.getLiquidState().ordinal());
	}
}
