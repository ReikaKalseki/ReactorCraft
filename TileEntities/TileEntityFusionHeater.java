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
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Laserable;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;

public class TileEntityFusionHeater extends TileEntityReactorBase implements TemperatureTE, Laserable, IFluidHandler {

	public static final int PLASMA_TEMP = 150000000;

	private int temperature;

	private HybridTank tank = new HybridTank("fusionheater", 8000);
	private HybridTank h2 = new HybridTank("fusionheaterh2", 4000);
	private HybridTank h3 = new HybridTank("fusionheaterh3", 4000);

	@Override
	public void whenInBeam(long power, int range) {
		temperature += 640*ReikaMathLibrary.logbase(power, 2);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.updateTemperature(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(temperature+": "+((float)temperature/PLASMA_TEMP), Side.SERVER);

		if (this.canMake())
			this.make();
	}

	private boolean canMake() {
		return temperature >= PLASMA_TEMP && !h2.isEmpty() && !h3.isEmpty();
	}

	private void make() {
		h2.removeLiquid(1);
		h3.removeLiquid(1);
		tank.addLiquid(2, FluidRegistry.getFluid("fusion plasma"));
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);
		int dT = temperature-Tamb;
		if (dT != 0)
			temperature -= (1+dT/16384D);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.INJECTOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	private boolean isHydrogen(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("rc deuterium")))
			return true;
		if (f.equals(FluidRegistry.getFluid("rc tritium")))
			return true;
		return false;
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
		return temperature/1000;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);

		tank.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		temperature = NBT.getInteger("temp");

		tank.readFromNBT(NBT);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (this.canFill(from, resource.getFluid()))
			return 0;
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc deuterium")))
			return h2.fill(resource, doFill);
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc tritium")))
			return h3.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, 	boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.isHydrogen(fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{h2.getInfo(), h3.getInfo(), tank.getInfo()};
	}

}
