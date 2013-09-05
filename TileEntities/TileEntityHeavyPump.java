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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityHeavyPump extends TileEntityReactorBase implements ShaftPowerReceiver, ITankContainer {

	public static final int MINPOWER = 65536;
	private int torque;
	private int omega;
	private long power;

	private LiquidTank tank = new LiquidTank(4000);

	@Override
	public int getIndex() {
		return ReactorTiles.HEAVYPUMP.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (power > 0) {
			phi += 0.05F;
		}
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
	public int[] getInputBlocksX() {
		return new int[0];
	}

	@Override
	public int[] getInputBlocksY() {
		return new int[0];
	}

	@Override
	public int[] getInputBlocksZ() {
		return new int[0];
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		return x == xCoord && y == yCoord && z == zCoord;
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
	public String getName() {
		return this.getTEName();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (power >= MINPOWER && this.canHarvest(world, x, y, z)) {
			this.harvest();
		}
		if (tank.getLiquid() != null) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.values()[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				TileEntity te = world.getBlockTileEntity(dx, dy, dz);
				if (te instanceof ITankContainer) {
					ITankContainer ic = (ITankContainer)te;
					LiquidStack is = this.drain(0, tank.getLiquid().amount, true);
					ic.fill(dir, is, true);
				}
			}
		}
	}

	private void harvest() {
		tank.fill(new LiquidStack(ReactorCraft.D2O.itemID, 200), true);
	}

	private boolean canHarvest(World world, int x, int y, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean) && y < 30 && this.isOceanFloor(world, x, y, z);
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
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[]{tank};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}

	public boolean hasABucket() {
		return tank.getLiquid() != null && tank.getLiquid().amount >= LiquidContainerRegistry.BUCKET_VOLUME;
	}

	public void subtractBucket() {
		tank.drain(LiquidContainerRegistry.BUCKET_VOLUME, true);
	}

}
