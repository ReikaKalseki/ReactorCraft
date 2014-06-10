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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorSounds;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Screwdriverable;
import Reika.RotaryCraft.API.ShaftPowerEmitter;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityTurbineCore extends TileEntityReactorBase implements ShaftPowerEmitter, Screwdriverable {

	protected int steam;

	private int omega;
	private int iotick;

	private int readx;
	private int ready;
	private int readz;
	private int writex;
	private int writey;
	private int writez;

	public static final int GEN_OMEGA = 65536;
	public static final int TORQUE_CAP = 32768;

	private Interference inter = null;

	private BlockArray contact = new BlockArray();

	private int damage;

	protected boolean hasMultiBlock = !this.needsMultiblock();
	private boolean readyForMultiBlock = false;

	public void markForMulti() {
		readyForMultiBlock = true;
	}

	public void setHasMultiBlock(boolean has) {
		hasMultiBlock = has ? readyForMultiBlock : has;
		readyForMultiBlock = false;
	}

	public boolean hasMultiBlock() {
		return hasMultiBlock;
	}

	private StepTimer soundTimer = new StepTimer(41);

	private int stage;

	public int getDamage() {
		return damage;
	}

	public boolean needsMultiblock() {
		return false;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.TURBINECORE.ordinal();
	}

	@Override
	public final void updateEntity(World world, int x, int y, int z, int meta) {
		this.getIOSides(world, x, y, z, meta);

		if (!hasMultiBlock) {
			omega = 0;
			phi = 0;
			steam = 0;
			return;
		}

		thermalTicker.update();
		soundTimer.update();

		stage = this.getStage();
		this.readSurroundings(world, x, y, z, meta);
		this.followHead(world, x, y, z, meta);
		this.enviroTest(world, x, y, z, meta);

		readyForMultiBlock = false;

		//ReikaJavaLibrary.pConsole(steam, stage == 6 && this.getSide() == Side.SERVER);
		if (steam > 0) {
			this.dumpSteam(world, x, y, z, meta);
			if (thermalTicker.checkCap()) {
				steam -= this.getConsumedSteam();
			}
		}

		if (omega == 0) {
			phi = 0;
			steam = 0;
		}
		else {
			if (soundTimer.checkCap() && stage == 0)
				ReactorSounds.TURBINE.playSoundAtBlock(world, x, y, z, 2F, 1F);
		}

		steam *= this.getDamageEfficiency();

		if (this.getGenPower() >= 1000000000L) {
			ReactorAchievements.GIGATURBINE.triggerAchievement(this.getPlacer());
		}

		TileEntity tg = this.getTileEntity(writex, writey, writez);
		if (tg instanceof ShaftPowerReceiver) {
			ShaftPowerReceiver rec = (ShaftPowerReceiver)tg;
			rec.setOmega(this.getOmega());
			rec.setTorque(this.getTorque());
			rec.setPower(this.getPower());
		}
	}

	protected void dumpSteam(World world, int x, int y, int z, int meta) {

	}

	protected int getMaxStage() {
		return 4;
	}

	protected final int getConsumedSteam() {
		return steam/32+1;
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

	public int getMaxTorque() {
		return 32768;
	}

	public int getMaxSpeed() {
		return 65536;
	}

	private void updateSpeed(boolean up) {
		if (up) {
			int max = this.getMaxSpeed();
			if (omega < max) {
				omega += 4*ReikaMathLibrary.logbase(max+1, 2);
				if (omega > max)
					omega = max;
			}
		}
		else {
			if (omega > 0) {
				omega -= omega/256+1;
			}
		}
	}

	public final boolean isAtEndOFLine() {
		if (ReactorTiles.getTE(worldObj, readx, ready, readz) == this.getMachine()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == xCoord && tile.writey == yCoord && tile.writez == zCoord) {
				return false;
			}
		}
		return true;
	}

	private int getAccelDelay() {
		return 1+(int)ReikaMathLibrary.logbase(omega+1, 2)/20;
	}

	protected final int getGenTorque() {
		int torque = steam > 0 ? (int)(steam*24) : omega/16+1;
		int ret = omega > 0 ? (int)(torque*this.getEfficiency()) : 0;
		return Math.min(ret, this.getMaxTorque());
	}

	private float getDamageEfficiency() {
		return damage > 0 ? 1F/(damage+1) : 1;
	}

	protected final long getGenPower() {
		return (long)this.getGenTorque()*(long)omega;
	}

	protected double getEfficiency() {
		switch(this.getNumberStagesTotal()) {
		case 0:
			return 0.025;
		case 1:
			return 0.1;
		case 2:
			return 0.25;
		case 3:
			return 0.5;
		case 4:
			return 1;
		default:
			return 0;
		}
	}

	public final int getStage() {
		if (ReactorTiles.getTE(worldObj, readx, ready, readz) == this.getMachine()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == xCoord && tile.writey == yCoord && tile.writez == zCoord) {
				int stage = tile.getStage();
				if (stage == this.getMaxStage())
					return this.getMaxStage();
				else
					return stage+1;
			}
		}
		return 0;
	}

	protected AxisAlignedBB getBoundingBox(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1);
		int r = 2+stage;
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

	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		int id = world.getBlockId(x, y-1, z);
		int meta2 = world.getBlockMetadata(x, y-1, z);
		boolean canAccel = false;
		if (id == ReactorBlocks.STEAM.getBlockID() && stage == 0) {
			if ((meta2&2) != 0 && (meta2&8) == 0) {
				int newmeta = 1+(meta2&4);
				if ((meta2&4) != 0) {
					steam += 2;
				}
				else
					steam++;
				canAccel = true;
			}
		}
		return canAccel;
	}

	private void readSurroundings(World world, int x, int y, int z, int meta) {
		contact.clear();
		if (contact.isEmpty()) {
			this.fillSurroundings(world, x, y, z, meta);
		}
		inter = null;
		for (int i = 0; i < contact.getSize(); i++) {
			int[] xyz = contact.getNthBlock(i);
			if (ReikaMathLibrary.py3d(x-xyz[0], y-xyz[1], z-xyz[2]) <= this.getRadius()) {
				int id2 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
				int meta2 = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (!ReikaWorldHelper.softBlocks(world, xyz[0], xyz[1], xyz[2]) && !(xyz[0] == x && xyz[1] == y && xyz[2] == z) && id2 != ReactorBlocks.TURBINEMULTI.getBlockID()) {
					phi = 0;
					omega = 0;
					if (inter == null || inter.maxSpeed > Interference.JAM.maxSpeed)
						inter = Interference.JAM;
				}
				else if (Block.blocksList[id2] instanceof BlockFluid || Block.blocksList[id2] instanceof BlockFluidBase) {
					if (inter == null || inter.maxSpeed > Interference.FLUID.maxSpeed)
						inter = Interference.FLUID;
				}
			}
		}
		boolean accel = this.intakeSteam(world, x, y, z, meta);
		this.updateSpeed(accel);
	}

	protected double getRadius() {
		return 1.5+this.getStage()/2;
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
		int r = 2+stage/2;
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (this.getOmega() > 0 && ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY-y-0.5, e.posZ-z-0.5) < r) {
				if (!(e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode)) {
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
		}

		if (inter != null) {
			omega = Math.min(omega, inter.maxSpeed);
		}
	}

	protected void breakTurbine() {
		damage++;
	}

	public final int getNumberStagesTotal() {
		if (ReactorTiles.getTE(worldObj, writex, writey, writez) == this.getMachine()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(writex, writey, writez);
			if (tile.readx == xCoord && tile.ready == yCoord && tile.readz == zCoord)
				return tile.getNumberStagesTotal();
		}
		return this.getStage();
	}

	private void followHead(World world, int x, int y, int z, int meta) {
		if (ReactorTiles.getTE(worldObj, readx, ready, readz) == this.getMachine()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)world.getBlockTileEntity(readx, ready, readz);
			if (tile.writex == x && tile.writey == y && tile.writez == z) {
				//omega = (omega+tile.omega)/2;
				omega = tile.omega;
				phi = tile.phi;
				steam = tile.steam;
				//return;
			}
		}
		if (ReactorTiles.getTE(worldObj, writex, writey, writez) == this.getMachine()) {
			TileEntityTurbineCore tile = (TileEntityTurbineCore)worldObj.getBlockTileEntity(writex, writey, writez); //write!
			if (tile.readx == x && tile.ready == y && tile.readz == z) {
				if (tile.inter != null)
					inter = tile.inter;
			}
		}
	}

	@Override
	protected final void animateWithTick(World world, int x, int y, int z) {
		iotick -= 8;
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += this.getAnimationSpeed()*ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	protected double getAnimationSpeed() {
		return 0.2F;
	}

	@Override
	public final int getOmega() {
		return this.isEmitting() ? omega : 0;
	}

	@Override
	public final int getTorque() {
		return this.getGenTorque();
	}

	@Override
	public final long getPower() {
		return this.getGenPower();
	}

	@Override
	public final int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public final void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public final boolean canWriteToBlock(int x, int y, int z) {
		return x == writex && y == writey && z == writez;
	}

	@Override
	public final boolean isEmitting() {
		return this.getGenPower() > 0;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		omega = NBT.getInteger("speed");
		steam = NBT.getInteger("steamlevel");

		inter = Interference.get(NBT.getInteger("blocked"));

		damage = NBT.getInteger("dmg");

		if (this.needsMultiblock())
			hasMultiBlock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("speed", omega);
		NBT.setInteger("steamlevel", steam);

		NBT.setInteger("dmg", damage);

		if (inter != null)
			NBT.setInteger("blocked", inter.ordinal());
		else
			NBT.setInteger("blocked", -1);

		if (this.needsMultiblock())
			NBT.setBoolean("multi", hasMultiBlock);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1).expand(6, 6, 6);
	}

	private static enum Interference {
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

	@Override
	public final long getMaxPower() {
		return this.getGenPower();
	}

	@Override
	public final long getCurrentPower() {
		return this.getGenPower();
	}

	@Override
	public final int getEmittingX() {
		return writex;
	}

	@Override
	public final int getEmittingY() {
		return writey;
	}

	@Override
	public final int getEmittingZ() {
		return writez;
	}

	@Override
	public final boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public final boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		int meta = this.getBlockMetadata();
		this.setBlockMetadata(meta < 3 ? meta+1 : 0);
		return true;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 4*super.getMaxRenderDistanceSquared();
	}

}
