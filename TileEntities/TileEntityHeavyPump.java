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

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidTank;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftPowerReceiver;

public class TileEntityHeavyPump extends TileEntityReactorBase implements ShaftPowerReceiver {

	private int torque;
	private int omega;
	private long power;

	private LiquidTank tank = new LiquidTank(200);

	@Override
	public int getIndex() {
		return ReactorTiles.HEAVYPUMP.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int[] getInputBlocksX() {
		return new int[0];
	}

	@Override
	public int[] getInputBlocksY() {
		return new int[0];
	}

	@Override
	public int[] getInputBlocksZ() {
		return new int[0];
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.values()[i];
			if (x == xCoord+dir.offsetX && y == yCoord+dir.offsetY && z == zCoord+dir.offsetZ)
				return true;
		}
		return false;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public String getName() {
		return this.getTEName();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	private boolean canHarvest(World world, int x, int y, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return biome == BiomeGenBase.ocean && y < 30 && ReikaWorldHelper.checkForAdjSourceBlock(world, x, y, z, Material.water) != -1;
	}

}
