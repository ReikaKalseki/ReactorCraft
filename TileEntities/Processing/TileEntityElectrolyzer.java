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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Interfaces.Shockable;
import Reika.RotaryCraft.API.Interfaces.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

import buildcraft.api.tiles.IHasWork;

@Strippable("buildcraft.api.tiles.IHasWork")
public class TileEntityElectrolyzer extends TileEntityInventoriedReactorBase implements IFluidHandler,
PipeConnector, TemperatureTE, ThermalMachine, Shockable, IHasWork {

	public static final int SODIUM_MELT = 98;

	public static final int SALT_MELT = 801;

	public static final int CAPACITY = 6000;

	public static final int MAXTEMP = 1200;

	private final HybridTank tankL = new HybridTank("lighttank", this.getCapacity());
	private final HybridTank tankH = new HybridTank("heavytank", this.getCapacity());

	private final HybridTank input = new HybridTank("input", this.getCapacity()*2);

	private StepTimer timer = new StepTimer(50);
	private StepTimer tempTimer = new StepTimer(20);

	public int time;

	private int temperature;

	//private int omega;
	//private int torque;
	//private long power;
	//private int iotick = 512;

	//public static final int SALTPOWER = 131072;

	private Electrolysis recipe;

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.ELECTROLYZER;
	}

	public int getCapacity() {
		return CAPACITY;
	}

	public int getHLevel() {
		return tankH.getLevel();
	}

	public int getLLevel() {
		return tankL.getLevel();
	}

	public int getTime() {
		return timer.getTick();
	}

	public int getTimerScaled(int d) {
		return d * time / timer.getCap();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {/*
		if (iotick > 0)
			iotick -= 8;

		if (!PowerTransferHelper.checkPowerFromAllSides(this, true)) {
			this.noInputMachine();
		}*/

		tempTimer.update();
		if (tempTimer.checkCap())
			this.updateTemperature(world, x, y, z, meta);
		if (recipe == null)
			recipe = this.findRecipe();
		if (!world.isRemote) {
			if (recipe != null && recipe.requirementsMet(this)) {
				if (timer.checkCap())
					recipe.run(this);
			}
			else {
				recipe = null;
				timer.reset();
			}
			time = timer.getTick();
		}

		//ReikaJavaLibrary.pConsole(timer.getFraction());

		//ReikaJavaLibrary.pConsole(this.getSide()+":"+input+":"+tankH+":"+tankL);
	}

	private Electrolysis findRecipe() {
		for (Electrolysis e : Electrolysis.recipes) {
			if (e.requirementsMet(this))
				return e;
		}
		return null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}
	/*
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
	 */
	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p);
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side.offsetY != 0 ? Flow.OUTPUT : Flow.INPUT;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return input.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		int maxDrain = resource.amount;
		if (from == ForgeDirection.DOWN && resource.getFluid() == tankH.getActualFluid())
			return tankH.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP && resource.getFluid() == tankL.getActualFluid())
			return tankL.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from == ForgeDirection.DOWN)
			return tankH.drain(maxDrain, doDrain);
		if (from == ForgeDirection.UP)
			return tankL.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (from.offsetY != 0)
			return false;
		for (Electrolysis e : Electrolysis.recipes) {
			if (e.uses(fluid))
				return true;
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tankH.getInfo(), tankL.getInfo(), input.getInfo()};
	}
	/*
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
		return true;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = 0;
		torque = 0;
		power = 0;
	}
	 */
	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		for (Electrolysis e : Electrolysis.recipes) {
			if (e.uses(itemstack)) {
				return true;
			}
		}
		return false;
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
		return 0;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
		world.newExplosion(null, x+0.5, y+0.5, z+0.5, 3F, true, true);
	}

	@Override
	public void onDischarge(int charge, double range) {
		if (recipe != null) {
			int extra = charge-this.getMinDischarge();
			int n = extra > 0 ? (int)Math.sqrt(extra)/16 : 1;
			if (n == 0)
				n = 1;
			for (int i = 0; i < n; i++)
				timer.update();
		}
	}

	@Override
	public int getMinDischarge() {
		return 4096;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 1200;
	}

	@Override
	public void onOverheat(World world, int x, int y, int z) {

	}

	@Override
	public boolean canBeFrictionHeated() {
		return true;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tankH.writeToNBT(NBT);
		tankL.writeToNBT(NBT);
		input.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);
		//NBT.setInteger("time", time);

		/*
		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

		NBT.setInteger("io", iotick);*/
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tankH.readFromNBT(NBT);
		tankL.readFromNBT(NBT);
		input.readFromNBT(NBT);

		temperature = NBT.getInteger("temp");
		//time = NBT.getInteger("time");

		/*
		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");

		iotick = NBT.getInteger("io");*/
	}

	public boolean addHeavyWater(int amt) {
		if (input.canTakeIn(amt)) {
			input.addLiquid(amt, FluidRegistry.getFluid("rc heavy water"));
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
		return false;
	}

	@Override
	public float getAimX() {
		return 0.5F;
	}

	@Override
	public float getAimY() {
		return 0.9375F;
	}

	@Override
	public float getAimZ() {
		return 0.5F;
	}

	public int getInputLevel() {
		return input.getLevel();
	}
	/*
	@Override
	public int getMinTorque(int available) {
		return 8;
	}

	@Override
	public int getMinTorque() {
		return 0;
	}

	@Override
	public int getMinSpeed() {
		return 0;
	}

	@Override
	public long getMinPower() {
		return 0;
	}
	 */

	@Override
	public boolean allowExternalHeating() {
		return true;
	}

	@Override
	public boolean canDischargeLongRange() {
		return false;
	}

	@Override
	public float getMultiplier() {
		return 0.5F;
	}

	@Override
	public void resetAmbientTemperatureTimer() {
		tempTimer.reset();
	}

	@Override
	public boolean hasWork() {
		return recipe != null && tankH.canTakeIn(recipe.lowerOutput) && tankL.canTakeIn(recipe.upperOutput);
	}

	public static enum Electrolysis {
		SALT(new ItemMatch("salt/dustSalt").addItem(new KeyedItemStack(ItemStacks.salt)), false, FluidRegistry.getFluid("rc chlorine"), 100, FluidRegistry.getFluid("rc sodium"), 100, SALT_MELT),
		HEAVYWATER(FluidRegistry.getFluid("rc heavy water"), 100, null, false, FluidRegistry.getFluid("rc deuterium"), 100, FluidRegistry.getFluid("rc oxygen"), 50);

		public final FluidStack requiredFluid;
		private final ItemMatch requiredItem;
		public final boolean consumeItem;
		public final FluidStack upperOutput;
		public final FluidStack lowerOutput;
		public final int requiredTemperature;

		private static Electrolysis[] recipes = values();

		private Electrolysis(ItemMatch item, boolean cata, Fluid out1, int amt1, Fluid out2, int amt2) {
			this(null, 0, item, cata, out1, amt1, out2, amt2, 0);
		}

		private Electrolysis(ItemMatch item, boolean cata, Fluid out1, int amt1, Fluid out2, int amt2, int temp) {
			this(null, 0, item, cata, out1, amt1, out2, amt2, temp);
		}

		private Electrolysis(Fluid in, int amt, ItemMatch item, boolean cata, Fluid out1, int amt1, Fluid out2, int amt2) {
			this(in, amt, item, cata, out1, amt1, out2, amt2, 0);
		}

		private Electrolysis(Fluid in, int amt, ItemMatch item, boolean cata, Fluid out1, int amt1, Fluid out2, int amt2, int temp) {
			requiredFluid = in != null ? new FluidStack(in, amt) : null;
			requiredItem = item != null ? item.copy() : null;
			consumeItem = !cata;
			upperOutput = out1 != null ? new FluidStack(out1, amt1) : null;
			lowerOutput = out2 != null ? new FluidStack(out2, amt2) : null;
			requiredTemperature = temp;
		}

		public boolean requirementsMet(TileEntityElectrolyzer te) {
			if (requiredFluid != null) {
				if (te.input.getActualFluid() != requiredFluid.getFluid() || te.input.getLevel() < requiredFluid.amount)
					return false;
			}
			if (requiredItem != null) {
				if (te.inv[0] == null || !requiredItem.match(te.inv[0]))
					return false;
			}
			if (upperOutput != null) {
				if (te.tankL.isFull() || (!te.tankL.isEmpty() && te.tankL.getActualFluid() != upperOutput.getFluid()))
					return false;
			}
			if (lowerOutput != null) {
				if (te.tankH.isFull() || (!te.tankH.isEmpty() && te.tankH.getActualFluid() != lowerOutput.getFluid()))
					return false;
			}
			return te.temperature >= requiredTemperature;
		}

		private void run(TileEntityElectrolyzer te) {
			if (requiredFluid != null) {
				te.input.removeLiquid(requiredFluid.amount);
			}
			if (requiredItem != null && consumeItem) {
				ReikaInventoryHelper.decrStack(0, te.inv);
			}
			if (upperOutput != null) {
				te.tankL.addLiquid(upperOutput.amount, upperOutput.getFluid());
			}
			if (lowerOutput != null) {
				te.tankH.addLiquid(lowerOutput.amount, lowerOutput.getFluid());
			}
		}

		public boolean makes(Fluid f) {
			return (upperOutput != null && upperOutput.getFluid() == f) || (lowerOutput != null && lowerOutput.getFluid() == f);
		}

		public boolean uses(Fluid f) {
			return requiredFluid != null && requiredFluid.getFluid() == f;
		}

		public boolean uses(ItemStack is) {
			return requiredItem != null && requiredItem.match(is);
		}

		public static Electrolysis[] getRecipes() {
			return Arrays.copyOf(recipes, recipes.length);
		}

		public boolean hasItemRequirement() {
			return requiredItem != null;
		}

		public Collection<ItemStack> getItemListForDisplay() {
			Collection<ItemStack> ret = new ArrayList();
			for (KeyedItemStack ks : requiredItem.getItemList()) {
				ret.add(ks.getItemStack());
			}
			return ret;
		}
	}

	public static void addRecipe(String name, Fluid in, int amt, ItemMatch item, boolean cata, Fluid out1, int amt1, Fluid out2, int amt2, int temp) {
		Class[] types = new Class[]{Fluid.class, int.class, ItemMatch.class, boolean.class, Fluid.class, int.class, Fluid.class, int.class, int.class};
		Object[] args = new Object[]{in, amt, item, cata, out1, amt1, out2, amt2, temp};
		Electrolysis c = EnumHelper.addEnum(Electrolysis.class, name.toUpperCase(), types, args);
		Electrolysis.recipes = Electrolysis.values();
	}

}
