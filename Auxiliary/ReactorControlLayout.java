package Reika.ReactorCraft.Auxiliary;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReactorControlLayout {

	private final TileEntityCPU controller;

	private final HashMap<List<Integer>, TileEntityControlRod> controls = new HashMap();
	private int minX;
	private int maxX;
	private int minZ;
	private int maxZ;

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
		return rod != null ? rod.isActive() ? Color.GREEN : Color.RED : Color.DARK_GRAY;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(controller.toString());
		sb.append(" ");
		sb.append(controls);
		return sb.toString();
	}

}
