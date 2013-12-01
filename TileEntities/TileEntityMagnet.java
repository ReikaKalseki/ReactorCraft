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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Screwdriverable;

public class TileEntityMagnet extends TileEntityReactorBase implements Screwdriverable {

	//0 is +x(E), rotates to -z(N)
	private Aim aim = Aim.N;


	private int alpha = 512;

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNET.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		List<EntityPlasma> li = world.getEntitiesWithinAABB(EntityPlasma.class, box);
		int[] tg = this.getTarget();
		for (int i = 0; i < li.size(); i++) {
			EntityPlasma e = li.get(i);
			e.setTarget(tg[0], tg[2]);
		}
		if (rand.nextInt(1) == 0) {
			EntityPlasma e = new EntityPlasma(world);
			e.setLocationAndAngles(x+0.5, y+0.5, z+0.5, 0, 0);
			if (!world.isRemote)
				world.spawnEntityInWorld(e);
		}
	}

	public int[] getTarget() {
		int[] tg = new int[3];
		tg[0] = xCoord+this.getAim().xOffset;
		tg[2] = zCoord+this.getAim().zOffset;
		tg[1] = yCoord;
		return tg;
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (alpha > 0)
			alpha -= 8;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		aim = this.getAim(NBT.getInteger("aim"));
		alpha = NBT.getInteger("al");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("aim", this.getAim().ordinal());
		NBT.setInteger("al", alpha);
	}

	private Aim getAim() {
		return aim != null ? aim : Aim.N;
	}

	private Aim getAim(int o) {
		return (o > 0 && o < Aim.list.length) ? Aim.list[o] : Aim.N;
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z) {
		alpha = 512;
		this.decrementAim();
		return true;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z) {
		alpha = 512;
		this.incrementAim();
		return true;
	}

	public float getAngle() {
		return this.getAim().angle;
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
	}

	private void decrementAim() {
		int o = this.getAim().ordinal();
		if (o == 0) {
			aim = Aim.list[Aim.list.length-1];
		}
		else {
			aim = Aim.list[o-1];
		}
	}

	private static enum Aim {
		N(0,			2, 0),
		NNW1(11.3F,		2, -1),
		NNW2(24,		2, -1),
		NNW3(36.9F,		1, -1),
		NW(45,			1, -1),
		WNW1(53.1F,		1, -2),
		WNW2(66,		1, -2),
		WNW3(78.7F,		0, -2),
		W(90,			0, -2),
		WSW1(101.3F,	-1, -2),
		WSW2(114,		-1, -2),
		WSW3(126.9F,	-1, -1),
		SW(135,			-1, -1),
		SSW1(143.1F,	-2, -1),
		SSW2(156,		-2, -1),
		SSW3(168.7F,	-2, 0),
		S(180,			-2, 0),
		SSE1(191.3F,	-2, 1),
		SSE2(204,		-2, 1),
		SSE3(216.9F,	-1, 1),
		SE(225,			-1, 1),
		ESE1(233.1F,	-1, 2),
		ESE2(246,		-1, 2),
		ESE3(258.7F,	0, 2),
		E(270,			0, 2),
		ENE1(281.3F,	1, 2),
		ENE2(294,		1, 2),
		ENE3(306.9F,	1, 1),
		NE(315,			1, 1),
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
	}
}
