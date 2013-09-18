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

import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityULine;

public class FuelNetwork {

	private ArrayList<TileEntityFuelRod> rods = new ArrayList<TileEntityFuelRod>();
	private ArrayList<Feedable> sources = new ArrayList<Feedable>();

	public FuelNetwork() {

	}

	public void addPipeTile(TileEntityULine te) {

	}

	public void addFuelCell(TileEntityFuelRod te) {

	}

	public void addSource(Feedable src) {

	}

	public void distribute() {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		for (int i = 0; i < sources.size(); i++) {
			ItemStack is = sources.get(i).feedOut();
			if (is != null)
				li.add(is);
		}
		for (int i = 0; i < rods.size(); i++) {

			//rods.get(i).feedIn(is);
		}
	}

	public void merge(FuelNetwork fuel) {
		if (fuel == null)
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
			else
				sb.append("\n");
		}
		return sb.toString();
	}

}
