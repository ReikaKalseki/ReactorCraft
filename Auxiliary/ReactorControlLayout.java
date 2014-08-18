/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.DragonAPICore;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReactorControlLayout {

	private final TileEntityCPU controller;

	private final HashMap<List<Integer>, TileEntityControlRod> controls = new HashMap();
	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public ReactorControlLayout(TileEntityCPU cpu) {
		controller = cpu;
	}

	public int getSizeX() {
		return maxX-minX+1;
	}

	public int getSizeZ() {
		return maxZ-minZ+1;
	}

	public void addControlRod(TileEntityControlRod rod) {
		int x = this.getXPosition(rod);
		int z = this.getZPosition(rod);
		if (minX > x)
			minX = x;
		if (maxX < x)
			maxX = x;
		if (minZ > z)
			minZ = z;
		if (maxZ < z)
			maxZ = z;
		controls.put(Arrays.asList(x, z), rod);
	}

	public boolean hasControlRodAtRelativePosition(int x, int z) {
		return this.getControlRodAtRelativePosition(x, z) != null;
	}

	public boolean hasControlRodAtAbsolutePosition(int x, int z) {
		return this.getControlRodAtAbsolutePosition(x, z) != null;
	}

	public TileEntityControlRod getControlRodAtRelativePosition(int x, int z) {
		return controls.get(Arrays.asList(x, z));
	}

	public TileEntityControlRod getControlRodAtAbsolutePosition(int x, int z) {
		return controls.get(Arrays.asList(x-controller.xCoord, z-controller.zCoord));
	}

	private int getXPosition(TileEntityControlRod rod) {
		return rod.xCoord-controller.xCoord;
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

	public int getMinZ() {
		return minZ;
	}

	public int getMaxZ() {
		return maxZ;
	}

	@SideOnly(Side.CLIENT)
	public Color getDisplayColorAtRelativePosition(int x, int z) {
		TileEntityControlRod rod = this.getControlRodAtRelativePosition(x, z);
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
		for (List li : controls.keySet()) {
			TileEntityControlRod rod = controls.get(li);
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

	public ArrayList<TileEntityControlRod> getAllRods() {
		ArrayList<TileEntityControlRod> li = new ArrayList();
		li.addAll(controls.values());
		return li;
	}

	public int countLoweredRods() {
		int count = 0;
		for (List li : controls.keySet()) {
			TileEntityControlRod rod = controls.get(li);
			if (rod.isActive())
				count++;
		}
		return count;
	}

	public boolean isEmpty() {
		return controls.isEmpty();
	}

}
