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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Power.BasicPowerHandler;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityCentrifuge extends TileEntityInventoriedReactorBase implements IFluidHandler, ReactorPowerReceiver, PipeConnector {

	/** "In the range of 100000 rpm" -> 10.5k rad/s <br>
	 * http://science.howstuffworks.com/uranium-centrifuge.htm */
	//public static final int REAL_SPEED = 16384;

	public static final int MINSPEED = 262144; //much faster since doing it in one step

	public static final int UF6_PER_DUST = 50;
	public static final int FUEL_CHANCE = 9;

	private static final HashMap<Fluid, Centrifuging> recipes = new HashMap();

	private final HybridTank tank = new HybridTank("centri", 12000);

	private StepTimer timer = new StepTimer(900);

	private final BasicPowerHandler powerHandler = new BasicPowerHandler();
	public int split; //timer

	public TileEntityCentrifuge() {
		ReikaJavaLibrary.initClass(Centrifuging.class, true);
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.CENTRIFUGE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		int omega = powerHandler.getOmega();
		if (omega >= 262144) {
			phi += 40;
		}
		else if (omega >= 65536) {
			phi += 30;
		}
		else if (omega >= 16384) {
			phi += 20;
		}
		else if (omega >= 4096) {
			phi += 15;
		}
		if (omega >= 1024) {
			phi += 10;
		}
		if (omega >= 256) {
			phi += 7;
		}
		else if (omega > 0) {
			phi += 5;
		}
		powerHandler.decrementIOTick(8);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		timer.setCap(this.setTimer());

		if (!PowerTransferHelper.checkPowerFrom(this, ForgeDirection.DOWN)) {
			this.noInputMachine();
		}

		if (powerHandler.getPower() > 0 && powerHandler.getOmega() >= MINSPEED && !tank.isEmpty()) {
			Centrifuging recipe = recipes.get(tank.getActualFluid());
			if (recipe != null && tank.getLevel() >= recipe.fluidAmount && this.hasInventorySpace(recipe)) {
				timer.update(recipe.speedFactor);
				if (timer.checkCap()) {
					if (!world.isRemote)
						this.make(recipe);
				}
			}
			else {
				timer.reset();
			}
		}
		else {
			timer.reset();
		}
		if (!world.isRemote) {
			split = timer.getTick();
		}
	}

	private int setTimer() {
		int omega = powerHandler.getOmega();
		if (omega >= 67108864) {
			return 8;
		}
		else if (omega >= 33554432) {
			return 20;
		}
		else if (omega >= 16777216) {
			return 50;
		}
		else if (omega >= 8388608) {
			return 100;
		}
		else if (omega >= 4194304) {
			return 240;
		}
		else if (omega >= 2097152) {
			return 400;
		}
		else if (omega >= 1048576) {
			return 600;
		}
		else if (omega >= 524288) {
			return 800;
		}
		else
			return 900;
	}

	private void make(Centrifuging recipe) {
		tank.removeLiquid(recipe.fluidAmount);
		if (ReikaRandomHelper.doWithPercentChance(recipe.chanceOfAOverB)) {
			ReikaInventoryHelper.addOrSetStack(recipe.getOutputA(), inv, 0);
		}
		else if (recipe.outputB != null) {
			ReikaInventoryHelper.addOrSetStack(recipe.getOutputB(), inv, 1);
		}
	}

	private boolean hasInventorySpace(Centrifuging recipe) {
		if (inv[0] != null && !ReikaItemHelper.matchStacks(inv[0], recipe.outputA))
			return false;
		if (inv[1] != null && recipe.outputB != null && !ReikaItemHelper.matchStacks(inv[1], recipe.outputB))
			return false;
		if (inv[0] != null && inv[0].stackSize >= inv[0].getMaxStackSize())
			return false;
		if (inv[1] != null && inv[1].stackSize >= inv[1].getMaxStackSize())
			return false;
		return true;
	}

	public int getProcessingScaled(int p) {
		return (int)(p*split/(float)timer.getCap());
	}

	public int getFluidScaled(int p) {
		return p*tank.getLevel()/tank.getCapacity();
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int getOmega() {
		return powerHandler.getOmega();
	}

	@Override
	public int getTorque() {
		return powerHandler.getTorque();
	}

	@Override
	public long getPower() {
		return powerHandler.getPower();
	}

	@Override
	public int getIORenderAlpha() {
		return powerHandler.getIORenderAlpha();
	}

	@Override
	public void setIORenderAlpha(int io) {
		powerHandler.setIORenderAlpha(io);
	}

	@Override
	public void setOmega(int omega) {
		powerHandler.setOmega(omega);
	}

	@Override
	public void setTorque(int torque) {
		powerHandler.setTorque(torque);
	}

	@Override
	public void setPower(long power) {
		powerHandler.setPower(power);
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
		powerHandler.noInputMachine();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
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
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && recipes.containsKey(fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		split = NBT.getInteger("time");

		powerHandler.readFromNBT(NBT);

		tank.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("time", split);

		powerHandler.writeToNBT(NBT);

		tank.writeToNBT(NBT);
	}

	public boolean canAcceptMoreUF6(int amt) {
		return tank.getFluid() == null || tank.getFluid().amount+amt <= tank.getCapacity();
	}

	public void addUF6(int amt) {
		tank.addLiquid(amt, ReactorCraft.UF6);
	}

	public void removeFluid(int volume) {
		tank.removeLiquid(volume);
	}

	@Override
	public boolean canItemEnterFromSide(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean canItemExitToSide(ForgeDirection dir) {
		return dir.offsetY != 0;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side == ForgeDirection.UP;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == ForgeDirection.UP ? Flow.INPUT : Flow.NONE;
	}

	@Override
	public int getMinTorque(int available) {
		return 1;
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
		return 1;
	}

	public int getUF6() {
		return tank.getActualFluid() == ReactorCraft.UF6 ? tank.getLevel() : 0;
	}

	public Fluid getFluid() {
		return tank.getActualFluid();
	}

	public static Collection<Centrifuging> getRecipes() {
		ReikaJavaLibrary.initClass(Centrifuging.class, true);
		return Collections.unmodifiableCollection(recipes.values());
	}

	public static Centrifuging getRecipe(Fluid f) {
		ReikaJavaLibrary.initClass(Centrifuging.class, true);
		return recipes.get(f);
	}

	public static enum Centrifuging {

		UF6(ReactorCraft.UF6, UF6_PER_DUST, ReactorStacks.fueldust, ReactorStacks.depdust, FUEL_CHANCE);

		public final Fluid input;
		public final int fluidAmount;
		private final ItemStack outputA;
		private final ItemStack outputB;
		public final float chanceOfAOverB; //percentage

		public final int minSpeed;
		public final int speedFactor;

		private Centrifuging(Fluid f, int amt, ItemStack a, ItemStack b, float c) {
			this(f, amt, a, b, c, MINSPEED, 1);
		}

		private Centrifuging(Fluid f, int amt, ItemStack a, ItemStack b, float c, int ms, int sf) {
			input = f;
			fluidAmount = amt;
			outputA = a;
			outputB = b;
			chanceOfAOverB = c;

			minSpeed = ms;
			speedFactor = sf;

			if (recipes.containsKey(f))
				throw new IllegalArgumentException("Fluid "+f.getName()+" already registered to a recipe!");
			recipes.put(f, this);
		}

		public ItemStack getOutputA() {
			return outputA.copy();
		}

		public ItemStack getOutputB() {
			return outputB != null ? outputB.copy() : null;
		}

		public boolean produces(ItemStack result) {
			return ReikaItemHelper.matchStacks(outputA, result) || (outputB != null && ReikaItemHelper.matchStacks(outputB, result));
		}

	}
}
