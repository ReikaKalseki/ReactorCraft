/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Collections.RelativePositionList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.ModInteract.AtmosphereHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCMachineHandler;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Base.BlockReCMultiBlock;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Storage.TileEntityReservoir;

public class TileEntityHiPTurbine extends TileEntityTurbineCore implements MultiBlockTile {

	public static final int GEN_OMEGA = 131072;
	public static final int FLUID_PER_RESERVOIR = TileEntityReactorBoiler.WATER_PER_STEAM * 131 / 20 / 24 * 6/10;

	private WorkingFluid fluid = WorkingFluid.EMPTY;
	private int dripBuffer;

	private RelativePositionList getInjectors() {
		RelativePositionList injectors = new RelativePositionList();
		ForgeDirection dir = this.getSteamMovement();
		if (dir.offsetX == 0) {
			injectors.addPosition(1, 1, 0);
			injectors.addPosition(0, 1, 0);
			injectors.addPosition(-1, 1, 0);

			injectors.addPosition(1, 0, 0);

			injectors.addPosition(-1, 0, 0);

			injectors.addPosition(1, -1, 0);
			injectors.addPosition(0, -1, 0);
			injectors.addPosition(-1, -1, 0);
		}
		else if (dir.offsetZ == 0) {
			injectors.addPosition(0, 1, 1);
			injectors.addPosition(0, 1, 0);
			injectors.addPosition(0, 1, -1);

			injectors.addPosition(0, 0, 1);

			injectors.addPosition(0, 0, -1);

			injectors.addPosition(0, -1, 1);
			injectors.addPosition(0, -1, 0);
			injectors.addPosition(0, -1, -1);
		}
		return injectors;
	}

	@Override
	public boolean needsMultiblock() {
		return !DragonAPICore.debugtest;
	}

	@Override
	public void setHasMultiBlock(boolean has) {
		if (hasMultiBlock && !has)
			this.testBreakageFailure();
		super.setHasMultiBlock(has);
	}

	private void testBreakageFailure() {
		if (omega > 2048) {
			this.fail(worldObj, xCoord, yCoord, zCoord);
		}
	}

	private void fail(World world, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
		new FlyingBlocksExplosion(world, x, y+0.5, z, 4).doExplosion();
	}

	@Override
	protected boolean checkForMultiblock(World world, int x, int y, int z, int meta) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (dir != this.getSteamMovement() && dir.getOpposite() != this.getSteamMovement()) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				Block mid = world.getBlock(dx, dy, dz);
				int mmeta = world.getBlockMetadata(dx, dy, dz);
				if (mid != ReactorBlocks.TURBINEMULTI.getBlockInstance())
					return false;
				if (mmeta < 8)
					return false;
			}
		}
		return true;
	}

	@Override
	public int getMaxTorque() {
		return fluid.efficiency > 1 ? 131072 : 65536;
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
	protected void copyDataFrom(TileEntityTurbineCore tile) {
		super.copyDataFrom(tile);
		TileEntityHiPTurbine te = (TileEntityHiPTurbine)tile;
		fluid = te.fluid;
		dripBuffer = te.dripBuffer;
		te.dripBuffer = 0;
	}

	@Override
	protected void dumpSteam(World world, int x, int y, int z, int meta) {
		if (dripBuffer > 0 && this.dumpLiquid(world, x, y, z, meta)) {
			ForgeDirection s = this.getSteamMovement();
			ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(s);
			int th = (int)(this.getRadius());
			if (!world.isRemote) {
				for (int d = 0; d <= 1; d++) {
					for (int dy = 2; dy < 5; dy++) {
						int ty = y-th-dy;
						for (int i = -th; i <= th; i++) {
							int tx = x+dir.offsetX*i+s.offsetX*d;
							int tz = z+dir.offsetZ*i+s.offsetZ*d;
							MachineRegistry m = MachineRegistry.getMachine(world, tx, ty, tz);
							if (fluid != null && fluid.getLowPressureFluid() == null) {

							}
							FluidStack fs = new FluidStack(fluid.getLowPressureFluid(), FLUID_PER_RESERVOIR);
							if (m == MachineRegistry.RESERVOIR) {
								TileEntity te = this.getTileEntity(tx, ty, tz);
								((TileEntityReservoir)te).addLiquid(fs.amount, fs.getFluid());
								dripBuffer -= fs.amount;
								break;
							}
							else if (world.getBlock(tx, ty, tz) == BCMachineHandler.getInstance().tankID) {
								TileEntity te = this.getTileEntity(tx, ty, tz);
								((IFluidHandler)te).fill(ForgeDirection.UP, fs, true);
								dripBuffer -= fs.amount;
								break;
							}
							int py = ty+1+rand.nextInt(th*2);
							if (ReikaMathLibrary.py3d(dir.offsetX*i, py-y, dir.offsetZ*i) < th)
								ReikaParticleHelper.DRIPWATER.spawnAroundBlock(world, x+dir.offsetX*i, py, z+dir.offsetZ*i, 5);
						}
						if (dripBuffer <= 0)
							break;
					}
					if (dripBuffer <= 0)
						break;
				}
			}
			int n = ConfigRegistry.SPRINKLER.getValue()*12;
			double ax = s.offsetX > 0 ? 1.2 : -0.2;
			double az = s.offsetZ > 0 ? 1.2 : -0.2;
			int d = -s.offsetX+s.offsetZ;
			for (int i = 0; i < n; i++) {
				double px = x+(-th+rand.nextDouble()*th*2+d)*dir.offsetX;
				double pz = z+(-th+rand.nextDouble()*th*2+d)*dir.offsetZ;
				ReikaParticleHelper.RAIN.spawnAt(world, px+ax, y-th+1+rand.nextInt(th*2), pz+az);
			}
		}
	}

	private boolean dumpLiquid(World world, int x, int y, int z, int meta) {
		if (AtmosphereHandler.isNoAtmo(world, x+this.getSteamMovement().offsetX, y, z+this.getSteamMovement().offsetZ, blockType, false))
			return false;
		return this.getStage() == this.getNumberStagesTotal()-1;
	}

	@Override
	protected double getEfficiency() {
		switch(this.getNumberStagesTotal()) {
			case 1:
				return 0.0125;
			case 2:
				return 0.025;
			case 3:
				return 0.075;
			case 4:
				return 0.125;
			case 5:
				return 0.25;
			case 6:
				return 0.5;
			case 7:
				return 1;
			default:
				return 0;
		}
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.BIGTURBINE;
	}

	@Override
	protected double getAnimationSpeed() {
		return 0.5F;
	}

	@Override
	protected void intakeLubricant(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getSteamMovement().getOpposite();
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;

		if (this.getStage() == 0) {
			RelativePositionList li = this.getInjectors();
			BlockArray pos = li.getPositionsRelativeTo(dx, dy, dz);
			for (int i = 0; i < pos.getSize(); i++) {
				Coordinate c = pos.getNthBlock(i);
				int sx = c.xCoord;
				int sy = c.yCoord;
				int sz = c.zCoord;
				TileEntity tile = world.getTileEntity(sx, sy, sz);
				if (tile instanceof TileEntitySteamInjector) {
					TileEntitySteamInjector te = (TileEntitySteamInjector)tile;
					int lube = te.getLubricant();
					int rem = Math.min(lube, tank.getRemainingSpace());
					if (rem > 0) {
						te.remove(rem);
						tank.addLiquid(rem, FluidRegistry.getFluid("rc lubricant"));
					}
				}
			}
		}
	}

	@Override
	protected boolean enabled(World world, int x, int y, int z) {
		if (!DragonAPICore.debugtest && tank.isEmpty())
			return false;
		if (this.isRedstoned(world, x, y, z))
			return false;
		return super.enabled(world, x, y, z);
	}

	private boolean isRedstoned(World world, int x, int y, int z) {
		ForgeDirection dir = this.getSteamMovement().getOpposite();
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		RelativePositionList li = this.getInjectors();
		BlockArray pos = li.getPositionsRelativeTo(dx, dy, dz);
		for (int i = 0; i < pos.getSize(); i++) {
			Coordinate c = pos.getNthBlock(i);
			int sx = c.xCoord;
			int sy = c.yCoord;
			int sz = c.zCoord;
			if (world.isBlockIndirectlyGettingPowered(sx, sy, sz))
				return true;
		}
		return false;
	}

	@Override
	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getSteamMovement().getOpposite();
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;

		boolean flag = false;

		if (DragonAPICore.debugtest) {
			steam = 5000;
			fluid = WorkingFluid.WATER;
			dripBuffer = 5000;
			return true;
		}

		ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
		if (r == ReactorTiles.STEAMLINE) {
			TileEntitySteamLine te = (TileEntitySteamLine)this.getAdjacentTileEntity(dir);
			int s = te.getSteam();
			//ReikaJavaLibrary.pConsole(steam+"/"+this.getMaxSteam()+" from "+s, Side.SERVER);
			if (s > 8 && this.canTakeIn(te.getWorkingFluid())) {
				Proportionality<ReactorType> source = te.getSourceReactorType();
				s = source != null ? this.getEffectiveUsable(s, source) : 0;
				if (s > 0) {
					int rm = s/8+1;
					if (steam < this.getMaxSteam()) {
						int rm2 = Math.min(rm, this.getMaxSteam()-steam);
						steam += rm2;
						fluid = te.getWorkingFluid();
						te.removeSteam(rm2);
						dripBuffer += rm2*1000;
						//ReikaJavaLibrary.pConsole("Took in "+rm2+" of "+s+" available", Side.SERVER);
					}
					flag = s > rm+32 && steam >= this.getMaxSteam()/15;
					//ReikaJavaLibrary.pConsole("Has "+steam+"/"+this.getMaxSteam()+", s/rm = "+s+"/"+rm, Side.SERVER);
				}
			}
		}
		else if (r == this.getTile()) {
			TileEntityHiPTurbine te = (TileEntityHiPTurbine)this.getAdjacentTileEntity(dir);
			fluid = te.fluid;
		}

		if (steam == 0) {
			fluid = WorkingFluid.EMPTY;
		}

		return flag;
	}

	private int getEffectiveUsable(int s, Proportionality<ReactorType> source) {
		float ret = 0;
		for (ReactorType r : source.getElements()) {
			if (r != null)
				ret += source.getFraction(r)*s*r.getHPTurbineMultiplier();
		}
		return (int)ret;
	}

	private int getMaxSteam() {
		return 3250;//170+this.getMaxTorque()/24;
	}

	@Override
	protected int getConsumedLubricant() {
		return 100;
	}

	private boolean canTakeIn(WorkingFluid f) {
		return fluid == WorkingFluid.EMPTY || f == fluid;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		fluid = WorkingFluid.getFromNBT(NBT);
		dripBuffer = NBT.getInteger("dripb");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		fluid.saveToNBT(NBT);
		NBT.setInteger("dripb", dripBuffer);
	}

	@Override
	protected float getTorqueFactor() {
		float base = super.getTorqueFactor();
		if (steam < this.getMaxSteam()/2) {
			float f = steam/(float)this.getMaxSteam(); //stops at 0.5, aka the peak of cos
			base *= ReikaMathLibrary.cosInterpolation(0, 1, f, 0, 1);
		}
		if (fluid.efficiency > 1) {
			base *= 1+(fluid.efficiency-1)*0.25F;
		}
		return base;
	}

	@Override
	public void breakBlock() {
		if (!worldObj.isRemote) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = xCoord+dir.offsetX;
				int dy = yCoord+dir.offsetY;
				int dz = zCoord+dir.offsetZ;
				Block b = worldObj.getBlock(dx, dy, dz);
				if (b instanceof BlockReCMultiBlock) {
					((BlockReCMultiBlock)b).breakMultiBlock(worldObj, dx, dy, dz);
				}
			}
		}
	}

	@Override
	protected int getLubricantCapacity() {
		return 128000;
	}

	@Override
	protected boolean canCollideCheck() {
		return false;
	}

}
