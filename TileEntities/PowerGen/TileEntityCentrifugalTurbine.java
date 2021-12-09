/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.world.World;

import Reika.ReactorCraft.Registry.ReactorTiles;

//TODO Incomplete item

//Stackable, 3x3 wide, power out top of top one, cold fluid out bottom of bottom one; each one adds some torque and some efficiency, up to 6(?)
//Handles any fluid identically (easy to use, no bonuses from ammonia; can also handle hot CO2
//maybe need housing blocks
public class TileEntityCentrifugalTurbine extends TileEntityTurbineCore {

	@Override
	protected void intakeLubricant(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		return false;
	}

	@Override
	protected void dumpSteam(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected double getEfficiency() {
		return Math.pow(this.getNumberStagesTotal()/(this.getMaxStage()+1), 0.8);
	}

	@Override
	protected int getConsumedLubricant() {
		return 4;
	}

	@Override
	protected float getTorqueFactor() {
		return 0;
	}

	@Override
	protected double getAnimationSpeed() {
		return 0.2;
	}

	@Override
	protected int getMaxStage() { //is one less than max stage count
		return 5;
	}

	@Override
	public int getMaxTorque() {
		return 0;
	}

	@Override
	public int getMaxSpeed() {
		return 0;
	}

	@Override
	protected int getLubricantCapacity() {
		return 8000;
	}

	@Override
	protected boolean canCollideCheck() {
		return false;
	}

	@Override
	protected double getRadius() {
		return 1.5;
	}

	@Override
	protected boolean canOrientVertically() {
		return true;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.MINITURBINE.ordinal();
	}

}
