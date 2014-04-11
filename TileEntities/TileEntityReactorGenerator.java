/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaBuildCraftHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerEmitter;
import Reika.RotaryCraft.Auxiliary.Interfaces.SimpleProvider;
import Reika.RotaryCraft.Base.TileEntity.TileEntityIOMachine;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntityPowerBus;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntityShaft;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntitySplitter;
import cofh.api.energy.IEnergyHandler;

public class TileEntityReactorGenerator extends TileEntityReactorBase implements IEnergyHandler {

	private ForgeDirection facingDir;

	private long power;
	private int torquein;
	private int omegain;

	public boolean hasMultiblock;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.setFacing(ForgeDirection.EAST);
		if ((world.getWorldTime()&31) == 0)
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);

		if (hasMultiblock)
			this.getPower(world, x, y, z, meta);
		else {
			omegain = torquein = 0;
		}
		power = (long)omegain*(long)torquein;

		if (omegain > 0)
			this.testFailure(world, x, y, z);

		//ReikaJavaLibrary.pConsole(power, Side.SERVER);

		if (power > 0) {
			int writex = x+this.getFacing().getOpposite().offsetX;
			int writey = y+this.getFacing().getOpposite().offsetY;
			int writez = z+this.getFacing().getOpposite().offsetZ;
			int id = world.getBlockId(writex, writey, writez);
			if (id != 0) {
				Block b = Block.blocksList[id];
				int metadata = world.getBlockMetadata(writex, writey, writez);
				if (b.hasTileEntity(metadata)) {
					TileEntity tile = world.getBlockTileEntity(writex, writey, writez);
					if (tile instanceof IEnergyHandler) {
						IEnergyHandler rc = (IEnergyHandler)tile;
						if (rc.canInterface(facingDir)) {
							int rf = this.getGenRF();
							float used = rc.receiveEnergy(facingDir, rf, false);
						}
					}
				}
			}
		}
	}

	private void testFailure(World world, int x, int y, int z) {
		if (ReikaEngLibrary.mat_rotfailure(ReikaEngLibrary.rhoiron, 7, omegain, 100*ReikaEngLibrary.Tsteel))
			this.fail(world, x, y, z);
	}

	private void fail(World world, int x, int y, int z) {
		world.setBlock(x, y, z, 0);
		FlyingBlocksExplosion ex = new FlyingBlocksExplosion(world, null, x+0.5, y+0.5, z+0.5, this.getGeneratorLength());
		ex.doExplosionA();
		ex.doExplosionB(true);
	}

	private int getGeneratorLength() {
		return 12;
	}

	private void getPower(World world, int x, int y, int z, int meta) {
		int len = this.getGeneratorLength();
		int dx = x+this.getFacing().offsetX*len;
		int dy = y+this.getFacing().offsetY*len;
		int dz = z+this.getFacing().offsetZ*len;
		int ex = x+this.getFacing().offsetX*(len-1);
		int ey = y+this.getFacing().offsetY*(len-1);
		int ez = z+this.getFacing().offsetZ*(len-1);

		MachineRegistry m = MachineRegistry.getMachine(world, dx, dy, dz);
		TileEntity te = this.getTileEntity(dx, dy, dz);

		if (m == MachineRegistry.SHAFT) {
			TileEntityShaft devicein = (TileEntityShaft)te;
			if (devicein.isCross()) {
				this.readFromCross(devicein, ex, ey, ez);
				return;
			}
			if (devicein.isWritingToCoordinate(ex, ey, ez)) {
				torquein = devicein.torque;
				omegain = devicein.omega;
			}
		}
		if (m == MachineRegistry.POWERBUS) {
			TileEntityPowerBus pwr = (TileEntityPowerBus)te;
			ForgeDirection dir = this.getFacing().getOpposite();
			omegain = pwr.getSpeedToSide(dir);
			torquein = pwr.getTorqueToSide(dir);
		}
		if (te instanceof SimpleProvider) {
			TileEntityIOMachine io = (TileEntityIOMachine)te;
			torquein = io.torque;
			omegain = io.omega;
		}
		if (te instanceof ShaftPowerEmitter) {
			ShaftPowerEmitter sp = (ShaftPowerEmitter)te;
			if (sp.isEmitting() && sp.canWriteToBlock(ex, ey, ez)) {
				torquein = sp.getTorque();
				omegain = sp.getOmega();
			}
		}
		if (m == MachineRegistry.SPLITTER) {
			TileEntitySplitter devicein = (TileEntitySplitter)te;
			if (devicein.isSplitting()) {
				this.readFromSplitter(devicein, ex, ey, ez);
				return;
			}
			else if (devicein.isWritingToCoordinate(ex, ey, ez)) {
				torquein = devicein.torque;
				omegain = devicein.omega;
			}
		}
	}

	protected void readFromCross(TileEntityShaft cross, int ex, int ey, int ez) {
		if (cross.isWritingToCoordinate(ex, ey, ez)) {
			omegain = cross.readomega[0];
			torquein = cross.readtorque[0];
		}
		else if (cross.isWritingToCoordinate2(ex, ey, ez)) {
			omegain = cross.readomega[1];
			torquein = cross.readtorque[1];
		}
		else
			return; //not its output
	}

	private void readFromSplitter(TileEntitySplitter spl, int ex, int ey, int ez) {
		int sratio = spl.getRatioFromMode();
		if (sratio == 0)
			return;
		boolean favorbent = false;
		if (sratio < 0) {
			favorbent = true;
			sratio = -sratio;
		}
		if (ex == spl.writeinline[0] && ez == spl.writeinline[1]) { //We are the inline
			omegain = spl.omega; //omega always constant
			if (sratio == 1) { //Even split, favorbent irrelevant
				torquein = spl.torque/2;
				return;
			}
			if (favorbent) {
				torquein = spl.torque/sratio;
			}
			else {
				torquein = (int)(spl.torque*((sratio-1D))/sratio);
			}
		}
		else if (ex == spl.writebend[0] && ez == spl.writebend[1]) { //We are the bend
			omegain = spl.omega; //omega always constant
			if (sratio == 1) { //Even split, favorbent irrelevant
				torquein = spl.torque/2;
				return;
			}
			if (favorbent) {
				torquein = (int)(spl.torque*((sratio-1D)/(sratio)));
			}
			else {
				torquein = spl.torque/sratio;
			}
		}
		else { //We are not one of its write-to blocks
			torquein = 0;
			omegain = 0;
			power = 0;
			return;
		}
	}

	public int getGenRF() {
		return (int)(power*10/ReikaBuildCraftHelper.getWattsPerMJ());
	}

	public ForgeDirection getFacing() {
		return facingDir != null ? facingDir : ForgeDirection.EAST;
	}

	protected void setFacing(ForgeDirection dir) {
		facingDir = dir;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.GENERATOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facingDir = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");

		power = NBT.getLong("pwr");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());
		NBT.setBoolean("multi", hasMultiblock);

		NBT.setLong("pwr", power);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return this.getGenRF();
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return from == facingDir.getOpposite();
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

}
