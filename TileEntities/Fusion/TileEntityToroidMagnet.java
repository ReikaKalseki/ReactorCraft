/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Screwdriverable;
import Reika.RotaryCraft.API.Shockable;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Weaponry.TileEntityVanDeGraff;

public class TileEntityToroidMagnet extends TileEntityReactorBase implements Screwdriverable, Shockable {

	//0 is +x(E), rotates to -z(N)
	private Aim aim = Aim.N;


	private int alpha = 512;

	protected boolean hasSolenoid = false;

	private int charge = 0;

	private StepTimer chargeTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNET.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!hasSolenoid && this.getTicksExisted() == 0) {
			this.checkSurroundingMagnetsAndCopySolenoidState();
		}

		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		List<EntityPlasma> li = world.getEntitiesWithinAABB(EntityPlasma.class, box);
		int[] tg = this.getTarget();
		for (int i = 0; i < li.size(); i++) {
			EntityPlasma e = li.get(i);
			if (this.canAffect(e)) {
				e.setTarget(tg[0], tg[2]);
				e.magnetOrdinal = this.getOrdinal();
			}
			else {
				ReactorAchievements.ESCAPE.triggerAchievement(this.getPlacer());
			}
		}
		this.collectCharge(world, x, y, z);

		chargeTimer.update();
		if ((chargeTimer.getTick()&4) != 0)
			this.distributeCharge(world, x, y, z);
		if (chargeTimer.checkCap()) {
			this.updateCharge(world, x, y, z);
		}

		//if (this.getTicksExisted() == 0)
		//	this.clearArea(world, x, y, z);
		//ReikaJavaLibrary.pConsole(aim, !hasSolenoid && this.getSide() == Side.SERVER);
	}

	private void checkSurroundingMagnetsAndCopySolenoidState() {
		Aim a = this.getAim();
		int dx = xCoord+a.xOffset;
		int dz = zCoord+a.zOffset;
		ReactorTiles r = ReactorTiles.getTE(worldObj, dx, yCoord, dz);
		if (r == ReactorTiles.MAGNET) {
			TileEntityToroidMagnet te = (TileEntityToroidMagnet)worldObj.getBlockTileEntity(dx, yCoord, dz);
			hasSolenoid = te.hasSolenoid;
		}
		else if (r == ReactorTiles.INJECTOR) {
			dx += a.xOffset;
			dz += a.zOffset;
			TileEntityToroidMagnet te = (TileEntityToroidMagnet)worldObj.getBlockTileEntity(dx, yCoord, dz);
			if (te != null) {
				hasSolenoid = te.hasSolenoid;
			}
		}
	}

	private void collectCharge(World world, int x, int y, int z) {
		MachineRegistry m = MachineRegistry.getMachine(world, x, y-3, z);
		if (m == MachineRegistry.VANDEGRAFF) {
			TileEntityVanDeGraff te = (TileEntityVanDeGraff)world.getBlockTileEntity(x, y-3, z);
			te.dischargeToBlock(x, y, z, this);
		}
	}

	private boolean distributeCharge(World world, int x, int y, int z) {
		Aim a = this.getAim();
		int dx = x+a.xOffset;
		int dz = z+a.zOffset;
		ReactorTiles r = ReactorTiles.getTE(world, dx, y, dz);
		if (r == ReactorTiles.MAGNET) {
			TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getBlockTileEntity(dx, y, dz);
			int dC = charge-te.charge;
			if (dC > 0) {
				te.charge += dC/4;
				charge -= dC/4;
				EntityDischarge e1 = new EntityDischarge(world, x+0.5, y+2.25, z+0.5, charge, te.xCoord+0.5, te.yCoord+2.25, te.zCoord+0.5);
				EntityDischarge e2 = new EntityDischarge(world, x+0.5, y-1.25, z+0.5, charge, te.xCoord+0.5, te.yCoord-1.25, te.zCoord+0.5);

				float ang = this.getAngle();
				float ang2 = te.getAngle();
				double fx = 1.75*Math.cos(Math.toRadians(ang));
				double fz = 1.75*Math.sin(Math.toRadians(ang));
				double fx2 = 1.75*Math.cos(Math.toRadians(ang2));
				double fz2 = 1.75*Math.sin(Math.toRadians(ang2));

				EntityDischarge e3 = new EntityDischarge(world, x+0.5+fx, y+0.5, z+0.5+fz, charge, te.xCoord+0.5+fx2, te.yCoord+0.5, te.zCoord+0.5+fz2);
				EntityDischarge e4 = new EntityDischarge(world, x+0.5-fx, y+0.5, z+0.5-fz, charge, te.xCoord+0.5-fx2, te.yCoord+0.5, te.zCoord+0.5-fz2);
				if (!world.isRemote && this.shouldSpawnSparks(world)) {
					world.spawnEntityInWorld(e1);
					world.spawnEntityInWorld(e2);
					world.spawnEntityInWorld(e3);
					world.spawnEntityInWorld(e4);
				}
			}
		}
		else if (r == ReactorTiles.INJECTOR) {
			dx += a.xOffset;
			dz += a.zOffset;
			TileEntityToroidMagnet te = (TileEntityToroidMagnet)world.getBlockTileEntity(dx, y, dz);
			if (te != null) {
				int dC = charge-te.charge;
				if (dC > 0) {
					te.charge += dC/4;
					charge -= dC/4;
					EntityDischarge e1 = new EntityDischarge(world, x+0.5, y+2, z+0.5, charge, te.xCoord+0.5, te.yCoord+2, te.zCoord+0.5);
					EntityDischarge e2 = new EntityDischarge(world, x+0.5, y-1, z+0.5, charge, te.xCoord+0.5, te.yCoord-1, te.zCoord+0.5);
					if (!world.isRemote && this.shouldSpawnSparks(world)) {
						world.spawnEntityInWorld(e1);
						world.spawnEntityInWorld(e2);
					}
				}
			}
		}
		return false;
	}

	private boolean shouldSpawnSparks(World world) {
		int rate = this.getPacketDelay();
		if (rate < 5)
			return true;
		else if (rate < 10)
			return world.getTotalWorldTime()%2 == 0;
		else
			return (world.getTotalWorldTime()&3) == 0;
	}

	private void updateCharge(World world, int x, int y, int z) {
		if (charge <= 1)
			charge = 0;
		else
			charge *= 0.8;
	}

	private void clearArea(World world, int x, int y, int z) {
		int r = 2;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int id = world.getBlockId(x+i, y+j, z+k);
					if (id == Block.grass.blockID || id == Block.dirt.blockID) {
						world.setBlock(x+i, y+j, z+k, 0);
					}
				}
			}
		}
	}

	private boolean canAffect(EntityPlasma e) {
		if (!hasSolenoid)
			return false;
		if (charge <= 0)
			return false;
		int o = this.getOrdinal();
		int p = e.magnetOrdinal;
		if (p == -1)
			return o%8 == 0;
		if (o > 30) {
			return p > 28 || p < 1;
		}
		if (p > 30) {
			return o > 28 || o < 1;
		}
		return Math.abs(p-o) <= 2;
	}

	public int[] getTarget() {
		int[] tg = new int[3];
		tg[0] = xCoord+this.getAim().xOffset;
		tg[2] = zCoord+this.getAim().zOffset;
		tg[1] = yCoord;
		return tg;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (alpha > 0)
			alpha -= 8;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		aim = this.getAim(NBT.getInteger("aim"));
		alpha = NBT.getInteger("al");
		hasSolenoid = NBT.getBoolean("solenoid");

		charge = NBT.getInteger("chg");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("aim", this.getAim().ordinal());
		NBT.setInteger("al", alpha);
		NBT.setBoolean("solenoid", hasSolenoid);

		NBT.setInteger("chg", charge);
	}

	public Aim getAim() {
		return aim != null ? aim : Aim.N;
	}

	private Aim getAim(int o) {
		return (o > 0 && o < Aim.list.length) ? Aim.list[o] : Aim.N;
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		alpha = 512;
		this.decrementAim();
		return true;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		alpha = 512;
		this.incrementAim();
		return true;
	}

	public float getAngle() {
		return this.getAim().angle;
	}

	public int getOrdinal() {
		return this.getAim().ordinal();
	}

	public int getPreviousOrdinal() {
		int o = this.getOrdinal();
		return o > 0 ? o : Aim.list.length-1;
	}

	public int getAlpha() {
		return alpha;
	}

	private void incrementAim() {
		int o = this.getAim().ordinal();
		if (o == Aim.list.length-1) {
			aim = Aim.list[0];
		}
		else {
			aim = Aim.list[o+1];
		}

		if (!hasSolenoid) {
			this.checkSurroundingMagnetsAndCopySolenoidState();
		}
	}

	private void decrementAim() {
		int o = this.getAim().ordinal();
		if (o == 0) {
			aim = Aim.list[Aim.list.length-1];
		}
		else {
			aim = Aim.list[o-1];
		}

		if (!hasSolenoid) {
			this.checkSurroundingMagnetsAndCopySolenoidState();
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(3, 3, 3);
	}

	public static enum Aim {
		N(0,			2, 0),
		NNW1(11.3F,		2, -1),
		NNW2(24,		2, -1),
		NNW3(36.9F,		2, -2),
		NW(45,			2, -2),
		WNW1(53.1F,		1, -2),
		WNW2(66,		1, -2),
		WNW3(78.7F,		0, -2),
		W(90,			0, -2),
		WSW1(101.3F,	-1, -2),
		WSW2(114,		-1, -2),
		WSW3(126.9F,	-2, -2),
		SW(135,			-2, -2),
		SSW1(143.1F,	-2, -1),
		SSW2(156,		-2, -1),
		SSW3(168.7F,	-2, 0),
		S(180,			-2, 0),
		SSE1(191.3F,	-2, 1),
		SSE2(204,		-2, 1),
		SSE3(216.9F,	-2, 2),
		SE(225,			-2, 2),
		ESE1(233.1F,	-1, 2),
		ESE2(246,		-1, 2),
		ESE3(258.7F,	0, 2),
		E(270,			0, 2),
		ENE1(281.3F,	1, 2),
		ENE2(294,		1, 2),
		ENE3(306.9F,	2, 2),
		NE(315,			2, 2),
		NNE1(323.1F,	2, 1),
		NNE2(336,		2, 1),
		NNE3(348.7F,	2, 0);

		public final float angle;
		public final int xOffset;
		public final int zOffset;

		public static final Aim[] list = values();

		private Aim(float a, int x, int z) {
			angle = a;
			xOffset = x;
			zOffset = z;
		}

		public Aim getNext() {
			return this.ordinal() < list.length-1 ? list[this.ordinal()+1] : list[0];
		}

		public boolean isCardinal() {
			return this.ordinal()%8 == 0;
		}
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += charge;
	}

	@Override
	public int getMinDischarge() {
		return 8192;
	}

	@Override
	public float getAimX() {
		return 0.5F;
	}

	@Override
	public float getAimY() {
		return -1.25F;
	}

	@Override
	public float getAimZ() {
		return 0.5F;
	}
}
