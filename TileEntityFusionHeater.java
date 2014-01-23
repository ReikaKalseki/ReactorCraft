/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Laserable;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import cpw.mods.fml.relauncher.Side;

public class TileEntityFusionHeater extends TileEntityReactorBase implements TemperatureTE, Laserable {

	public static final int PLASMA_TEMP = 150000000;

	private int temperature;

	private HybridTank tank = new HybridTank("fusionheater", 8000);

	@Override
	public void whenInBeam(long power, int range) {
		temperature += 640*ReikaMathLibrary.logbase(power, 2);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.updateTemperature(world, x, y, z, meta);

		ReikaJavaLibrary.pConsole(temperature+": "+((float)temperature/PLASMA_TEMP), Side.SERVER);
	}

	public void updateTemperature(World world, int x, int y, int z, int meta) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);
		int dT = temperature-Tamb;
		if (dT != 0)
			temperature -= (1+dT/16384D);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.INJECTOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return temperature/1000;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);

		tank.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		temperature = NBT.getInteger("temp");

		tank.readFromNBT(NBT);
	}

}
