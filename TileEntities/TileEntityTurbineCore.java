/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.BlockArray;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerEmitter;

public class TileEntityTurbineCore extends TileEntityReactorBase implements ShaftPowerEmitter {

	private long storedEnergy;

	public static final int GEN_OMEGA = 512; //377 real
	public static final int TORQUE_CAP = 8388608;

	public static final long MAX_POWER = 8589934592L; //8.5 GW, biggest in world (Kashiwazaki)

	private int omega;
	private int iotick;

	private StepTimer accelTicker = new StepTimer(1);

	private int readx;
	private int ready;
	private int readz;
	private int writex;
	private int writey;
	private int writez;

	private BlockArray contact = new BlockArray();
	private boolean isFull;

	@Override
	public int getIndex() {
		return ReactorTiles.TURBINECORE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getIOSides(world, x, y, z, meta);
		this.accumulateEnergy(world, x, y, z, meta);
		if (this.isAtEndOFLine())
			this.useEnergy();
		this.readSurroundings(world, x, y, z, meta);
		this.enviroTest(world, x, y, z, meta);
	}

	private void getIOSides(World world, int x, int y, int z, int meta) {
		switch(meta) {
		case 0:
			readx = x+1;
			ready = y;
			readz = z;
			writex = x-1;
			writey = y;
			writez = z;
			break;
		case 1:
			readx = x-1;
			ready = y;
			readz = z;
			writex = x+1;
			writey = y;
			writez = z;
			break;
		case 2:
			readx = x;
			ready = y;
			readz = z+1;
			writex = x;
			writey = y;
			writez = z-1;
			break;
		case 3:
			readx = x;
			ready = y;
			readz = z-1;
			writex = x;
			writey = y;
			writez = z+1;
			break;
		}
	}

	private void updateSpeed(boolean up) {
		accelTicker.update();
		accelTicker.setCap(this.getAccelDelay());
		if (up) {
			if (accelTicker.checkCap())
				omega = ReikaMathLibrary.extrema(omega+1, GEN_OMEGA, "absmin");
		}
		else {
			omega = ReikaMathLibrary.extrema(omega-1, 0, "max");
		}
	}

	public boolean isAtEndOFLine() {
		int id = worldObj.getBlockId(readx, ready, readz);
		int meta = worldObj.getBlockMetadata(readx, ready, readz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == xCoord && tile.writey == yCoord && tile.writez == zCoord) {
				return false;
			}
		}
		return true;
	}

	private int getAccelDelay() {
		return (int)(1+ReikaMathLibrary.logbase(omega+1, 2));
	}

	private void useEnergy() {
		storedEnergy -= this.getConsumedEnergy();
	}

	private double getConsumedEnergy() {
		return storedEnergy/400D;
	}

	private int getGenTorque() {
		return omega > 0 ? (int)(this.getGenPower()/GEN_OMEGA) : 0;
	}

	private long getGenPower() {
		return (long) Math.min(MAX_POWER, storedEnergy*this.getEfficiency()*((double)omega/GEN_OMEGA));
	}

	private double getEfficiency() {
		return Math.min(0.2*this.getNumberStagesTotal(), 1);
	}

	public int getStage() {
		int id = worldObj.getBlockId(readx, ready, readz);
		int meta = worldObj.getBlockMetadata(readx, ready, readz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == xCoord && tile.writey == yCoord && tile.writez == zCoord) {
				int stage = tile.getStage();
				if (stage == 4)
					return 4;
				else
					return stage+1;
			}
		}
		return 0;
	}

	private AxisAlignedBB getBoundingBox(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1);
		int r = 2+this.getStage();
		switch(meta) {
		case 2:
		case 3:
			box = box.expand(r/2, r/2, 0);
			break;
		case 0:
		case 1:
			box = box.expand(0, r/2, r/2);
			break;
		}
		return box;
	}

	private void readSurroundings(World world, int x, int y, int z, int meta) {
		if (contact.isEmpty()) {
			this.fillSurroundings(world, x, y, z, meta);
		}
		for (int i = 0; i < contact.getSize(); i++) {
			int[] xyz = contact.getNthBlock(i);
			if (ReikaMathLibrary.py3d(x-xyz[0], y-xyz[1], z-xyz[2]) <= 1+this.getStage()/2) {
				int id2 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
				if (!ReikaWorldHelper.softBlocks(world, xyz[0], xyz[1], xyz[2])) {
					phi = 0;
					omega = 0;
					storedEnergy = 0;
				}
				else if (Block.blocksList[id2] instanceof BlockFluid) {
					phi = phi/1.25F;
					omega = 1+(int)(omega/1.25F);
				}
				else if (id2 != ReactorBlocks.STEAM.getBlockID()) {
					phi = phi/1.0625F;
					omega = 1+(int)(omega/1.0625F);
				}
			}
		}
		if (this.canCreateSteam(world, x, y, z)) {
			int[] xyz = contact.getNextAndMoveOn();
			int id3 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
			if (id3 != ReactorBlocks.STEAM.getBlockID() && (id3 == 0 || Block.blocksList[id3].isAirBlock(world, xyz[0], xyz[1], xyz[2]))) {
				world.setBlock(xyz[0], xyz[1], xyz[2], ReactorBlocks.STEAM.getBlockID(), 1, 3);
				storedEnergy -= 1000;
				isFull = false;
			}
			else
				isFull = true;
		}
		else {
			int[] xyz = contact.getNextAndMoveOn();
			int id3 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
			int meta3 = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			if (id3 == ReactorBlocks.STEAM.getBlockID() && (meta3 == 1 || omega == 0 || storedEnergy == 0)) {
				world.setBlock(xyz[0], xyz[1], xyz[2], 0);
				isFull = false;
			}
		}
		if (omega <= 1)
			phi = 0;
	}

	private boolean canCreateSteam(World world, int x, int y, int z) {
		int id = world.getBlockId(readx, ready, readz);
		int meta = world.getBlockMetadata(readx, ready, readz);
		if (id != this.getTileEntityBlockID() && id != ReactorTiles.WATERLINE.getBlockID())
			return false;
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)world.getBlockTileEntity(readx, ready, readz);
			return te.isFull && storedEnergy > 2000 && omega > 0;
		}
		else {
			return meta == ReactorTiles.WATERLINE.getBlockMetadata() && storedEnergy > 2000 && omega > 0;
		}
	}

	private void fillSurroundings(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1);
		int r = 3;
		switch(meta) {
		case 2:
		case 3:
			for (int i = x-r; i <= x+r; i++) {
				for (int j = y-r; j <= y+r; j++) {
					if (x != i || y != j)
						contact.addBlockCoordinate(i, j, z);
				}
			}
			break;
		case 0:
		case 1:
			for (int i = z-r; i <= z+r; i++) {
				for (int j = y-r; j <= y+r; j++) {
					if (z != i || y != j)
						contact.addBlockCoordinate(x, j, i);
				}
			}
			break;
		}

	}

	private void enviroTest(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = this.getBoundingBox(world, x, y, z, meta);
		int r = 2+this.getStage()/2;
		List<EntityLiving> li = world.getEntitiesWithinAABB(EntityLiving.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLiving e = li.get(i);
			if (this.getOmega() > 0 && ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5) < r) {
				Explosion exp = world.createExplosion(null, e.posX, e.posY+e.getEyeHeight()/1F, e.posZ, 2, false);
				e.motionX += 0.4*(e.posX-x-0.5)+par5Random.nextDouble()*0.1;
				e.motionY += 0.4*(e.posY-y-0.5);
				e.motionZ += 0.4*(e.posZ-z-0.5)+par5Random.nextDouble()*0.1;
				this.breakTurbine();
				e.attackEntityFrom(DamageSource.setExplosionSource(exp), 2);
			}
		}
		int id = world.getBlockId(readx, ready, readz);
		int bmeta = world.getBlockMetadata(readx, ready, readz);
		if (id == ReactorTiles.TURBINECORE.getBlockID() && bmeta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)world.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == x && tile.writey == y && tile.writez == z) {
				if (phi != tile.phi) {
					ReikaParticleHelper.CRITICAL.spawnAroundBlock(world, x, y, z, 5);
					if (par5Random.nextInt(3) == 0)
						ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "mob.blaze.hit");
				}
			}
		}
	}

	private void breakTurbine() {

	}

	public int getNumberStagesTotal() {
		int id = worldObj.getBlockId(readx, ready, readz);
		int meta = worldObj.getBlockMetadata(readx, ready, readz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == xCoord && tile.writey == yCoord && tile.writez == zCoord) {
				int stages = tile.getNumberStagesTotal();
				return stages+1;
			}
		}
		return 1;
	}

	private void accumulateEnergy(World world, int x, int y, int z, int meta) {
		int id = world.getBlockId(readx, ready, readz);
		int bmeta = world.getBlockMetadata(readx, ready, readz);
		if (id == ReactorTiles.WATERLINE.getBlockID() && bmeta == ReactorTiles.WATERLINE.getBlockMetadata()) {
			TileEntityWaterLine te = (TileEntityWaterLine)world.getBlockTileEntity(readx, ready, readz);
			storedEnergy += te.removeEnergy();
			this.updateSpeed(storedEnergy > 0);
			return;

		}
		if (id == this.getTileEntityBlockID() && bmeta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)world.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == x && tile.writey == y && tile.writez == z) {
				storedEnergy = tile.storedEnergy;
				omega = tile.omega;
				phi = tile.phi;
				return;
			}
		}
		this.updateSpeed(false);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		iotick -= 8;
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += 0.3F*ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	public double getEnergy() {
		return storedEnergy;
	}

	protected double removeEnergy() {
		double E = storedEnergy;
		storedEnergy = 0;
		return E;
	}

	@Override
	public int getOmega() {
		return this.isEmitting() ? omega : 0;
	}

	@Override
	public int getTorque() {
		return this.getGenTorque();
	}

	@Override
	public long getPower() {
		return this.getGenPower();
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
	public int getMachineX() {
		return xCoord;
	}

	@Override
	public int getMachineY() {
		return yCoord;
	}

	@Override
	public int getMachineZ() {
		return zCoord;
	}

	@Override
	public boolean canWriteToBlock(int x, int y, int z) {
		return x == writex && y == writey && z == writez;
	}

	@Override
	public boolean isEmitting() {
		return this.getGenPower() > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		storedEnergy = NBT.getLong("energy");
		omega = NBT.getInteger("speed");
		isFull = NBT.getBoolean("full");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setLong("energy", storedEnergy);
		NBT.setInteger("speed", omega);
		NBT.setBoolean("full", isFull);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1).expand(6, 6, 6);
	}

}
