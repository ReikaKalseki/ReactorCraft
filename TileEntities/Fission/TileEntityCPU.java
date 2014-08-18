/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Event.ScramEvent;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorSounds;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.RotaryCraft.API.PowerTransferHelper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCPU extends TileEntityReactorBase implements ReactorPowerReceiver, Temperatured {

	private final ReactorControlLayout layout = new ReactorControlLayout(this);
	private final BlockArray reactor = new BlockArray();

	public static final int POWERPERROD = 1024;

	private int omega;
	private int torque;
	private long power;

	private int redstoneUpdate = 200;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();
		if (thermalTicker.checkCap())
			this.updateTemperature(world, x, y, z);
		if (reactor.isEmpty()) {
			layout.clear();
			int r = 6;
			Block id = ReactorBlocks.REACTOR.getBlockInstance();
			Block id2 = ReactorBlocks.MODELREACTOR.getBlockInstance();
			for (int i = 2; i < 6; i++)
				reactor.recursiveMultiAddWithBounds(world, x+dirs[i].offsetX, y, z+dirs[i].offsetZ, x-r, y, z-r, x+r, y, z+r, id, id2);
			for (int i = 0; i < reactor.getSize(); i++) {
				int[] xyz = reactor.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				Block idx = world.getBlock(dx, dy, dz);
				int metax = world.getBlockMetadata(dx, dy, dz);
				if (idx == ReactorTiles.CONTROL.getBlock() && metax == ReactorTiles.CONTROL.getBlockMetadata()) {
					TileEntityControlRod rod = (TileEntityControlRod)world.getTileEntity(dx, dy, dz);
					layout.addControlRod(rod);
				}
			}
		}

		if ((world.getTotalWorldTime()&16) == 16)
			reactor.clear();

		if (!PowerTransferHelper.checkPowerFromAllSides(this, true)) {
			this.noInputMachine();
		}

		if (power < layout.getMinPower())
			this.SCRAM();

		if (layout.getNumberRods() > 0) {
			if (temperature > this.getMaxTemperature() && power >= layout.getMinPower()*4) {
				ReactorAchievements.SCRAM.triggerAchievement(this.getPlacer());
				this.SCRAM();
			}
		}

		if (redstoneUpdate > 0) {
			redstoneUpdate--;
			if (redstoneUpdate <= 0) {
				world.markBlockForUpdate(x, y, z);
				ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
			}
		}

	}

	public void SCRAM() {
		MinecraftForge.EVENT_BUS.post(new ScramEvent(this, temperature));
		layout.SCRAM();
		if (redstoneUpdate == 0)
			redstoneUpdate = 7;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.CPU.ordinal();
	}

	public ReactorControlLayout getLayout() {
		return layout;
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
	public int getIORenderAlpha() {
		return 0;
	}

	@Override
	public void setIORenderAlpha(int io) {}

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
		torque = omega = 0;
		power = 0;
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
	public int getMaxTemperature() {
		return 800;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq != LiquidStates.EMPTY;
	}

	public void lowerAllRods() {
		ArrayList<TileEntityControlRod> li = layout.getAllRods();
		for (int i = 0; i < li.size(); i++) {
			TileEntityControlRod te = li.get(i);
			te.setActive(true, false);
		}
		ReactorSounds.CONTROL.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, 1, 1.3F);
		redstoneUpdate = 30;
	}

	public void raiseAllRods() {
		ArrayList<TileEntityControlRod> li = layout.getAllRods();
		for (int i = 0; i < li.size(); i++) {
			TileEntityControlRod te = li.get(i);
			te.setActive(false, false);
		}
		ReactorSounds.CONTROL.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, 1, 1.3F);
		redstoneUpdate = 30;
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
		return 1;
	}

	@Override
	public long getMinPower() {
		return layout != null ? layout.getMinPower() : 0;
	}

	@Override
	public int getRedstoneOverride() {
		return layout.isEmpty() ? 0 : 15*layout.countLoweredRods()/layout.getNumberRods();
	}

}
