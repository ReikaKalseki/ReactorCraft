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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Client.ResourceReloadEvent;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.RadiationShield;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntityBreederCore;
import Reika.ReactorCraft.TileEntities.Fission.Thorium.TileEntityThoriumCore;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHiPTurbine;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityReactorPump;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySolarExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityWasteDecayer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReactorDescriptions {

	private static String PARENT = getParent(true);
	public static final String DESC_SUFFIX = ":desc";
	public static final String NOTE_SUFFIX = ":note";

	private static HashMap<ReactorBook, String> data = new HashMap<ReactorBook, String>();
	private static HashMap<ReactorBook, String> notes = new HashMap<ReactorBook, String>();

	private static HashMap<ReactorTiles, Object[]> machineData = new HashMap<ReactorTiles, Object[]>();
	private static HashMap<ReactorTiles, Object[]> machineNotes = new HashMap<ReactorTiles, Object[]>();
	private static HashMap<ReactorBook, Object[]> miscData = new HashMap<ReactorBook, Object[]>();

	private static ArrayList<ReactorBook> categories = new ArrayList<ReactorBook>();

	private static final XMLInterface parents = loadData("categories");
	private static final XMLInterface machines = loadData("machines");
	private static final XMLInterface tools = loadData("tools");
	private static final XMLInterface resources = loadData("resource");
	private static final XMLInterface infos = loadData("info");

	private static XMLInterface loadData(String name) {
		XMLInterface xml = new XMLInterface(ReactorCraft.class, PARENT+name+".xml", !ReikaObfuscationHelper.isDeObfEnvironment());
		xml.setFallback(getParent(false)+name+".xml");
		xml.init();
		return xml;
	}

	private static String getParent(boolean locale) {
		return locale && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getLocalizedParent() : "Resources/";
	}

	@SideOnly(Side.CLIENT)
	private static String getLocalizedParent() {
		Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
		String lang = language.getLanguageCode();
		if (hasLocalizedFor(language) && !"en_US".equals(lang))
			return "Resources/"+lang+"/";
		return "Resources/";
	}

	@SideOnly(Side.CLIENT)
	private static boolean hasLocalizedFor(Language language) {
		String lang = language.getLanguageCode();
		try (InputStream o = ReactorCraft.class.getResourceAsStream("Resources/"+lang+"/categories.xml")) {
			return o != null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

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
		PARENT = getParent(true);

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
			String desc = parents.getValueAtNode("categories:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (int i = 0; i < machinetabs.size(); i++) {
			ReactorBook h = machinetabs.get(i);
			ReactorTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+NOTE_SUFFIX);
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
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (int i = 0; i < resourcetabs.length; i++) {
			ReactorBook h = resourcetabs[i];
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (int i = 0; i < infotabs.length; i++) {
			ReactorBook h = infotabs[i];
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase(Locale.ENGLISH));
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
		MinecraftForge.EVENT_BUS.register(new ReloadListener());
	}

	public static class ReloadListener {

		@SubscribeEvent
		public void reload(ResourceReloadEvent evt) {
			ReactorDescriptions.reload();
		}

	}

	private static void loadNumericalData() {
		addNotes(ReactorTiles.CENTRIFUGE, TileEntityCentrifuge.MINSPEED);
		addNotes(ReactorTiles.ELECTROLYZER, /*TileEntityElectrolyzer.SALTPOWER, */TileEntityElectrolyzer.SALT_MELT);
		addNotes(ReactorTiles.SYNTHESIZER, TileEntitySynthesizer.AMMONIATEMP);
		addNotes(ReactorTiles.TURBINECORE, TileEntityTurbineCore.GEN_OMEGA, TileEntityTurbineCore.TORQUE_CAP);
		addNotes(ReactorTiles.BIGTURBINE, TileEntityHiPTurbine.GEN_OMEGA);
		addNotes(ReactorTiles.PUMP, TileEntityReactorPump.MINPOWER, TileEntityReactorPump.MINTORQUE);
		addNotes(ReactorTiles.EXCHANGER, TileEntityHeatExchanger.MINPOWER, TileEntityHeatExchanger.MINSPEED);
		addNotes(ReactorTiles.FUEL, TileEntityFuelRod.MELTDOWN);
		addNotes(ReactorTiles.CPU, TileEntityCPU.POWERPERROD);
		addNotes(ReactorTiles.BREEDER, TileEntityBreederCore.MELTDOWN);
		addNotes(ReactorTiles.HEATER, TileEntityFusionHeater.PLASMA_TEMP);
		addNotes(ReactorTiles.SOLENOID, TileEntitySolenoidMagnet.MINOMEGA, TileEntitySolenoidMagnet.MINTORQUE, TileEntitySolenoidMagnet.MAX_SPEED);
		//addData(ReactorTiles.HEAVYPUMP, TileEntityHeavyPump.MINDEPTH, TileEntityHeavyPump.MAXY);
		addData(ReactorTiles.BOILER, TileEntityReactorBoiler.DETTEMP);
		addData(ReactorTiles.FLUIDEXTRACTOR, TileEntityHeavyPump.HeavyWaterExtraction.MAXY, TileEntityHeavyPump.HeavyWaterExtraction.MINDEPTH);
		addNotes(ReactorTiles.FLUIDEXTRACTOR, TileEntityHeavyPump.MINPOWER, TileEntityHeavyPump.MINTORQUE);
		addNotes(ReactorTiles.TURBINEMETER, TileEntityTurbineCore.GEN_OMEGA, TileEntityHiPTurbine.GEN_OMEGA);
		addData(ReactorTiles.FUELDUMP, TileEntityThoriumCore.FUEL_DUMP_TEMPERATURE);
		addData(ReactorTiles.SOLARTOP, ReactorTiles.SOLARTOP.getName());
		addData(ReactorTiles.SOLAR, ReactorTiles.SOLAR.getName());
		addNotes(ReactorTiles.SOLAR, TileEntitySolarExchanger.MINPOWER, TileEntitySolarExchanger.MINSPEED);
		addNotes(ReactorTiles.WASTEDECAYER, TileEntityWasteDecayer.OPTIMAL_TEMP);

		notes.put(ReactorBook.SHIELDING, RadiationShield.getDataAsString());
	}
}
