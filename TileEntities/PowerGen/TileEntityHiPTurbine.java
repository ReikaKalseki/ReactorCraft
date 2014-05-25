/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.ReactorCraft.Blocks.BlockTurbineMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.TileEntities.Piping.TileEntityPipe;

public class TileEntityHiPTurbine extends TileEntityTurbineCore {

	public static final int GEN_OMEGA = 131072;
	public static final int TORQUE_CAP = 65536;

	@Override
	public boolean needsMultiblock() {
		return true;
	}

	@Override
	public int getMaxTorque() {
		return 65536;
	}

	@Override
	public int getMaxSpeed() {
		return 131072;
	}

	@Override
	protected int getMaxStage() {
		return 6;
	}

	@Override
	protected double getRadius() {
		return 1.5+this.getStage()/2;
	}

	@Override
	protected void dumpSteam(World world, int x, int y, int z, int meta) {
		int stage = this.getStage();
		if (stage == this.getNumberStagesTotal()) {
			int tx = x;
			int tz = z;
			int th = ((BlockTurbineMulti)ReactorBlocks.TURBINEMULTI.getBlockVariable()).getThickness(stage);
			int ty = -1+y-th;
			TileEntity te = this.getTileEntity(tx, ty, tz);
			FluidStack fs = new FluidStack(FluidRegistry.getFluid("lowpwater"), TileEntityReactorBoiler.WATER_PER_STEAM);
			if (te instanceof IFluidHandler) {
				((IFluidHandler)te).fill(ForgeDirection.DOWN, fs, true);
			}
			else if (te instanceof PipeConnector) {
				((PipeConnector)te).fill(ForgeDirection.DOWN, fs, true);
			}
			else if (te instanceof TileEntityPipe) {
				((TileEntityPipe)te).setFluid(fs.getFluid());
				((TileEntityPipe)te).addFluid(fs.amount);
			}
			ReikaParticleHelper.DRIPWATER.spawnAroundBlock(world, tx, ty+1+rand.nextInt(th), tz, 5);
			ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(this.getSteamMovement());
			int n = ConfigRegistry.SPRINKLER.getValue()*12;
			for (int i = 0; i < n; i++) {
				double px = tx+(-4+rand.nextDouble()*8)*dir.offsetX;
				double pz = tz+(-4+rand.nextDouble()*8)*dir.offsetZ;
				ReikaParticleHelper.RAIN.spawnAt(world, px, ty+1+rand.nextInt(th*2), pz);
			}
		}
	}

	@Override
	protected double getEfficiency() {
		switch(this.getNumberStagesTotal()) {
		case 0:
			return 0.0125;
		case 1:
			return 0.025;
		case 2:
			return 0.075;
		case 3:
			return 0.125;
		case 4:
			return 0.25;
		case 5:
			return 0.5;
		case 6:
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public int getIndex() {
		return ReactorTiles.BIGTURBINE.ordinal();
	}

	@Override
	protected double getAnimationSpeed() {
		return 0.55F;
	}

	@Override
	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getSteamMovement().getOpposite();
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;

		boolean flag = false;

		if (ReactorTiles.getTE(world, dx, dy, dz) == ReactorTiles.STEAMLINE) {
			TileEntitySteamLine te = (TileEntitySteamLine)this.getAdjacentTileEntity(dir);
			int s = te.getSteam();
			if (s > 8 && te.getWorkingFluid() == WorkingFluid.WATER) {
				int rm = s/8+1;
				steam += rm;
				te.removeSteam(rm);
				flag = true;
			}
		}

		return flag;
	}

}
