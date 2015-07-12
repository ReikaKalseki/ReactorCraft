/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.RotaryCraft.API.Interfaces.Laserable;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityFusionHeater extends TileEntityReactorBase implements TemperatureTE, Laserable, IFluidHandler, PipeConnector, MultiBlockTile {

	public static final int PLASMA_TEMP = 150000000;

	private int temperature;

	private boolean hasMultiBlock = false;

	private HybridTank tank = new HybridTank("fusionheater", 8000);
	private HybridTank h2 = new HybridTank("fusionheaterh2", 4000);
	private HybridTank h3 = new HybridTank("fusionheaterh3", 4000);

	public boolean hasMultiBlock() {
		return hasMultiBlock && !ReikaWorldHelper.isExposedToAir(worldObj, xCoord, yCoord, zCoord);
	}

	public void setHasMultiBlock(boolean has) {
		hasMultiBlock = has && !ReikaWorldHelper.isExposedToAir(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void whenInBeam(World world, int x, int y, int z, long power, int range) {
		if (this.hasMultiBlock())
			temperature += 640*ReikaMathLibrary.logbase(power, 2);
	}

	public boolean blockBeam(World world, int x, int y, int z, long power) {
		return this.hasMultiBlock();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.updateTemperature(world, x, y, z, meta);

		if (DragonAPICore.debugtest) {
			temperature = 200000000;
		}

		//ReikaJavaLibrary.pConsole(temperature+": "+((float)temperature/PLASMA_TEMP), Side.SERVER);
		//ReikaJavaLibrary.pConsole(h2, Side.SERVER);

		if (this.canMake())
			this.make();
	}

	private boolean canMake() {
		return this.hasMultiBlock() && temperature >= PLASMA_TEMP && !h2.isEmpty() && !h3.isEmpty() && tank.canTakeIn(100);
	}

	private void make() {
		ReactorAchievements.PLASMA.triggerAchievement(this.getPlacer());
		h2.removeLiquid(50);
		h3.removeLiquid(50);
		tank.addLiquid(100, FluidRegistry.getFluid("rc fusion plasma"));
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		int dT = temperature-Tamb;
		if (dT != 0)
			temperature -= (1+dT/16384D);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.HEATER.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("temp", temperature);

		tank.writeToNBT(NBT);
		h2.writeToNBT(NBT);
		h3.writeToNBT(NBT);

		NBT.setBoolean("multi", hasMultiBlock);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		temperature = NBT.getInteger("temp");

		tank.readFromNBT(NBT);
		h2.readFromNBT(NBT);
		h3.readFromNBT(NBT);

		hasMultiBlock = NBT.getBoolean("multi");
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc deuterium")))
			return h2.fill(resource, doFill);
		if (resource.getFluid().equals(FluidRegistry.getFluid("rc tritium")))
			return h3.fill(resource, doFill);
		return 0;
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
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.isHydrogen(fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && this.getAdjacentTileEntity(from) instanceof TileEntityMagneticPipe;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{h2.getInfo(), h3.getInfo(), tank.getInfo()};
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return p == MachineRegistry.PIPE && side != ForgeDirection.UP;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == ForgeDirection.UP ? Flow.OUTPUT : Flow.INPUT;
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return 0;
	}

	@Override
	public boolean canBeCooledWithFins() {
		return false;
	}

	public void setTemperature(int temp) {

	}

	@Override
	public int getMaxTemperature() {
		return Integer.MAX_VALUE;
	}

}
