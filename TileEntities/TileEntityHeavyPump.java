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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityHeavyPump extends TileEntityReactorBase implements ShaftPowerReceiver, IFluidHandler {

	public static final int MINPOWER = 65536;
	private int torque;
	private int omega;
	private long power;
	private int iotick;

	private StepTimer timer = new StepTimer(20);

	private HybridTank tank = new HybridTank("heavypump", 4000);

	@Override
	public int getIndex() {
		return ReactorTiles.HEAVYPUMP.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (power > 0) {
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
	public boolean canReadFromBlock(int x, int y, int z) {
		return x == xCoord && y == yCoord+1 && z == zCoord;
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
		timer.update();
		if (timer.checkCap() && power >= MINPOWER && this.canHarvest(world, x, y, z)) {
			this.harvest();
		}
	}

	private void harvest() {
		tank.fill(new FluidStack(ReactorCraft.D2O, 200), true);
	}

	private boolean canHarvest(World world, int x, int y, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean) && y < 36 && this.isOceanFloor(world, x, y, z);
	}

	private boolean isOceanFloor(World world, int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.values()[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			int id = world.getBlockId(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != Block.waterMoving.blockID && id != Block.waterStill.blockID) {
				return false;
			}
		}
		for (int i = 1; i < 16; i++) {
			int dy = y+i;
			int id = world.getBlockId(x, dy, z);
			int meta = world.getBlockMetadata(x, dy, z);
			if (id != Block.waterMoving.blockID && id != Block.waterStill.blockID) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from == ForgeDirection.DOWN || from == ForgeDirection.UP)
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
		return this.drain(from, resource.amount, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
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
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		if (NBT.hasKey("internalFluid")) {
			tank.setFluid(new FluidStack(NBT.getInteger("liquidId"), NBT.getInteger("internalFluid")));
		}
		else if (NBT.hasKey("tank")) {
			tank.setFluid(FluidStack.loadFluidStackFromNBT(NBT.getCompoundTag("tank")));
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		if (tank.getFluid() != null) {
			NBT.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
		}
	}

}
