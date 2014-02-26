/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.BlockSolenoidMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet.Aim;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntitySolenoidMagnet extends TileEntityReactorBase implements ShaftPowerReceiver {

	public boolean hasMultiBlock = false;
	private boolean checkForToroids = true;

	private int torque;
	private int omega;
	private long power;
	private int iotick;

	public static final int MINOMEGA = 256;
	public static final int MINTORQUE = 32768;

	@Override
	public int getIndex() {
		return ReactorTiles.SOLENOID.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!hasMultiBlock && this.getTicksExisted() == 0) {
			this.checkForMultiBlock(world, x, y, z);
		}
		if (hasMultiBlock && checkForToroids && this.arePowerReqsMet()) {
			this.addToToroids();
		}
		if (!hasMultiBlock || !this.arePowerReqsMet()) {
			this.removeFromToroids();
		}
		//if (this.arePowerReqsMet() && hasMultiBlock && this.getTicksExisted()%4 == 0)
		//	ReactorSounds.FUSION.playSoundAtBlock(world, x, y, z);
		if (omega > 32768) { //violently fail
			world.setBlock(x, y, z, 0);
			FlyingBlocksExplosion ex = new FlyingBlocksExplosion(world, null, x+0.5, y+0.5, z+0.5, 16);
			ex.doExplosionA();
			ex.doExplosionB(true);
		}
	}

	private void checkForMultiBlock(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y-1, z);
		if (id == ReactorBlocks.SOLENOIDMULTI.getBlockID()) {
			BlockSolenoidMulti b = (BlockSolenoidMulti)ReactorBlocks.SOLENOIDMULTI.getBlockVariable();
			if (b.checkForFullMultiBlock(world, x, y-1, z, ForgeDirection.UNKNOWN)) {
				b.onCreateFullMultiBlock(world, x, y-1, z);
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (this.canTurn()) {
			phi += 8;
			if (omega >= 2048)
				phi += 8;
			if (omega >= 16384)
				phi += 8;
			if (omega >= 65536)
				phi += 8;
		}
		else
			phi = 0;
	}

	public boolean canTurn() {
		return hasMultiBlock && power > 0 && torque >= MINTORQUE;
	}

	public boolean arePowerReqsMet() {
		return omega >= MINOMEGA && this.canTurn();
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("multi", hasMultiBlock);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		hasMultiBlock = NBT.getBoolean("multi");

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");
	}

	public void addToToroids() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		x += 14;
		z -= 2;

		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		int c = 0;
		Aim a = Aim.W;
		while ((r == ReactorTiles.MAGNET || r == ReactorTiles.INJECTOR) && c < 38) {
			if (r == ReactorTiles.MAGNET) {
				TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getBlockTileEntity(x, y, z);
				te.hasSolenoid = true;
				a = te.getAim();
			}
			x += a.xOffset;
			z += a.zOffset;
			r = ReactorTiles.getTE(world, x, y, z);
			//ReikaJavaLibrary.pConsole(r+":"+a+":"+a.xOffset+":"+a.zOffset, Side.SERVER);
			c++;
		}
		checkForToroids = false;
	}

	public void removeFromToroids() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		x += 14;
		z -= 2;

		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		int c = 0;
		Aim a = Aim.W;
		while ((r == ReactorTiles.MAGNET || r == ReactorTiles.INJECTOR) && c < 38) {
			if (r == ReactorTiles.MAGNET) {
				TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getBlockTileEntity(x, y, z);
				te.hasSolenoid = false;
				a = te.getAim();
			}
			x += a.xOffset;
			z += a.zOffset;
			r = ReactorTiles.getTE(world, x, y, z);
			//ReikaJavaLibrary.pConsole(r+":"+a+":"+a.xOffset+":"+a.zOffset, Side.SERVER);
			c++;
		}
		checkForToroids = true;
	}

	public boolean canRenderCoil() {
		return hasMultiBlock;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(9, 2, 9);
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
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
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
	public boolean canReadFromBlock(int x, int y, int z) {
		if (x != xCoord || z != zCoord)
			return false;
		return y == yCoord-1 || y == yCoord+1;
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

}
