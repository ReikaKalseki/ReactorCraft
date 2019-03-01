/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Collections.ChancedOutputList;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.Isotopes.ElementGroup;
import Reika.ReactorCraft.Registry.ReactorItems;

public class WasteManager {

	private static final ArrayList<Isotopes> wastes = new ArrayList<Isotopes>();
	private static final ArrayList<Isotopes> thoriumWastes = new ArrayList<Isotopes>();
	private static final WeightedRandom<Isotopes> yields = new WeightedRandom();
	private static final WeightedRandom<Isotopes> thoriumYields = new WeightedRandom();
	private static final Random rand = new Random();

	private static void addWaste(Isotopes iso, double percent) {
		wastes.add(iso);
		yields.addEntry(iso, percent);
	}

	private static void addThoriumWaste(Isotopes iso, double percent) {
		thoriumWastes.add(iso);
		thoriumYields.addEntry(iso, percent);
	}

	static {
		addWaste(Isotopes.Cs134, 6.79);
		addWaste(Isotopes.Xe135, 6.33);
		addWaste(Isotopes.Zr93, 6.30);
		addWaste(Isotopes.Mo99, 6.10);
		addWaste(Isotopes.Cs137, 6.09);
		addWaste(Isotopes.Tc99, 6.05);
		addWaste(Isotopes.Sr90, 5.75);
		addWaste(Isotopes.I131, 2.83);
		addWaste(Isotopes.Pm147, 2.27);
		addWaste(Isotopes.I129, 0.66);
		addWaste(Isotopes.Sm151, 0.42);
		addWaste(Isotopes.Ru106, 0.39);
		addWaste(Isotopes.Kr85, 0.27);
		addWaste(Isotopes.Pd107, 0.16);
		addWaste(Isotopes.Se79, 0.05);
		addWaste(Isotopes.Gd155, 0.03);
		addWaste(Isotopes.Sb125, 0.03);
		addWaste(Isotopes.Sn126, 0.02);

		addThoriumWaste(Isotopes.Cs137, 6.84);
		//stable addThoriumWaste(Isotopes.Xe136, 6.67);
		//stable addThoriumWaste(Isotopes.Mo95, 6.36);
		//stable addThoriumWaste(Isotopes.Xe134, 6.30);
		//stable addThoriumWaste(Isotopes.Nd143, 5.97);
		//stable addThoriumWaste(Isotopes.Cs133, 5.95);
		addThoriumWaste(Isotopes.I135, 5.03);
		addThoriumWaste(Isotopes.Tc99, 4.92);
		addThoriumWaste(Isotopes.Xe131, 3.60);
		//stable addThoriumWaste(Isotopes.Nd145, 3.45);
		//stable addThoriumWaste(Isotopes.Ru101, 3.17);
		addThoriumWaste(Isotopes.Pm147, 1.74);
		addThoriumWaste(Isotopes.Ru103,	1.57);
		addThoriumWaste(Isotopes.Xe135, 1.23);
		//stable addThoriumWaste(Isotopes.Kr83, 1.01);
		addThoriumWaste(Isotopes.Pm149, 0.78);
		addThoriumWaste(Isotopes.Rh105, 0.50);
		//stable addThoriumWaste(Isotopes.I127, 0.46);
		addThoriumWaste(Isotopes.Sm151, 0.32);
		addThoriumWaste(Isotopes.Ru106, 0.25);
	}

	public static Isotopes getRandomWaste() {
		return yields.getRandomEntry();
	}

	public static Isotopes getRandomThoriumWaste() {
		return thoriumYields.getRandomEntry();
	}

	public static int getFullyRandomWaste() {
		int i = rand.nextInt(wastes.size());
		return i;
	}

	public static int getNumberWastes() {
		return wastes.size();
	}

	public static List<Isotopes> getWasteList() {
		return Collections.unmodifiableList(wastes);
	}

	public static ItemStack getRandomWasteItem() {
		Isotopes atom = getRandomWaste();
		ItemStack is = getWaste(atom);
		return is;
	}

	public static ItemStack getRandomThoriumWasteItem() {
		Isotopes atom = getRandomThoriumWaste();
		ItemStack is = getWaste(atom);
		return is;
	}

	public static ItemStack getWaste(Isotopes atom) {
		return ReactorItems.WASTE.getStackOfMetadata(/*wastes.indexOf(atom)*/atom.ordinal());
	}

	public static ItemStack getWaste(ElementGroup g) {
		return ReactorItems.WASTE.getStackOfMetadata(1000+g.ordinal());
	}

	public static ItemStack getFullyRandomWasteItem() {
		int i = getFullyRandomWaste();
		Isotopes atom = wastes.get(i);
		ItemStack is = ReactorItems.WASTE.getStackOfMetadata(i);
		return is;
	}

	public static ChancedOutputList getOutputs() {
		ChancedOutputList c = new ChancedOutputList(false);
		for (Isotopes i : wastes) {
			float ch = (float)(100F*yields.getWeight(i)/yields.getMaxWeight());
			if (ch > 0)
				c.addItem(getWaste(i), ch);
		}
		return c;
	}

	private static HashMap<ElementGroup, Float> getChancesByGroup() {
		HashMap<ElementGroup, Float> chances = new HashMap();
		float total = 0;
		for (Isotopes i : thoriumWastes) {
			Float get = chances.get(i.group);
			if (get == null)
				get = 0F;
			float ch = (float)(100F*thoriumYields.getWeight(i)/thoriumYields.getTotalWeight());
			get += ch;
			total += ch;
			chances.put(i.group, get);
		}
		for (Entry<ElementGroup, Float> e : chances.entrySet()) {
			e.setValue(e.getValue()/total);
		}
		return chances;
	}

	public static ChancedOutputList getThoriumOutputs(boolean splitByElementType) {
		ChancedOutputList c = new ChancedOutputList(false);
		if (splitByElementType) {
			HashMap<ElementGroup, Float> map = getChancesByGroup();
			for (Entry<ElementGroup, Float> e : map.entrySet()) {
				if (e.getValue() > 0) {
					c.addItem(getWaste(e.getKey()), 100*e.getValue());
				}
			}
		}
		else {
			for (Isotopes i : thoriumWastes) {
				float ch = (float)(100F*thoriumYields.getWeight(i)/thoriumYields.getTotalWeight());
				if (ch > 0)
					c.addItem(getWaste(i), ch);
			}
		}
		return c;
	}

	public static ChancedOutputList getThoriumGroupOutputs(ElementGroup g) {
		ChancedOutputList c = new ChancedOutputList(false);
		float f = 1F/getChancesByGroup().get(g);
		for (Isotopes i : thoriumWastes) {
			if (i.group == g) {
				float ch = (float)(100F*f*thoriumYields.getWeight(i)/thoriumYields.getTotalWeight());
				if (ch > 0) {
					ItemStack is = getWaste(i);
					//ReikaJavaLibrary.pConsole("Adding "+i.getDisplayName()+" to "+g.displayName+" with chance "+ch+" = "+is);
					c.addItem(is, ch);
				}
			}
		}
		return c;
	}

}
