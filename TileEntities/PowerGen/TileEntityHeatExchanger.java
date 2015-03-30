/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityHeatExchanger extends TileEntityTankedReactorMachine implements TemperatureTE, ReactorPowerReceiver {

	public static final int CAPACITY = 2000;

	public static final int MAXTEMP = 1500;

	public static final int COOL_AMOUNT = 100;

	public static final int MINPOWER = 8192;
	public static final int MINSPEED = 512;

	private long power;
	private int omega;
	private int torque;

	private int iotick;

	private HybridTank output = new HybridTank("exchangerout", this.getCapacity());

	private StepTimer temp = new StepTimer(20);

	@Override
	public final FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo(), output.getInfo()};
	}

	@Override
	public int getIndex() {
		return ReactorTiles.EXCHANGER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!PowerTransferHelper.checkPowerFrom(this, ForgeDirection.DOWN)) {
			this.noInputMachine();
		}

		Exchange e = this.getExchange();

		if (this.canCool(e))
			this.cool(e);
		temp.update();
		if (temp.checkCap()) {
			this.distributeHeat(world, x, y, z, e);
			this.updateTemperature(world, x, y, z, meta);
		}
	}

	private void distributeHeat(World world, int x, int y, int z, Exchange e) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.BOILER) {
				TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getTileEntity(dx, dy, dz);
				int dT = temperature - te.getTemperature();
				if (dT > 0) {
					temperature -= dT/4;
					te.setTemperature(te.getTemperature()+dT/4);
					te.setReactorType(e != null ? e.type : null);
				}
			}
		}
	}

	private void cool(Exchange e) {
		tank.removeLiquid(COOL_AMOUNT);
		output.addLiquid(COOL_AMOUNT, e.coldFluid);
		double c = e.heatCapacity;
		temperature += c*COOL_AMOUNT;
	}

	private Exchange getExchange() {
		for (int i = 0; i < Exchange.list.length; i++) {
			Exchange e = Exchange.list[i];
			Fluid in = e.hotFluid;
			if (in.equals(tank.getActualFluid()))
				return e;
		}
		return null;
	}

	private boolean canCoolFluid(Fluid f) {
		for (int i = 0; i < Exchange.list.length; i++) {
			Fluid fl = Exchange.list[i].hotFluid;
			if (fl.equals(f))
				return true;
		}
		return false;
	}

	private boolean canCool(Exchange e) {
		if (e == null)
			return false;
		if (power < MINPOWER || omega < MINSPEED)
			return false;

		return temperature < e.maxTemperature && tank.getLevel() >= COOL_AMOUNT && !output.isFull() && this.canCoolFluid(tank.getActualFluid());
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (iotick > 0)
			iotick -= 8;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return this.drain(from, resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return side != ForgeDirection.DOWN && this.canConnectToPipe(p);
	}

	@Override
	public int getCapacity() {
		return CAPACITY;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return from == ForgeDirection.UP;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		for (int i = 0; i < Exchange.list.length; i++) {
			Fluid in = Exchange.list[i].hotFluid;
			if (f.equals(in))
				return true;
		}
		return false;
	}

	//Add API to allow others to add fluids
	protected static enum Exchange {
		SODIUM("rc hotsodium", "rc sodium", ReikaThermoHelper.SODIUM_HEAT, 600, ReactorType.BREEDER),
		CO2("rc hot co2", "rc co2", ReikaThermoHelper.CO2_HEAT, TileEntityPebbleBed.MINTEMP, ReactorType.HTGR);

		public final Fluid hotFluid;
		public final Fluid coldFluid;
		public final double heatCapacity;
		public final int maxTemperature;
		public final ReactorType type;

		public static final Exchange[] list = values();

		private Exchange(String from, String to, double c, int max, ReactorType t) {
			coldFluid = FluidRegistry.getFluid(to);
			hotFluid = FluidRegistry.getFluid(from);
			heatCapacity = c;
			maxTemperature = max;
			type = t;
		}
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);

		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water);
		if (waterside != null) {
			Tamb /= 2;
		}
		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.ice);
		if (iceside != null) {
			if (Tamb > 0)
				Tamb /= 4;
			ReikaWorldHelper.changeAdjBlock(world, x, y, z, iceside, Blocks.flowing_water, 0);
		}
		ForgeDirection fireside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.fire);
		if (fireside != null) {
			Tamb += 200;
			if (temperature < 100)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, fireside, Blocks.air, 0);
			else {
				world.setBlockToAir(x, y, z);
				world.createExplosion(null, x+0.5, y+0.5, z+0.5, 6, ConfigRegistry.BLOCKDAMAGE.getState());
			}
		}
		ForgeDirection lavaside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.lava);
		if (lavaside != null) {
			Tamb += 600;
			if (temperature < 100)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, lavaside, Blocks.stone, 0);
			else {
				world.setBlockToAir(x, y, z);
				world.createExplosion(null, x+0.5, y+0.5, z+0.5, 6, ConfigRegistry.BLOCKDAMAGE.getState());
			}
		}
		if (temperature > Tamb)
			temperature--;
		if (temperature > Tamb*2)
			temperature--;
		if (temperature < Tamb)
			temperature++;
		if (temperature*2 < Tamb)
			temperature++;
		if (temperature > MAXTEMP)
			temperature = MAXTEMP;
		if (temperature > 100) {
			ForgeDirection side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.snow);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Blocks.air, 0);
			side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.ice);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Blocks.flowing_water, 0);
		}
	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return temperature/250;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {

	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		if (side == ForgeDirection.DOWN)
			return Flow.NONE;
		return this.canReceiveFrom(side) ? Flow.INPUT : Flow.OUTPUT;
	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFrom(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		torque = omega = 0;
		power = 0;
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection side) {
		return type == PipeType.FLUID && side != ForgeDirection.DOWN ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	@Override
	public int getMinTorque(int available) {
		return 16;
	}

	@Override
	public int getMinTorque() {
		return 1;
	}

	@Override
	public int getMinSpeed() {
		return MINSPEED;
	}

	@Override
	public long getMinPower() {
		return MINPOWER;
	}

	@Override
	public boolean canBeCooledWithFins() {
		return true;
	}

}
