/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import java.util.HashMap;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Recipe.FlexibleIngredient;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Interfaces.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySynthesizer extends TileEntityInventoriedReactorBase implements IFluidHandler, ThermalMachine, PipeConnector {

	private static final int WATER_PER_AMMONIA = 250;
	private static final int AMMONIA_PER_STEP = 1000;
	public static final int AMMONIATEMP = 220;
	private static final int MAXTEMP = 1000;

	private static final HashMap<Fluid, FluidSynthesis> fluidMap = new HashMap();

	public int timer;
	private FluidSynthesis recipe;

	private final HybridTank tank = new HybridTank("synthout", 24000);
	private final HybridTank water = new HybridTank("synthwater", 24000);

	private StepTimer steptimer = new StepTimer(1800);
	private StepTimer tempTimer = new StepTimer(20);

	public static enum FluidSynthesis {
		AMMONIA(FluidRegistry.WATER, ReactorCraft.NH3, WATER_PER_AMMONIA, AMMONIA_PER_STEP, AMMONIATEMP, 50, 0, constructItemMatch("dustQuicklime", ReactorStacks.lime, 1), constructItemMatch("dustAmmonium", ReactorStacks.ammonium, 1)),
		HOTLIFBE(ReactorCraft.LIFBe_fuel, ReactorCraft.LIFBe_fuel_preheat, 50, 50, 350, 100, 5);

		public final Fluid input;
		public final Fluid output;
		public final int fluidConsumed;
		public final int fluidProduced;
		public final int minTemp;
		public final int baseDuration;
		private final int temperatureSpeedCurve;
		private final FlexibleIngredient itemA;
		private final FlexibleIngredient itemB;

		private FluidSynthesis(Fluid in, Fluid out, int amtin, int amtout, int temp, int time, int tc) {
			this(in, out, amtin, amtout, temp, time, tc, null);
		}

		private FluidSynthesis(Fluid in, Fluid out, int amtin, int amtout, int temp, int time, int tc, FlexibleIngredient is) {
			this(in, out, amtin, amtout, temp, time, tc, is, null);
		}

		private FluidSynthesis(Fluid in, Fluid out, int amtin, int amtout, int temp, int time, int tc, FlexibleIngredient a, FlexibleIngredient b) {
			input = in;
			output = out;
			fluidConsumed = amtin;
			fluidProduced = amtout;
			minTemp = temp;
			baseDuration = time;
			temperatureSpeedCurve = tc;
			itemA = a;
			itemB = b;
			if (fluidMap.containsKey(input))
				throw new IllegalArgumentException("Fluid "+input.getName()+" already mapped to a recipe!");
			fluidMap.put(input, this);
		}

		@SideOnly(Side.CLIENT)
		public ItemStack getAForDisplay() {
			return itemA != null ? itemA.getItemForDisplay(true) : null;
		}

		@SideOnly(Side.CLIENT)
		public ItemStack getBForDisplay() {
			return itemB != null ? itemB.getItemForDisplay(true) : null;
		}

		public boolean usesItem(ItemStack item) {
			return (itemA != null && itemA.match(item)) || (itemB != null && itemB.match(item));
		}

		private static FlexibleIngredient constructItemMatch(ItemStack is) {
			return new FlexibleIngredient(is, 100, is.stackSize);
		}

		private static FlexibleIngredient constructItemMatch(String ore, int amt) {
			return constructItemMatch(ore, null, amt);
		}

		private static FlexibleIngredient constructItemMatch(String ore, ItemStack is, int amt) {
			FlexibleIngredient ret = new FlexibleIngredient(ore, 100, amt);
			if (is != null)
				ret.addItem(is);
			return ret;
		}

		public int getDuration(int temperature) {
			return Math.max(5, baseDuration-temperatureSpeedCurve*(temperature-minTemp)/100);
		}
	}

	public static void addRecipe(String name, Fluid in, Fluid out, int amtin, int amtout, int temp, int time, int curve, FlexibleIngredient a, FlexibleIngredient b) {
		Class[] types = new Class[]{Fluid.class, Fluid.class, int.class, int.class, int.class, int.class, int.class, FlexibleIngredient.class, FlexibleIngredient.class};
		Object[] args = new Object[]{in, out, amtin, amtout, temp, time, curve, a, b};
		FluidSynthesis c = EnumHelper.addEnum(FluidSynthesis.class, name.toUpperCase(), types, args);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.SYNTHESIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getWaterBuckets();
		recipe = this.getRecipe();
		if (recipe != null)
			steptimer.setCap(recipe.getDuration(temperature));
		if (recipe != null && water.getLevel() >= recipe.fluidConsumed && temperature >= recipe.minTemp && tank.canTakeIn(recipe.output, recipe.fluidProduced)) {
			steptimer.update();
			if (steptimer.checkCap())
				this.make();
		}
		else
			steptimer.reset();
		timer = steptimer.getTick();
		//ReikaJavaLibrary.pConsole(tank);
		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, x, y, z, meta);
		}
	}

	private FluidSynthesis getRecipe() {
		if (water.isEmpty())
			return null;
		FluidSynthesis fr = fluidMap.get(water.getActualFluid());
		if (fr == null)
			return null;
		if (!ReikaItemHelper.matchStacks(inv[1], fr.itemA)) //handles null
			return null;
		if (!ReikaItemHelper.matchStacks(inv[2], fr.itemB))
			return null;
		return fr;
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
			ForgeDirection side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.snow);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Blocks.air, 0);
			side = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.ice);
			if (side != null)
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Blocks.flowing_water, 0);
		}
	}

	private void make() {
		if (recipe.itemA != null)
			ReikaInventoryHelper.decrStack(1, inv);
		if (recipe.itemB != null)
			ReikaInventoryHelper.decrStack(2, inv);
		water.removeLiquid(recipe.fluidConsumed);
		tank.addLiquid(recipe.fluidProduced, recipe.output);
	}

	public int getWaterScaled(int px) {
		return water.getLevel()*px/water.getCapacity();
	}

	public int getAmmoniaScaled(int px) {
		return tank.getLevel() * px / tank.getCapacity();
	}

	public int getTimerScaled(int px) {
		return steptimer.getTick() * px / steptimer.getCap();
	}

	private void getWaterBuckets() {
		if (inv[0] != null && inv[0].stackSize == 1 && inv[0].getItem() == Items.water_bucket && water.canTakeIn(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME)) {
			water.addLiquid(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.WATER);
			inv[0] = new ItemStack(Items.bucket);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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
		return true;//fluid.equals(FluidRegistry.getFluid("rc ammonia"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluidMap.get(fluid) != null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{water.getInfo(), tank.getInfo()};
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return itemstack.getItem() == Items.bucket;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (i == 0)
			return is.getItem() == Items.water_bucket;
		for (FluidSynthesis rec : FluidSynthesis.values()) {
			if (rec.itemA != null && rec.itemA.match(is))
				return i == 1;
			else if (rec.itemB != null && rec.itemB.match(is))
				return i == 2;
		}
		return false;
	}

	public String getInputFluid() {
		return water.isEmpty() ? null : water.getActualFluid().getLocalizedName();
	}

	public String getOutputFluid() {
		return tank.isEmpty() ? null : tank.getActualFluid().getLocalizedName();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		timer = NBT.getInteger("time");

		water.readFromNBT(NBT);
		tank.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("time", timer);

		water.writeToNBT(NBT);
		tank.writeToNBT(NBT);
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
	public void addTemperature(int T) {
		temperature += T;
	}

	@Override
	public int getMaxTemperature() {
		return MAXTEMP;
	}

	@Override
	public void onOverheat(World world, int x, int y, int z) {

	}

	@Override
	public boolean canBeFrictionHeated() {
		return true;
	}

	public boolean addWater(int amt) {
		if (water.canTakeIn(amt)) {
			water.addLiquid(amt, FluidRegistry.WATER);
			return true;
		}
		return false;
	}

	@Override
	public boolean canItemEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canItemExitToSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe() || m == MachineRegistry.FUELLINE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry m, ForgeDirection side) {
		return this.canConnectToPipe(m);
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side.offsetY == 0 ? Flow.INPUT : Flow.OUTPUT;
	}

	@Override
	public float getMultiplier() {
		return 1;
	}

	@Override
	public void resetAmbientTemperatureTimer() {
		tempTimer.reset();
	}

}
