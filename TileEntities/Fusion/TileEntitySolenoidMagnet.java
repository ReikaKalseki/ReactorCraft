/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Auxiliary.NeutronTile;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.Multi.BlockSolenoidMulti;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet.Aim;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;

public class TileEntitySolenoidMagnet extends TileEntityReactorBase implements ReactorPowerReceiver, MultiBlockTile, NeutronTile {

	private boolean hasMultiBlock = false;
	private boolean checkForToroids = true;

	private int torque;
	private int omega;
	private long power;
	private int iotick;
	private float speed = 0;

	public static final int MINOMEGA = 256;
	public static final int MAX_SPEED = 8192;
	public static final int MINTORQUE = 32768;
	private static final int MAX_SAFE_SPEED = 30;

	@Override
	public int getIndex() {
		return ReactorTiles.SOLENOID.ordinal();
	}

	public boolean hasMultiBlock() {
		return hasMultiBlock;
	}

	public void setHasMultiBlock(boolean has) {
		if (hasMultiBlock && !has)
			this.testBreakageFailure();
		hasMultiBlock = has;
	}

	private void testBreakageFailure() {
		if (omega > 32) {
			this.fail(worldObj, xCoord, yCoord, zCoord);
		}
	}

	private void fail(World world, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
		new FlyingBlocksExplosion(this, 12).doExplosion();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!PowerTransferHelper.checkPowerFrom(this, ForgeDirection.DOWN)) {
			this.noInputMachine();
		}

		//this.animateWithTick(world, x, y, z);

		float v = 0.1F;
		if (this.canTurn()) {
			if (speed > MAX_SAFE_SPEED)
				v *= 3;
			speed = Math.min(speed+v, this.getMaxRenderSpeed());
		}
		else {
			speed = Math.max(0, speed-v);
		}

		if (DragonAPICore.debugtest) {
			hasMultiBlock = true;
			torque = MINTORQUE*8;
			omega = 4096;
			power = (long)omega*(long)torque;
		}

		if (ReactorCraft.logger.shouldDebug()) {
			if (world.isRemote)
				ReactorCraft.logger.log("Clientside "+this+" receiving "+torque+" Nm @ "+omega+" rad/s. Phi="+phi);
			else
				ReactorCraft.logger.log("Serverside "+this+" receiving "+torque+" Nm @ "+omega+" rad/s.");
		}

		if (DragonAPICore.debugtest || hasMultiBlock && checkForToroids && this.arePowerReqsMet()) {
			this.addToToroids();
		}
		if (!hasMultiBlock || !this.arePowerReqsMet()) {
			this.removeFromToroids();
		}
		if (hasMultiBlock && torque >= MINTORQUE && speed > MAX_SAFE_SPEED*4) { //violently fail
			world.setBlockToAir(x, y, z);
			new FlyingBlocksExplosion(this, 16).doExplosion();
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (!hasMultiBlock) {
			this.checkForMultiBlock(world, x, y, z);
		}
	}

	private void checkForMultiBlock(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y-1, z);
		if (id == ReactorBlocks.SOLENOIDMULTI.getBlockInstance()) {
			BlockSolenoidMulti b = (BlockSolenoidMulti)ReactorBlocks.SOLENOIDMULTI.getBlockInstance();
			if (b.checkForFullMultiBlock(world, x, y-1, z, ForgeDirection.UNKNOWN)) {
				b.onCreateFullMultiBlock(world, x, y-1, z);
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (hasMultiBlock) {
			phi += speed;
		}
		else
			phi = 0;
	}

	private float getMaxRenderSpeed() {
		if (omega > MAX_SPEED)
			return 512;
		else if (omega >= 4096)
			return MAX_SAFE_SPEED;
		else if (omega >= 2048)
			return 20F;
		else if (omega >= 1024)
			return 7.5F;
		else
			return 4.5F;
	}

	public boolean canTurn() {
		return hasMultiBlock && power > 0 && torque >= MINTORQUE;
	}

	public boolean arePowerReqsMet() {
		return omega >= MINOMEGA && this.canTurn();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("multi", hasMultiBlock);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

		NBT.setFloat("phi", phi);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasMultiBlock = NBT.getBoolean("multi");

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");

		phi = NBT.getFloat("phi");
	}

	public void addToToroids() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		x += 14; //radius of tokamak
		z -= 2;

		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		int c = 0;
		Aim a = Aim.W;
		while ((r == ReactorTiles.MAGNET || r == ReactorTiles.INJECTOR) && c <= 38) {
			if (r == ReactorTiles.MAGNET) {
				TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getTileEntity(x, y, z);
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
				TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getTileEntity(x, y, z);
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
	public boolean canReadFrom(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
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
	public int getMinTorque(int available) {
		return MINTORQUE;
	}

	@Override
	public int getMinTorque() {
		return MINTORQUE;
	}

	@Override
	public int getMinSpeed() {
		return MINOMEGA;
	}

	@Override
	public long getMinPower() {
		return 1;
	}

	@Override
	public int getUpdatePacketRadius() {
		return 96; //much larger visually
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
				if (b instanceof BlockMultiBlock) {
					((BlockMultiBlock)b).breakMultiBlock(worldObj, dx, dy, dz);
				}
			}
		}
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

}
