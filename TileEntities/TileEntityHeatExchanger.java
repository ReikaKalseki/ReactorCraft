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
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityHeatExchanger extends TileEntityTankedReactorMachine implements TemperatureTE, ShaftPowerReceiver {

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
	public int getIndex() {
		return ReactorTiles.EXCHANGER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.canCool())
			this.cool();
		temp.update();
		if (temp.checkCap()) {
			this.distributeHeat(world, x, y, z);
			this.updateTemperature(world, x, y, z, meta);
		}
	}

	private void distributeHeat(World world, int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.BOILER) {
				TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(dx, dy, dz);
				int dT = temperature - te.getTemperature();
				if (dT > 0) {
					temperature -= dT/4;
					te.setTemperature(te.getTemperature()+dT/4);
				}
			}
		}
	}

	private void cool() {
		tank.removeLiquid(COOL_AMOUNT);
		Exchange e = this.getExchange();
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

	private boolean canCool() {
		if (power < MINPOWER || omega < MINSPEED)
			return false;
		Exchange e = this.getExchange();
		if (e == null)
			return false;
		return temperature < e.maxTemperature && tank.getLevel() >= COOL_AMOUNT && !output.isFull() && this.canCoolFluid(tank.getActualFluid());
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
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
	enum Exchange {
		SODIUM(FluidRegistry.getFluid("hotsodium"), FluidRegistry.getFluid("sodium"), ReikaThermoHelper.SODIUM_HEAT, 600);

		public final Fluid hotFluid;
		public final Fluid coldFluid;
		public final double heatCapacity;
		public final int maxTemperature;

		public static final Exchange[] list = values();

		private Exchange(Fluid from, Fluid to, double c, int max) {
			coldFluid = to;
			hotFluid = from;
			heatCapacity = c;
			maxTemperature = max;
		}
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);

		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water);
		if (waterside != null) {
			Tamb /= 2;
		}
		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.ice.blockID);
		if (iceside != null) {
			if (Tamb > 0)
				Tamb /= 4;
			ReikaWorldHelper.changeAdjBlock(world, x, y, z, iceside, Block.waterMoving.blockID, 0);
		}
		ForgeDirection fireside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.fire.blockID);
		if (fireside != null) {
			Tamb += 200;
		}
		ForgeDirection lavaside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.lava);
		if (lavaside != null) {
			Tamb += 600;
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
			ForgeDirection side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.snow.blockID);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, 0, 0);
			side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Block.ice.blockID);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.waterMoving.blockID, 0);
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
	public int getMachineX() {
		return xCoord;
	}

	@Override
	public int getMachineY() {
		return yCoord;
	}

	@Override
	public int getMachineZ() {
		return zCoord;
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
	public boolean canReadFromBlock(int x, int y, int z) {
		return x == xCoord && z == zCoord && y == yCoord-1;
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
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		tank.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		tank.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

}
