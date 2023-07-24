/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Thorium;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Blocks.BlockThoriumFuel;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;


public class TileEntityFuelDump extends TileEntityTankedReactorMachine {

	private int fullTicks = 0;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			TileEntity te = this.getAdjacentTileEntity(ForgeDirection.UP);
			if (te instanceof TileEntityThoriumCore) {
				TileEntityThoriumCore tc = (TileEntityThoriumCore)te;
				if (tc.getTemperature() >= tc.FUEL_DUMP_TEMPERATURE && tc.hasFuel()) {
					int rem = tank.getRemainingSpace();
					if (rem > 0) {
						int fuel = ((TileEntityThoriumCore)te).dumpFuel(this, rem);
						if (fuel > 0) {
							tank.addLiquid(fuel, ReactorCraft.LIFBe_fuel);
						}
						fullTicks = 0;
					}
					else {
						fullTicks++;
						if (fullTicks > 200) {
							this.overload(world, x, y, z);
						}
					}
				}
			}
			if (tank.getLevel() >= 125 && this.canDumpAt(world, x, y-1, z)) {
				this.dumpFuel(world, x, y, z);
			}
		}
	}

	private void dumpFuel(World world, int x, int y, int z) {
		int n1 = Math.min(8, tank.getLevel()/125);
		int n2 = n1-1;
		if (world.getBlock(x, y-1, z) == ReactorBlocks.THORIUM.getBlockInstance()) {
			int fmeta = world.getBlockMetadata(x, y-1, z);
			n1 = Math.min(n1, 7-fmeta);
			n2 = n1+fmeta;
		}
		tank.removeLiquid(n1*125);
		world.setBlock(x, y-1, z, ReactorBlocks.THORIUM.getBlockInstance(), n2, 3);
		fullTicks = 0;
		ReikaSoundHelper.playSoundFromServerAtBlock(world, x, y, z, "random.fizz", 1, 1, true);
	}

	private boolean canDumpAt(World world, int x, int y, int z) {
		return BlockThoriumFuel.canOverwrite(world, x, y, z) || (world.getBlock(x, y, z) == ReactorBlocks.THORIUM.getBlockInstance() && world.getBlockMetadata(x, y, z) < 7);
	}

	private void overload(World world, int x, int y, int z) {
		this.delete();
		world.newExplosion(null, x+0.5, y+0.5, z+0.5, 3, true, true);
		world.setBlock(x, y, z, ReactorBlocks.CORIUMFLOWING.getBlockInstance());
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.hasTile() ? this.getCore().drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.hasTile() ? this.getCore().drain(from, maxDrain, doDrain) : null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return this.hasTile() ? this.getCore().canDrain(from, fluid) : false;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return this.hasTile() ? this.getCore().canConnectToPipe(m) : false;
	}

	private boolean hasTile() {
		return this.getAdjacentTileEntity(ForgeDirection.UP) instanceof TileEntityThoriumCore;
	}

	private TileEntityThoriumCore getCore() {
		return (TileEntityThoriumCore)this.getAdjacentTileEntity(ForgeDirection.UP);
	}

	@Override
	public int getCapacity() {
		return 2000;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return false;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.FUELDUMP;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
