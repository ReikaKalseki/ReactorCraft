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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell.LiquidStates;

public class TileEntityControlRod extends TileEntityReactorBase implements ReactorCoreTE, Temperatured {

	private boolean lowered;
	private int rodOffset;
	private Motions motion;

	private static final int MINOFFSET = 0;
	private static final int MAXOFFSET = 20;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.moveRods();
	}

	private void moveRods() {
		if (motion != null) {
			rodOffset += motion.stepHeight;
		}
		if (rodOffset <= MINOFFSET || rodOffset >= MAXOFFSET) {
			motion = null;
			rodOffset = Math.max(MINOFFSET, rodOffset);
			rodOffset = Math.min(MAXOFFSET, rodOffset);
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return ReactorTiles.CONTROL.ordinal();
	}

	public void toggle() {
		if (lowered) {
			motion = Motions.RAISING;
		}
		else {
			motion = Motions.LOWERING;
		}
		lowered = !lowered;
	}

	public void drop() {
		motion = Motions.SCRAM;
	}

	public boolean isActive() {
		return lowered && rodOffset == MINOFFSET;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return this.isActive() ? ReikaRandomHelper.doWithChance(50) : false;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setBoolean("down", lowered);

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		lowered = NBT.getBoolean("down");
	}

	@Override
	public int getMaxTemperature() {
		return 0;
	}

	private void onMeltdown(World world, int x, int y, int z) {

	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq.isWater();
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return side.offsetY != 0 && this.isActive() ? 1 : 0;
	}

	public int getRodPosition() {
		return rodOffset;
	}

	private static enum Motions {
		RAISING(1),
		LOWERING(-1),
		SCRAM(-7);

		public final int stepHeight;

		private Motions(int dh) {
			stepHeight = dh;
		}
	}

}
