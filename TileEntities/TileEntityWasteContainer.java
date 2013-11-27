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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Base.TileEntityWasteUnit;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.TemperatureTE;

public class TileEntityWasteContainer extends TileEntityWasteUnit implements TemperatureTE {

	public static final int WIDTH = 9;
	public static final int HEIGHT = 3;

	@Override
	public int getIndex() {
		return ReactorTiles.WASTECONTAINER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();

		if (thermalTicker.checkCap()) {
			int waste = this.countWaste();
			temperature += waste*ReikaNuclearHelper.getWasteDecayHeat();
			this.updateTemperature(world, x, y, z, meta);
		}

		if (!world.isRemote)
			this.decayWaste();

		//this.fill();
	}

	private void distributeHeat(World world, int x, int y, int z) {
		int Tamb = ReikaWorldHelper.getBiomeTemp(world.getBiomeGenForCoords(x, z));
		//ReikaJavaLibrary.pConsole(temperature);
		if (temperature > Tamb) {
			ForgeDirection side = ReikaWorldHelper.checkForAdjSourceBlock(world, x, y, z, Material.water);
			if (side != null) {
				temperature -= ReikaThermoHelper.getTemperatureIncrease(1, 15000, ReikaThermoHelper.WATER_BLOCK_HEAT);
				//ReikaJavaLibrary.pConsole(temperature);
				if (temperature > 100)
					ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, 0, 0);
				else
					ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.waterMoving.blockID, 6);
			}
		}
		//ReikaJavaLibrary.pConsole(temperature);
		if (temperature < Tamb)
			temperature = Tamb;
		if (temperature > this.getMaxTemperature()) {
			this.overheat(world, x, y, z);
		}
		else if (temperature > this.getMaxTemperature()/2 && rand.nextInt(6) == 0) {
			world.spawnParticle("smoke", x+rand.nextDouble(), y+1, z+rand.nextDouble(), 0, 0, 0);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
		}
		else if (temperature > this.getMaxTemperature()/4 && rand.nextInt(20) == 0) {
			world.spawnParticle("smoke", x+rand.nextDouble(), y+1, z+rand.nextDouble(), 0, 0, 0);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return this.isLongLivedWaste(itemstack);
	}

	@Override
	public int getSizeInventory() {
		return WIDTH*HEIGHT;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void addTemperature(int T) {
		temperature += T;
	}

	public int getMaxTemperature() {
		return 600;
	}

	public void onMeltdown(World world, int x, int y, int z) {
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 9, true);
		RadiationEffects.contaminateArea(world, x, y, z, 9);
	}

	@Override
	public void updateTemperature(World world, int x, int y, int z, int meta) {
		this.distributeHeat(world, x, y, z);
	}

	@Override
	public int getThermalDamage() {
		return temperature/100;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {
		this.onMeltdown(world, x, y, z);
	}

	@Override
	public boolean leaksRadiation() {
		return true;
	}

	@Override
	public boolean isValidIsotope(Isotopes i) {
		return true;
	}

}
