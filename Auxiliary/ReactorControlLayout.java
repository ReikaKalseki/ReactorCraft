/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReactorControlLayout {

	private WorldLocation controller;

	private final TileEntityCache<TileEntityControlRod> controls = new TileEntityCache();
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public ReactorControlLayout(TileEntityCPU cpu) {
		controller = new WorldLocation(cpu);
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
		if (minX > rod.xCoord)
			minX = rod.xCoord;
		if (maxX < rod.xCoord)
			maxX = rod.xCoord;
		if (minY > rod.yCoord)
			minY = rod.yCoord;
		if (maxY < rod.yCoord)
			maxY = rod.yCoord;
		if (minZ > rod.zCoord)
			minZ = rod.zCoord;
		if (maxZ < rod.zCoord)
			maxZ = rod.zCoord;
		controls.put(rod);
	}

	public boolean hasControlRodAtRelativePosition(World world, int x, int y, int z) {
		return this.getControlRodAtRelativePosition(world, x, y, z) != null;
	}

	public boolean hasControlRodAtAbsolutePosition(World world, int x, int y, int z) {
		return this.getControlRodAtAbsolutePosition(world, x, y, z) != null;
	}

	public TileEntityControlRod getControlRodAtRelativePosition(World world, int x, int y, int z) {
		return controls.get(new WorldLocation(world, x+controller.xCoord, y+controller.yCoord, z+controller.zCoord));
	}

	public TileEntityControlRod getControlRodAtAbsolutePosition(World world, int x, int y, int z) {
		return controls.get(new WorldLocation(world, x, y, z));
	}

	public int getMinX() {
		return minX-controller.xCoord;
	}

	public int getMaxX() {
		return maxX-controller.xCoord;
	}

	public int getMinY() {
		return minY-controller.yCoord;
	}

	public int getMaxY() {
		return maxY-controller.yCoord;
	}

	public int getMinZ() {
		return minZ-controller.zCoord;
	}

	public int getMaxZ() {
		return maxZ-controller.zCoord;
	}

	@SideOnly(Side.CLIENT)
	public int getDisplayColorAtRelativePosition(World world, int x, int y, int z) {
		TileEntityControlRod rod = this.getControlRodAtRelativePosition(world, x, y, z);
		if (rod != null) {
			if (((TileEntityCPU)controller.getTileEntity()).getPower() >= this.getMinPower())
				return rod.isActive() ? 0x00ff00 : 0xff0000;
			else
				return 0xa0a0a0;
		}
		return 0x6a6a6a;
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
		minX = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		minZ = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		maxY = Integer.MIN_VALUE;
		maxZ = Integer.MIN_VALUE;
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

	public void writeToNBT(NBTTagCompound NBT) {
		controls.writeToNBT(NBT);
		controller.writeToNBT("control", NBT);
		NBT.setInteger("maxx", maxX);
		NBT.setInteger("maxy", maxY);
		NBT.setInteger("maxz", maxZ);
		NBT.setInteger("minx", minX);
		NBT.setInteger("miny", minY);
		NBT.setInteger("minz", minZ);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		controls.readFromNBT(NBT);
		controller = WorldLocation.readFromNBT("control", NBT);
		maxX = NBT.getInteger("maxx");
		maxY = NBT.getInteger("maxy");
		maxZ = NBT.getInteger("maxz");
		minX = NBT.getInteger("minx");
		minY = NBT.getInteger("miny");
		minZ = NBT.getInteger("minz");
	}

}
