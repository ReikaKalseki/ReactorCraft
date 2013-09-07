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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityHeavyPump extends TileEntityReactorBase implements ShaftPowerReceiver, ITankContainer {

	public static final int MINPOWER = 65536;
	private int torque;
	private int omega;
	private long power;
	private int iotick;

	private StepTimer timer = new StepTimer(20);

	private LiquidTank tank = new LiquidTank(4000);

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
		tank.fill(new LiquidStack(ReactorCraft.D2O.itemID, 200), true);
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
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from == ForgeDirection.DOWN || from == ForgeDirection.UP)
			return null;
		else
			return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection from) {
		if (from == ForgeDirection.DOWN || from == ForgeDirection.UP)
			return new ILiquidTank[0];
		else
			return new ILiquidTank[]{tank};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection from, LiquidStack type) {
		if (from == ForgeDirection.DOWN || from == ForgeDirection.UP)
			return null;
		else
			return tank;
	}

	public boolean hasABucket() {
		return tank.getLiquid() != null && tank.getLiquid().amount >= LiquidContainerRegistry.BUCKET_VOLUME;
	}

	public void subtractBucket() {
		tank.drain(LiquidContainerRegistry.BUCKET_VOLUME, true);
	}

	public int getTankLevel() {
		return tank.getLiquid() != null ? tank.getLiquid().amount : 0;
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

}
