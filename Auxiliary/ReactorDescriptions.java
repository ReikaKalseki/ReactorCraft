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

import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntityBreederCore;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityReactorPump;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;

public final class ReactorDescriptions {

	public static final String PARENT = "Resources/";
	public static final String DESC_SUFFIX = ":desc";
	public static final String NOTE_SUFFIX = ":note";

	private static HashMap<ReactorBook, String> data = new HashMap<ReactorBook, String>();
	private static HashMap<ReactorBook, String> notes = new HashMap<ReactorBook, String>();

	private static HashMap<ReactorTiles, Object[]> machineData = new HashMap<ReactorTiles, Object[]>();
	private static HashMap<ReactorTiles, Object[]> machineNotes = new HashMap<ReactorTiles, Object[]>();
	private static HashMap<ReactorBook, Object[]> miscData = new HashMap<ReactorBook, Object[]>();

	private static ArrayList<ReactorBook> categories = new ArrayList<ReactorBook>();

	private static final boolean mustLoad = !ReikaObfuscationHelper.isDeObfEnvironment();
	private static final XMLInterface parents = new XMLInterface(ReactorCraft.class, PARENT+"categories.xml", mustLoad);
	private static final XMLInterface machines = new XMLInterface(ReactorCraft.class, PARENT+"machines.xml", mustLoad);
	private static final XMLInterface tools = new XMLInterface(ReactorCraft.class, PARENT+"tools.xml", mustLoad);
	private static final XMLInterface resources = new XMLInterface(ReactorCraft.class, PARENT+"resource.xml", mustLoad);
	private static final XMLInterface infos = new XMLInterface(ReactorCraft.class, PARENT+"info.xml", mustLoad);

	public static String getTOC() {
		List<ReactorBook> toctabs = ReactorBook.getTOCTabs();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toctabs.size(); i++) {
			ReactorBook h = toctabs.get(i);
			sb.append("Page ");
			sb.append(h.getScreen());
			sb.append(" - ");
			sb.append(h.getTitle());
			if (i < toctabs.size()-1)
				sb.append("\n");
		}
		return sb.toString();
	}

	private static void addData(ReactorTiles m, Object... data) {
		machineData.put(m, data);
	}

	private static void addData(ReactorBook h, Object... data) {
		miscData.put(h, data);
	}

	private static void addData(ReactorBook h, int[] data) {
		Object[] o = new Object[data.length];
		for (int i = 0; i < o.length; i++)
			o[i] = data[i];
		miscData.put(h, o);
	}

	private static void addNotes(ReactorTiles m, Object... data) {
		machineNotes.put(m, data);
	}

	public static void reload() {
		data.clear();
		loadNumericalData();

		machines.reread();
		tools.reread();
		resources.reread();
		infos.reread();

		parents.reread();

		loadData();
	}

	private static void addEntry(ReactorBook h, String sg) {
		data.put(h, sg);
	}

	public static void loadData() {
		List<ReactorBook> parenttabs = ReactorBook.getCategoryTabs();

		List<ReactorBook> machinetabs = ReactorBook.getMachineTabs();
		ReactorBook[] tooltabs = ReactorBook.getToolTabs();
		ReactorBook[] resourcetabs = ReactorBook.getResourceTabs();
		ReactorBook[] infotabs = ReactorBook.getInfoTabs();

		for (int i = 0; i < parenttabs.size(); i++) {
			ReactorBook h = parenttabs.get(i);
			String desc = parents.getValueAtNode("categories:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (int i = 0; i < machinetabs.size(); i++) {
			ReactorBook h = machinetabs.get(i);
			ReactorTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase()+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase()+NOTE_SUFFIX);
			desc = String.format(desc, machineData.get(m));
			aux = String.format(aux, machineNotes.get(m));

			if (XMLInterface.NULL_VALUE.equals(desc))
				desc = "There is no handbook data for this machine yet.";

			if (m.isDummiedOut()) {
				desc += "\nThis machine is currently unavailable.";
				aux += "\nNote: Dummied Out";
			}

			addEntry(h, desc);
			notes.put(h, aux);
		}

		for (int i = 0; i < tooltabs.length; i++) {
			ReactorBook h = tooltabs[i];
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (int i = 0; i < resourcetabs.length; i++) {
			ReactorBook h = resourcetabs[i];
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (int i = 0; i < infotabs.length; i++) {
			ReactorBook h = infotabs[i];
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase());
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}
	}


	public static String getData(ReactorBook h) {
		if (!data.containsKey(h))
			return "";
		return data.get(h);
	}

	public static String getNotes(ReactorBook h) {
		if (!notes.containsKey(h))
			return "";
		return notes.get(h);
	}

	static {
		loadNumericalData();
	}

	private static void loadNumericalData() {
		addNotes(ReactorTiles.CENTRIFUGE, TileEntityCentrifuge.MINSPEED);
		addNotes(ReactorTiles.ELECTROLYZER, TileEntityElectrolyzer.SALTPOWER, TileEntityElectrolyzer.SALT_MELT);
		addNotes(ReactorTiles.SYNTHESIZER, TileEntitySynthesizer.AMMONIATEMP);
		addNotes(ReactorTiles.TURBINECORE, TileEntityTurbineCore.GEN_OMEGA, TileEntityTurbineCore.TORQUE_CAP);
		addNotes(ReactorTiles.PUMP, TileEntityReactorPump.MINPOWER, TileEntityReactorPump.MINTORQUE);
		addNotes(ReactorTiles.EXCHANGER, TileEntityHeatExchanger.MINPOWER, TileEntityHeatExchanger.MINSPEED);
		addNotes(ReactorTiles.FUEL, TileEntityFuelRod.EXPLOSION);
		addNotes(ReactorTiles.CPU, TileEntityCPU.POWERPERROD);
		addNotes(ReactorTiles.BREEDER, TileEntityBreederCore.EXPLOSION);
		addNotes(ReactorTiles.HEATER, TileEntityFusionHeater.PLASMA_TEMP);
		addNotes(ReactorTiles.SOLENOID, TileEntitySolenoidMagnet.MINOMEGA, TileEntitySolenoidMagnet.MINTORQUE);
		addData(ReactorTiles.HEAVYPUMP, TileEntityHeavyPump.MINDEPTH, TileEntityHeavyPump.MAXY);
		addNotes(ReactorTiles.HEAVYPUMP, TileEntityHeavyPump.MINPOWER, TileEntityHeavyPump.MINTORQUE);
	}
}
