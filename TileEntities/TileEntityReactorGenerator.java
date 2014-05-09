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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaBuildCraftHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntityShaft;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntitySplitter;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityReactorGenerator extends TileEntityReactorBase implements IEnergyHandler {

	private ForgeDirection facingDir;

	private long power;
	private int torquein;
	private int omegain;

	public boolean hasMultiblock;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if ((world.getWorldTime()&31) == 0)
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);

		if (hasMultiblock)
			this.getPower(world, x, y, z, meta);
		else {
			omegain = torquein = 0;
		}
		power = (long)omegain*(long)torquein;

		if (omegain > 0)
			;//this.testFailure(world, x, y, z);

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
		int l = this.getGeneratorLength()/2;
		world.setBlock(x, y, z, 0);
		double dx = x+0.5+this.getFacing().offsetX*l;
		double dz = z+0.5+this.getFacing().offsetZ*l;
		FlyingBlocksExplosion ex = new FlyingBlocksExplosion(world, null, dx, y+0.5, dz, 12);
		ex.doExplosionA();
		ex.doExplosionB(true);
	}

	public static int getGeneratorLength() {
		return 10;
	}

	private void getPower(World world, int x, int y, int z, int meta) {
		int len = this.getGeneratorLength();
		int dx = x+this.getFacing().offsetX*len;
		int dy = y+this.getFacing().offsetY*len;
		int dz = z+this.getFacing().offsetZ*len;

		ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);

		if (r == ReactorTiles.TURBINECORE) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)this.getTileEntity(dx, dy, dz);
			power = te.getPower();
			omegain = te.getOmega();
			torquein = te.getTorque();
		}
		else {
			omegain = torquein = 0;
			power = 0;
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

	public void setFacing(ForgeDirection dir) {
		facingDir = dir;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.GENERATOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += 0.5*ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omegain+1, 2), 1.05);
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
		return from == this.getFacing().getOpposite();
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		int l = this.getGeneratorLength();
		int x1 = xCoord+1+this.getFacing().offsetX*l;
		int z1 = zCoord+1+this.getFacing().offsetZ*l;
		int mx = Math.min(x1, xCoord);
		int mz = Math.min(z1, zCoord);
		int mx2 = Math.max(x1, xCoord);
		int mz2 = Math.max(z1, zCoord);
		return AxisAlignedBB.getAABBPool().getAABB(mx, yCoord-2, mz, mx2, yCoord+3, mz2).expand(6, 6, 6);
	}

}
