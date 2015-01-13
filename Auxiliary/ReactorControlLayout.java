/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReactorControlLayout {

	private final TileEntityCPU controller;

	private final TileEntityCache<TileEntityControlRod> controls = new TileEntityCache();
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public ReactorControlLayout(TileEntityCPU cpu) {
		controller = cpu;
	}

	public int getSizeX() {
		return maxX-minX+1;
	}

	public int getSizeY() {
		return maxY-minY+1;
	}

	public int getSizeZ() {
		return maxZ-minZ+1;
	}

	public void addControlRod(TileEntityControlRod rod) {
		int x = this.getXPosition(rod);
		int y = this.getYPosition(rod);
		int z = this.getZPosition(rod);
		if (minX > x)
			minX = x;
		if (maxX < x)
			maxX = x;
		if (minY > y)
			minY = y;
		if (maxY < y)
			maxY = y;
		if (minZ > z)
			minZ = z;
		if (maxZ < z)
			maxZ = z;
		controls.put(new WorldLocation(rod.worldObj, x, y, z), rod);
	}

	public boolean hasControlRodAtRelativePosition(World world, int x, int y, int z) {
		return this.getControlRodAtRelativePosition(world, x, y, z) != null;
	}

	public boolean hasControlRodAtAbsolutePosition(World world, int x, int y, int z) {
		return this.getControlRodAtAbsolutePosition(world, x, y, z) != null;
	}

	public TileEntityControlRod getControlRodAtRelativePosition(World world, int x, int y, int z) {
		return controls.get(new WorldLocation(world, x, y, z));
	}

	public TileEntityControlRod getControlRodAtAbsolutePosition(World world, int x, int y, int z) {
		return controls.get(new WorldLocation(world, x-controller.xCoord, y-controller.yCoord, z-controller.zCoord));
	}

	private int getXPosition(TileEntityControlRod rod) {
		return rod.xCoord-controller.xCoord;
	}

	private int getYPosition(TileEntityControlRod rod) {
		return rod.yCoord-controller.yCoord;
	}

	private int getZPosition(TileEntityControlRod rod) {
		return rod.zCoord-controller.zCoord;
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getMaxZ() {
		return maxZ;
	}

	@SideOnly(Side.CLIENT)
	public Color getDisplayColorAtRelativePosition(World world, int x, int y, int z) {
		TileEntityControlRod rod = this.getControlRodAtRelativePosition(world, x, y, z);
		if (rod != null) {
			if (controller.getPower() >= this.getMinPower())
				return rod.isActive() ? Color.GREEN : Color.RED;
			else
				return Color.GRAY;
		}
		return Color.DARK_GRAY;
	}

	public long getMinPower() {
		return DragonAPICore.debugtest ? 0 : this.getPowerPerRod()*controls.size();
	}

	private long getPowerPerRod() {
		return TileEntityCPU.POWERPERROD;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(controller.toString());
		sb.append(" ");
		sb.append(controls);
		return sb.toString();
	}

	public void SCRAM() {
		int iter = 0;
		for (WorldLocation c : controls.keySet()) {
			TileEntityControlRod rod = controls.get(c);
			rod.drop(iter == 0);
			iter++;
		}
	}

	public void clear() {
		controls.clear();
		maxX = minX = maxZ = minZ = 0;
	}

	public int getNumberRods() {
		return controls.size();
	}

	public Collection<TileEntityControlRod> getAllRods() {
		return Collections.unmodifiableCollection(controls.values());
	}

	public int countLoweredRods() {
		int count = 0;
		for (WorldLocation c : controls.keySet()) {
			TileEntityControlRod rod = controls.get(c);
			if (rod.isActive())
				count++;
		}
		return count;
	}

	public boolean isEmpty() {
		return controls.isEmpty();
	}

}
