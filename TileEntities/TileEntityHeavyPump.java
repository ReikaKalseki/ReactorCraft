/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityHeavyPump extends TileEntityReactorBase implements ReactorPowerReceiver, IFluidHandler, PipeConnector {

	public static final int MINPOWER = 65536;
	public static final int MINTORQUE = 512;
	private int torque;
	private int omega;
	private long power;
	private int iotick;

	private static final HashMap<Fluid, Extraction> extractions = new HashMap();

	static {
		extractions.put(FluidRegistry.WATER, new HeavyWaterExtraction());
		extractions.put(FluidRegistry.LAVA, new MoltenLithiumExtraction());
	}

	private StepTimer timer = new StepTimer(20);

	private final HybridTank tank = new HybridTank("heavypump", 8000);

	@Override
	public int getIndex() {
		return ReactorTiles.FLUIDEXTRACTOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (power >= MINPOWER && torque >= MINTORQUE) {
			phi += 10F;
		}
		iotick -= 8;
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
	public boolean canReadFrom(ForgeDirection dir) {
		return dir.offsetY != 0;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!PowerTransferHelper.checkPowerFrom(this, ForgeDirection.DOWN) && !PowerTransferHelper.checkPowerFrom(this, ForgeDirection.UP)) {
			this.noInputMachine();
		}

		if (power >= MINPOWER && torque >= MINTORQUE) {
			timer.setCap(Math.max(1, 20-2*(int)ReikaMathLibrary.logbase(omega, 2)));
			timer.update();
			Extraction e = this.getExtraction(world, x, y, z);
			if (e != null) {
				if (timer.checkCap() && e.canPerform(world, x, y, z)) {
					this.harvest(e, world, x, y, z);
				}
			}
			else {
				timer.reset();
			}
		}
	}

	private Extraction getExtraction(World world, int x, int y, int z) {
		Fluid f = null;
		int c = 0;
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dz = z+dir.offsetZ;
			Fluid f2 = ReikaWorldHelper.getFluid(world, dx, y, dz);
			if (f2 != null && ReikaWorldHelper.isLiquidSourceBlock(world, dx, y, dz)) {
				if (f == null || f2 == f) {
					c++;
					f = f2;
				}
				else {
					return null;
				}
			}
		}
		return f != null && c >= 3 ? extractions.get(f) : null;
	}

	private void harvest(Extraction e, World world, int x, int y, int z) {
		if (e instanceof HeavyWaterExtraction)
			ReactorAchievements.HEAVYWATER.triggerAchievement(this.getPlacer());
		tank.fill(new FluidStack(e.output, e.getExtractedAmount(world, x, y, z)), true);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from.offsetY != 0)
			return null;
		else
			return tank.drain(maxDrain, doDrain);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0 && ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	public boolean hasABucket() {
		return tank.getFluid() != null && tank.getFluid().amount >= FluidContainerRegistry.BUCKET_VOLUME;
	}

	public void subtractBucket() {
		tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
	}

	public int getTankLevel() {
		return tank.getFluid() != null ? tank.getFluid().amount : 0;
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
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);

		omega = NBT.getInteger("speed");
		torque = NBT.getInteger("trq");
		power = NBT.getLong("pwr");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);

		NBT.setInteger("speed", omega);
		NBT.setInteger("trq", torque);
		NBT.setLong("pwr", power);
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side.offsetY == 0;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side.offsetY == 0 ? Flow.OUTPUT : Flow.NONE;
	}

	@Override
	public int getMinTorque(int available) {
		return MINTORQUE;
	}

	@Override
	public int getMinTorque() {
		return MINTORQUE;
	}

	@Override
	public int getMinSpeed() {
		return 1;
	}

	@Override
	public long getMinPower() {
		return MINPOWER;
	}

	private static abstract class Extraction {

		protected final Fluid output;

		private Extraction(Fluid f) {
			output = f;
		}

		protected abstract boolean canPerform(World world, int x, int y, int z);

		protected abstract int getExtractedAmount(World world, int x, int y, int z);

	}

	public static class HeavyWaterExtraction extends Extraction {

		public static final int MAXY = 45;
		public static final int MINDEPTH = 16;

		private HeavyWaterExtraction() {
			super(FluidRegistry.getFluid("rc heavy water"));
		}

		@Override
		protected boolean canPerform(World world, int x, int y, int z) {
			return this.isValidWorld(world) && y < MAXY && ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(x, z)) && this.isOceanFloor(world, x, y, z);
		}

		private boolean isValidWorld(World world) {
			return ReactorCraft.config.isDimensionValidForHeavyWater(world.provider.dimensionId);
		}

		private boolean isOceanFloor(World world, int x, int y, int z) {
			for (int i = 0; i < MINDEPTH; i++) {
				int dy = y+i;
				for (int a = -1; a <= 1; a += 2) {
					for (int b = -1; b <= 1; b += 2) {
						Block id = world.getBlock(x+a, dy, z+b);
						int meta = world.getBlockMetadata(x+a, dy, z+b);
						if ((id != Blocks.flowing_water && id != Blocks.water) || meta != 0) {
							return false;
						}
					}
				}
				if (i >= 1) {
					Block id = world.getBlock(x, dy, z);
					int meta = world.getBlockMetadata(x, dy, z);
					if ((id != Blocks.flowing_water && id != Blocks.water) || meta != 0) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		protected int getExtractedAmount(World world, int x, int y, int z) {
			return 200;
		}

	}

	private static class MoltenLithiumExtraction extends Extraction {

		private MoltenLithiumExtraction() {
			super(FluidRegistry.getFluid("rc lithium"));
		}

		@Override
		protected boolean canPerform(World world, int x, int y, int z) {
			return y == this.getSurfaceY(world, x, y, z) && this.isLavaSurface(world, x, y, z);
		}

		private boolean isLavaSurface(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y-1, z);
			Block b2 = world.getBlock(x, y+1, z);
			return (b == Blocks.lava || b == Blocks.flowing_lava) && (b2 != Blocks.lava && b2 != Blocks.flowing_lava);
		}

		private int getSurfaceY(World world, int x, int y, int z) {
			switch(world.provider.dimensionId) {
				case 0:
					return 10;
				case -1:
					return 31;
				default:
					return -1;
			}
		}

		@Override
		protected int getExtractedAmount(World world, int x, int y, int z) {
			return world.provider.dimensionId == -1 ? 10+rand.nextInt(21)+rand.nextInt(51) : 10+rand.nextInt(31);
		}

	}

	public Fluid getFluid() {
		return tank.getActualFluid();
	}

}
