package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.world.World;



//Stackable, 3x3 wide, power out top of top one, cold fluid out bottom of bottom one; each one adds some torque and some efficiency, up to 6(?)
//Handles any fluid identically (easy to use, no bonuses from ammonia; can also handle hot CO2
//maybe need housing blocks
public class TileEntityCentrifugalTurbine extends TileEntityTurbineCore {

	@Override
	protected void intakeLubricant(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected boolean intakeSteam(World world, int x, int y, int z, int meta) {
		return false; //TODO
	}

	@Override
	protected void dumpSteam(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected double getEfficiency() {
		return 0.8+0.2*this.getNumberStagesTotal()/(this.getMaxStage()+1);
	}

	@Override
	protected int getConsumedLubricant() {
		return 4;
	}

	@Override
	protected float getTorqueFactor() {
		return 0; //TODO
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
		return 0; //TODO
	}

	@Override
	public int getMaxSpeed() {
		return 0; //TODO
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

}
