/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Interfaces.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntitySynthesizer extends TileEntityInventoriedReactorBase implements IFluidHandler, ThermalMachine, PipeConnector {

	private static final int WATER_PER_AMMONIA = 250;
	private static final int AMMONIA_PER_STEP = 1000;
	public static final int AMMONIATEMP = 220;
	private static final int MAXTEMP = 1000;

	public int timer;

	private final HybridTank tank = new HybridTank("synthout", 24000);

	private final HybridTank water = new HybridTank("synthwater", 24000);

	private StepTimer steptimer = new StepTimer(1800);
	private StepTimer tempTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.SYNTHESIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		steptimer.setCap(50);
		this.getWaterBuckets();
		if (this.getWater() > 0 && this.hasAmmonium() && this.hasQuicklime() && this.canMakeAmmonia(AMMONIA_PER_STEP)) {
			steptimer.update();
			if (steptimer.checkCap())
				this.makeAmmonia();
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

	private boolean canMakeAmmonia(int amt) {
		return temperature >= AMMONIATEMP && (tank.isEmpty() || tank.getLevel()+amt < tank.getCapacity());
	}

	private void makeAmmonia() {
		ReikaInventoryHelper.decrStack(1, inv);
		ReikaInventoryHelper.decrStack(2, inv);
		water.removeLiquid(WATER_PER_AMMONIA);
		tank.addLiquid(AMMONIA_PER_STEP, FluidRegistry.getFluid("rc ammonia"));
	}

	private boolean hasQuicklime() {
		if (inv[1] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[1], ReactorStacks.lime))
			return true;
		ArrayList<ItemStack> lime = OreDictionary.getOres("dustQuicklime");
		return ReikaItemHelper.collectionContainsItemStack(lime, inv[1]);
	}

	private boolean hasAmmonium() {
		if (inv[2] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[2], ReactorStacks.ammonium))
			return true;
		ArrayList<ItemStack> dust = OreDictionary.getOres("dustAmmonium");
		return ReikaItemHelper.collectionContainsItemStack(dust, inv[2]);
	}

	private int getWater() {
		return water.getLevel();
	}

	public int getWaterScaled(int px) {
		return this.getWater()*px/water.getCapacity();
	}

	public int getAmmoniaScaled(int px) {
		return tank.getLevel() * px / tank.getCapacity();
	}

	public int getTimerScaled(int px) {
		return steptimer.getTick() * px / steptimer.getCap();
	}

	private void getWaterBuckets() {
		if (inv[0] != null && inv[0].getItem() == Items.water_bucket && this.canAcceptMoreWater(FluidContainerRegistry.BUCKET_VOLUME)) {
			water.fill(FluidRegistry.getFluidStack("water", FluidContainerRegistry.BUCKET_VOLUME), true);
			inv[0] = new ItemStack(Items.bucket);
		}
	}

	public boolean canAcceptMoreWater(int amt) {
		return water.getFluid() == null || water.getFluid().amount+amt <= water.getCapacity();
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
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.getFluid("rc ammonia"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.WATER);
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
		if (i == 1)
			return ReikaItemHelper.matchStacks(is, ReactorStacks.lime);
		if (i == 2)
			return ReikaItemHelper.matchStacks(is, ReactorStacks.ammonium);
		return false;
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

	public static boolean isAmmoniaIngredient(ItemStack is) {
		if (ReikaItemHelper.matchStacks(is, ReactorStacks.ammonium))
			return true;
		if (ReikaItemHelper.matchStacks(is, ReactorStacks.lime))
			return true;
		return false;
	}

	public boolean addWater(int amt) {
		if (water.canTakeIn(amt)) {
			water.addLiquid(amt, FluidRegistry.WATER);
			return true;
		}
		return false;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
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
