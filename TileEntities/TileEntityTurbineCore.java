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

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
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

	private void enviroTest(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = this.getBoundingBox(world, x, y, z, meta);
		List<EntityLiving> li = world.getEntitiesWithinAABB(EntityLiving.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLiving e = li.get(i);
			if (this.getOmega() > 0)
				world.createExplosion(null, e.posX, e.posY+e.getEyeHeight()/1F, e.posZ, 0.5F*(float)ReikaMathLibrary.logbase(this.getOmega(), 2), true);
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
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1).expand(6, 6, 6);
	}

}
