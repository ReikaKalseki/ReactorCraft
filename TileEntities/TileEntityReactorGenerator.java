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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaBuildCraftHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
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
			ForgeDirection write = this.getFacing().getOpposite();
			TileEntity tile = this.getAdjacentTileEntity(write);
			if (tile instanceof IEnergyHandler) {
				IEnergyHandler rc = (IEnergyHandler)tile;
				if (rc.canConnectEnergy(facingDir)) {
					int rf = this.getGenRF();
					float used = rc.receiveEnergy(facingDir, rf, false);
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
		world.setBlockToAir(x, y, z);
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

		if (r != null && r.isTurbine()) {
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
	public boolean canConnectEnergy(ForgeDirection from) {
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
		return AxisAlignedBB.getBoundingBox(mx, yCoord-2, mz, mx2, yCoord+3, mz2).expand(6, 6, 6);
	}

}
