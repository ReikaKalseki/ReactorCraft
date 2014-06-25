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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.ModInteract.BCMachineHandler;
import Reika.ReactorCraft.Blocks.BlockTurbineMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Storage.TileEntityReservoir;

public class TileEntityHiPTurbine extends TileEntityTurbineCore {

	public static final int GEN_OMEGA = 131072;
	public static final int TORQUE_CAP = 65536;
	private WorkingFluid fluid = WorkingFluid.EMPTY;

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
			ForgeDirection s = this.getSteamMovement();
			ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(s);
			int th = ((BlockTurbineMulti)ReactorBlocks.TURBINEMULTI.getBlockVariable()).getThickness(stage);
			int ty = -1+y-th;
			for (int i = -th; i <= th; i++) {
				int tx = x+dir.offsetX*i+s.offsetX;
				int tz = z+dir.offsetZ*i+s.offsetZ;
				MachineRegistry m = MachineRegistry.getMachine(world, tx, ty, tz);
				FluidStack fs = new FluidStack(fluid.getLowPressureFluid(), TileEntityReactorBoiler.WATER_PER_STEAM);
				if (m == MachineRegistry.RESERVOIR) {
					TileEntity te = this.getTileEntity(tx, ty, tz);
					((TileEntityReservoir)te).addLiquid(fs.amount, fs.getFluid());
				}
				else if (world.getBlockId(tx, ty, tz) == BCMachineHandler.getInstance().tankID) {
					TileEntity te = this.getTileEntity(tx, ty, tz);
					((IFluidHandler)te).fill(ForgeDirection.UP, fs, true);
				}
				int py = ty+1+rand.nextInt(th*2);
				if (ReikaMathLibrary.py3d(dir.offsetX*i, py-y, dir.offsetZ*i) < th)
					ReikaParticleHelper.DRIPWATER.spawnAroundBlock(world, x+dir.offsetX*i, py, z+dir.offsetZ*i, 5);
			}
			int n = ConfigRegistry.SPRINKLER.getValue()*12;
			double ax = s.offsetX > 0 ? 1.2 : -0.2;
			double az = s.offsetZ > 0 ? 1.2 : -0.2;
			for (int i = 0; i < n; i++) {
				double px = x+(-th+rand.nextDouble()*th*2)*dir.offsetX;
				double pz = z+(-th+rand.nextDouble()*th*2)*dir.offsetZ;
				ReikaParticleHelper.RAIN.spawnAt(world, px+ax, ty+1+rand.nextInt(th*2), pz+az);
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
		return 0.5F;
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
			if (s > 8 && this.canTakeIn(te.getWorkingFluid())) {
				int rm = s/8+1;
				steam += rm;
				te.removeSteam(rm);
				flag = s > rm+32;
			}
		}

		return flag;
	}

	private boolean canTakeIn(WorkingFluid f) {
		return fluid == WorkingFluid.EMPTY || f == fluid;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		fluid = WorkingFluid.getFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		fluid.saveToNBT(NBT);
	}

	@Override
	protected float getTorqueFactor() {
		return fluid.efficiency > 1 ? 1+(fluid.efficiency-1)*0.25F : fluid.efficiency;
	}

}
