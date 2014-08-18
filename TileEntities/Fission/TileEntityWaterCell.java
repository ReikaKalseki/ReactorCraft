/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Storage.TileEntityReservoir;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityWaterCell extends TileEntityReactorBase implements ReactorCoreTE, Temperatured {

	private LiquidStates internalLiquid;

	public TileEntityWaterCell() {
		this.setLiquidState(LiquidStates.EMPTY);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();
		if (ReactorTiles.getTE(world, x, y-1, z) == this.getMachine()) {
			TileEntityWaterCell te = (TileEntityWaterCell)world.getTileEntity(x, y-1, z);
			if (te.getLiquidState() == LiquidStates.EMPTY && this.getLiquidState() != LiquidStates.EMPTY) {
				te.setLiquidState(this.getLiquidState());
				this.setLiquidState(LiquidStates.EMPTY);
			}
		}
		MachineRegistry m = MachineRegistry.getMachine(world, x, y+1, z);
		if (m == MachineRegistry.RESERVOIR) {
			TileEntityReservoir te = (TileEntityReservoir)this.getAdjacentTileEntity(ForgeDirection.UP);
			if (te.getLevel() >= 1000) {
				Fluid f = te.getFluid();
				if (this.canIntakeFluid(f)) {
					te.removeLiquid(1000);
					LiquidStates lq = LiquidStates.getState(f);
					this.setLiquidState(lq);
				}
			}
		}

		if (thermalTicker.checkCap() && !world.isRemote) {
			this.updateTemperature(world, x, y, z);
		}

		if (this.getLiquidState() == LiquidStates.EMPTY) {
			TileEntity te = world.getTileEntity(x, y+1, z);
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

		if (this.getLiquidState() == LiquidStates.HEAVY)
			ReactorAchievements.CANDU.triggerAchievement(this.getPlacer());
	}

	private boolean canIntakeFluid(Fluid f) {
		return f != null && LiquidStates.getState(f) != null && internalLiquid == LiquidStates.EMPTY;
	}

	@Override
	protected void updateTemperature(World world, int x, int y, int z) {
		super.updateTemperature(world, x, y, z);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		int dT = temperature-Tamb;
		if (dT > 0) {
			temperature -= dT/8;
		}
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof Temperatured) {
				Temperatured tr = (Temperatured)te;
				if (tr.canDumpHeatInto(internalLiquid)) {
					int t = tr.getTemperature();
					int dt = t-this.getTemperature();
					if (dt > 0) {
						temperature += dt/2;
						tr.setTemperature(t-dt/2);
						if (rand.nextInt(5) == 0)
							this.setLiquidState(LiquidStates.EMPTY);
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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

	@Override
	public int getTextureState(ForgeDirection side) {
		return this.getLiquidState().ordinal();
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
		EMPTY(null),
		WATER(FluidRegistry.WATER),
		HEAVY(FluidRegistry.getFluid("heavy water")),
		SODIUM(FluidRegistry.getFluid("rc sodium"));

		public static final LiquidStates[] list = values();

		private static final HashMap<Fluid, LiquidStates> map = new HashMap();

		private final Fluid fluid;

		private LiquidStates(Fluid f) {
			fluid = f;
		}

		public boolean isWater() {
			return this == WATER || this == HEAVY;
		}

		public static LiquidStates getState(Fluid f) {
			return map.get(f);
		}

		static {
			for (int i = 1; i < list.length; i++) {
				LiquidStates lq = list[i];
				map.put(lq.fluid, lq);
			}
		}
	}

	public LiquidStates getLiquidState() {
		return internalLiquid;
	}

	public void setLiquidState(LiquidStates liq) {
		internalLiquid = liq;
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public int getMaxTemperature() {
		return 1000;
	}

	private void onMeltdown(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		this.setLiquidState(LiquidStates.list[NBT.getInteger("liq")]);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("liq", this.getLiquidState().ordinal());
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq != LiquidStates.EMPTY && (this.getLiquidState().isWater() == liq.isWater());
	}
}
