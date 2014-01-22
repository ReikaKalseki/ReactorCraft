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
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Laserable;
import Reika.RotaryCraft.API.Shockable;
import Reika.RotaryCraft.API.ThermalMachine;
import cpw.mods.fml.relauncher.Side;

public class TileEntityIonizer extends TileEntityReactorBase implements Shockable, Laserable, ThermalMachine {

	public static final int PLASMACHARGE = 600000;
	public static final int PLASMA_TEMP = 150000000;

	private int charge;

	private ForgeDirection facing;

	private int temperature;

	private StepTimer tempTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.IONIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		//tempTimer.update();
		this.updateTemperature(world, x, y, z, meta);

		ReikaJavaLibrary.pConsole(temperature+": "+((float)temperature/PLASMA_TEMP), Side.SERVER);
	}

	private void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);
		int dT = temperature-Tamb;
		if (dT != 0)
			temperature -= (1+dT/16384D);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("chg", charge);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("temp", temperature);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		charge = NBT.getInteger("chg");

		facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];

		temperature = NBT.getInteger("temp");
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += charge;
	}

	@Override
	public int getMinDischarge() {
		return 16384;
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	@Override
	public void whenInBeam(long power, int range) {
		temperature += 640*ReikaMathLibrary.logbase(power, 2);
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
	public void addTemperature(int T) {
		temperature += T;
	}

	@Override
	public int getMaxTemperature() {
		return 200000000;
	}

	@Override
	public void onOverheat(World world, int x, int y, int z) {

	}

	@Override
	public boolean canBeFrictionHeated() {
		return false;
	}

}
