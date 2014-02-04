/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerEmitter;

public class TileEntityTurbineCore extends TileEntityReactorBase implements ShaftPowerEmitter {

	private int steam;

	public static final int GEN_OMEGA = 65536; //377 real
	public static final int TORQUE_CAP = 16384;

	public static final long MAX_POWER = 8589934592L; //8.5 GW, biggest in world (Kashiwazaki)

	private int omega;
	private int iotick;

	private int readx;
	private int ready;
	private int readz;
	private int writex;
	private int writey;
	private int writez;

	private Interference inter = null;

	private BlockArray contact = new BlockArray();

	private int damage;

	public int getDamage() {
		return damage;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.TURBINECORE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();
		this.getIOSides(world, x, y, z, meta);
		this.readSurroundings(world, x, y, z, meta);
		this.followHead(world, x, y, z, meta);
		this.enviroTest(world, x, y, z, meta);

		if (thermalTicker.checkCap()) {
			if (steam > 0)
				steam -= steam/32+1;
		}

		if (omega == 0) {
			phi = 0;
			steam = 0;
		}

		steam *= this.getDamageEfficiency();

		//ReikaJavaLibrary.pConsole(String.format("Steam: %d; Omega: %d", steam, omega), Side.SERVER);

		//ReikaJavaLibrary.pConsole(FMLCommonHandler.instance().getEffectiveSide()+":"+steam+":"+omega+":"+String.format("%.3f", ReikaMathLibrary.getThousandBase(this.getGenPower()))+ReikaEngLibrary.getSIPrefix(this.getGenPower()), this.getStage() == 0);
		//ReikaJavaLibrary.pConsole(thermalTicker.getTick()+"/"+thermalTicker.getCap());
		//ReikaJavaLibrary.pConsole(this.getStage()+":"+inter, FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER);
	}

	public ForgeDirection getSteamMovement() {
		switch(this.getBlockMetadata()) {
		case 0:
			return ForgeDirection.WEST;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.SOUTH;
		default:
			return ForgeDirection.DOWN;
		}
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
		//accelTicker.setCap(this.getAccelDelay());
		//ReikaJavaLibrary.pConsole(accelTicker.getCap(), this.getSide() == Side.SERVER && this.getStage() == 0);
		if (up) {
			if (omega < GEN_OMEGA) {
				//ReikaJavaLibrary.pConsole(omega+"->"+(omega+2*(int)(ReikaMathLibrary.logbase(maxspeed, 2))), Side.SERVER);
				omega += 4*(int)ReikaMathLibrary.logbase(GEN_OMEGA+1, 2);
				if (omega > GEN_OMEGA)
					omega = GEN_OMEGA;
			}
		}
		else {
			if (omega > 0) {
				//ReikaJavaLibrary.pConsole(omega+"->"+(omega-omega/128-1), Side.SERVER);
				omega -= omega/256+1;
				//soundtick = 2000;
			}
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
		return (int)(1+ReikaMathLibrary.logbase(omega+1, 2)/20);
	}

	private int getGenTorque() {
		int torque = steam > 0 ? (int)(steam*24) : omega/16+1;
		return omega > 0 ? (int)(torque*this.getEfficiency()) : 0;
	}

	private float getDamageEfficiency() {
		return damage > 0 ? 1F/(damage+1) : 1;
	}

	private long getGenPower() {
		return Math.min(MAX_POWER, (long)this.getGenTorque()*(long)omega);
	}

	private double getEfficiency() {
		switch(this.getNumberStagesTotal()) {
		case 0:
			return 0;
		case 1:
			return 0.025;
		case 2:
			return 0.1;
		case 3:
			return 0.25;
		case 4:
			return 0.5;
		case 5:
			return 1;
		default:
			return 0;
		}
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
		contact.clear();
		if (contact.isEmpty()) {
			this.fillSurroundings(world, x, y, z, meta);
		}
		boolean canAccel = false;
		inter = null;
		for (int i = 0; i < contact.getSize(); i++) {
			int[] xyz = contact.getNthBlock(i);
			if (ReikaMathLibrary.py3d(x-xyz[0], y-xyz[1], z-xyz[2]) <= 1+this.getStage()/2) {
				int id2 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
				int meta2 = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (!ReikaWorldHelper.softBlocks(world, xyz[0], xyz[1], xyz[2]) && !(xyz[0] == x && xyz[1] == y && xyz[2] == z)) {
					//ReikaJavaLibrary.pConsole(Arrays.toString(xyz)+":"+this, this.getStage() == 4 && this.getSide() == Side.SERVER);
					phi = 0;
					omega = 0;
					if (inter == null || inter.maxSpeed > Interference.JAM.maxSpeed)
						inter = Interference.JAM;
				}
				else if (Block.blocksList[id2] instanceof BlockFluid) {
					if (inter == null || inter.maxSpeed > Interference.FLUID.maxSpeed)
						inter = Interference.FLUID;
				}
				else if (this.getStage() == 0 && id2 == ReactorBlocks.STEAM.getBlockID()) {
					//ReikaJavaLibrary.pConsole(meta2);
					if ((meta2&2) != 0) {
						int newmeta = 1+(meta2&4);
						//ReikaJavaLibrary.pConsole(meta2+":"+newmeta+":"+((newmeta&4) != 0));
						//world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], newmeta, 3);
						if ((meta2&4) != 0) {
							steam += 2;
							//omega = Math.min(omega+8, GEN_OMEGA);
						}
						else
							steam++;
						canAccel = true;
					}
				}
			}
		}
		this.updateSpeed(canAccel);
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
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (this.getOmega() > 0 && ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5) < r) {
				if (!world.isRemote) {
					Explosion exp = world.createExplosion(null, e.posX, e.posY+e.getEyeHeight()/1F, e.posZ, 2, false);
					e.attackEntityFrom(DamageSource.setExplosionSource(exp), 2);
					this.breakTurbine();
				}
				e.motionX += 0.4*(e.posX-x-0.5+0.1)+rand.nextDouble()*0.1;
				e.motionY += 0.4*(e.posY-y-0.5+0.1);
				e.motionZ += 0.4*(e.posZ-z-0.5+0.1)+rand.nextDouble()*0.1;
				if (inter == null || inter.maxSpeed > Interference.MOB.maxSpeed)
					inter = Interference.MOB;
			}
		}

		if (inter != null) {
			omega = Math.min(omega, inter.maxSpeed);
		}
	}

	private void breakTurbine() {
		damage++;
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

	private void followHead(World world, int x, int y, int z, int meta) {
		int id = world.getBlockId(readx, ready, readz);
		int bmeta = world.getBlockMetadata(readx, ready, readz);
		if (id == this.getTileEntityBlockID() && bmeta == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)world.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == x && tile.writey == y && tile.writez == z) {
				//omega = (omega+tile.omega)/2;
				omega = tile.omega;
				phi = tile.phi;
				steam = tile.steam;
				//return;
			}
		}
		int id2 = world.getBlockId(writex, writey, writez);
		int meta2 = world.getBlockMetadata(writex, writey, writez);
		if (id2 == this.getTileEntityBlockID() && meta2 == ReactorTiles.TURBINECORE.getBlockMetadata()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)world.getBlockTileEntity(writex, writey, writez);
			if (tile.readx == x && tile.ready == y && tile.readz == z) {
				if (tile.inter != null)
					inter = tile.inter;
			}
		}
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

		omega = NBT.getInteger("speed");
		steam = NBT.getInteger("steamlevel");

		inter = Interference.get(NBT.getInteger("blocked"));

		damage = NBT.getInteger("dmg");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("speed", omega);
		NBT.setInteger("steamlevel", steam);

		NBT.setInteger("dmg", damage);

		if (inter != null)
			NBT.setInteger("blocked", inter.ordinal());
		else
			NBT.setInteger("blocked", -1);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1).expand(6, 6, 6);
	}

	enum Interference {
		JAM(0),
		FLUID(512),
		MOB(4096);

		public final int maxSpeed;

		public static final Interference[] list = values();

		private Interference(int max) {
			maxSpeed = max;
		}

		public static Interference get(int o) {
			if (o < 0)
				return null;
			return list[o];
		}
	}

}
