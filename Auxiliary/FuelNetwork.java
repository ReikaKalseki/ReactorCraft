/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityULine;

public final class FuelNetwork {

	private final ArrayList<TileEntityFuelRod> rods = new ArrayList<TileEntityFuelRod>();
	private final ArrayList<Feedable> sources = new ArrayList<Feedable>();
	private final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	private static final HashMap<FuelNetwork, Integer> map = new HashMap<FuelNetwork, Integer>();
	private static int freeID = 1;

	public FuelNetwork() {
		map.put(this, this.getNextFreeID());
	}

	@Override
	public boolean equals(Object fuel) {
		if (fuel.getClass() != this.getClass())
			return false;
		return ((FuelNetwork)fuel).getID() == this.getID();
	}

	public int getID() {
		int id = map.get(this);
		if (id <= 0)
			return -1;
		else
			return id;
	}

	private int getNextFreeID() {
		int id = freeID;
		freeID++;
		return id;
	}

	public void addPipeTile(TileEntityULine te) {
		sources.add(te);
	}

	public void deletePipeTile(TileEntityULine te) {
		sources.remove(te);
	}

	public void addFuelCell(TileEntityFuelRod te) {
		rods.add(te);
	}

	public void deleteFuelCell(TileEntityFuelRod te) {
		rods.remove(te);
	}

	public void addSource(Feedable src) {
		sources.add(src);
	}

	public void deleteSource(Feedable src) {
		sources.remove(src);
	}

	public boolean isItemEmpty() {
		return items.isEmpty();
	}

	public void addItem(ItemStack is) {
		items.add(is);
	}

	public List<ItemStack> getItems() {
		return ReikaJavaLibrary.copyList(items);
	}

	public void distribute() {
		for (int i = 0; i < sources.size(); i++) {
			ItemStack is = sources.get(i).feedOut();
			if (is != null)
				items.add(is);
		}

		ArrayList<ItemStack> toremove = new ArrayList();
		for (int i = 0; i < rods.size(); i++) {
			TileEntityFuelRod rod = rods.get(i);
			for (int k = 0; k < items.size(); k++) {
				ItemStack is = items.get(k);
				if (!toremove.contains(is)) {
					if (rod.feedIn(is)) {
						toremove.add(is);
					}
				}
			}
		}
		items.removeAll(toremove);
	}

	public void merge(FuelNetwork fuel) {
		if (fuel == null)
			return;
		if (fuel.equals(this))
			return;
		rods.addAll(fuel.rods);
		sources.addAll(fuel.sources);
		fuel.destroy(this);
	}

	public void destroy(FuelNetwork repl) {
		for (int i = 0; i < rods.size(); i++) {
			TileEntityFuelRod rod = rods.get(i);
			rod.setNetwork(repl);
		}
		for (int i = 0; i < sources.size(); i++) {
			Feedable feed = sources.get(i);
			feed.setNetwork(repl);
		}
		rods.clear();
		sources.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(rods.size()+"/"+sources.size()+":\n");/*
		sb.append("Supplying:\n");
		for (int i = 0; i < rods.size(); i++) {
			TileEntityFuelRod rod = rods.get(i);
			sb.append(rod);
			if (i < rods.size()-1)
				sb.append(", ");
			else
				sb.append("\n");
		}
		sb.append("Supplied by:\n");
		for (int i = 0; i < sources.size(); i++) {
			Feedable src = sources.get(i);
			sb.append(src);
			if (i < sources.size()-1)
				sb.append(", ");
		}*/
		sb.append(this.getID());
		return sb.toString();
	}

}
