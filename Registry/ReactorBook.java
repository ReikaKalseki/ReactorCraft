/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.ReactorCraft.Auxiliary.ReactorBookData;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;

public enum ReactorBook implements HandbookEntry {

	//---------------------TOC--------------------//
	TOC("Table Of Contents", ""),
	INFO(ReactorItems.BOOK.getStackOf()),
	PROCESSING(ReactorTiles.PROCESSOR.getCraftedProduct()),
	POWERGEN(ReactorTiles.TURBINECORE.getCraftedProduct()),
	HTGRS(ReactorTiles.PEBBLEBED.getCraftedProduct()),
	FISSION(ReactorTiles.FUEL.getCraftedProduct()),
	BREEDER(ReactorTiles.BREEDER.getCraftedProduct()),
	FUSION(ReactorTiles.MAGNET.getCraftedProduct()),
	ACC(ReactorTiles.MAGNETPIPE.getCraftedProduct()),
	TOOLS(ReactorItems.GOGGLES.getStackOf()),
	RESOURCE(ReactorItems.FUEL.getStackOf()),
	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	PHYSICS("Nuclear Physics", Item.book),
	FISSIONINFO("Nuclear Fission", ReactorItems.FUEL),
	FUSIONINFO("Nuclear Fusion", ReactorStacks.h2can),
	BASICS("Nuclear Power Basics", ReactorBlocks.STEAM.getBlockVariable()),
	ENRICHMENT("Uranium Enrichment", ReactorTiles.CENTRIFUGE),
	MELTDOWN("Meltdowns", MatBlocks.SLAG.getStackOf()),
	RADIATION("Radiation", ReactorItems.WASTE),

	//--------------------PROCESSING---------------//
	PROCDESC("Processing Machines", ""),
	PROCESSOR(ReactorTiles.PROCESSOR),
	CENTRIFUGE(ReactorTiles.CENTRIFUGE),
	ELECTROLYZER(ReactorTiles.ELECTROLYZER),
	SYNTHESIZER(ReactorTiles.SYNTHESIZER),
	TRITIZER(ReactorTiles.TRITIZER),

	GENDESC("Power Generation Machines", ""),
	BOILER(ReactorTiles.BOILER),
	STEAMLINE(ReactorTiles.STEAMLINE),
	STEAMGRATE(ReactorTiles.GRATE),
	TURBINE(ReactorTiles.TURBINECORE),
	CONDENSER(ReactorTiles.CONDENSER),
	HEATEXCHANGER(ReactorTiles.EXCHANGER),
	PUMP(ReactorTiles.PUMP),
	GENERATOR(ReactorTiles.GENERATOR),
	BIGTURBINE(ReactorTiles.BIGTURBINE),

	HTGRDESC("HTGR Components", ""),
	PEBBLEBED(ReactorTiles.PEBBLEBED),
	CO2HEATER(ReactorTiles.CO2HEATER),

	FISSIONDESC("Fission Reactor Components", ""),
	FUELROD(ReactorTiles.FUEL),
	CONTROLROD(ReactorTiles.CONTROL),
	WATERCELL(ReactorTiles.COOLANT),
	CPU(ReactorTiles.CPU),

	BREEDERDESC("Breeder Reactor Components", ""),
	BREEDERCORE(ReactorTiles.BREEDER),
	SODIUMHEATER(ReactorTiles.SODIUMBOILER),

	FUSIONDESC("Fusion Reactor Components", ""),
	FUSIONHEATER(ReactorTiles.HEATER),
	FUSIONINJECTOR(ReactorTiles.INJECTOR),
	TOROID(ReactorTiles.MAGNET),
	SOLENOID(ReactorTiles.SOLENOID),
	ABSORBER(ReactorTiles.ABSORBER),

	ACCDESC("Utility Machines", ""),
	GASCOLLECTOR(ReactorTiles.COLLECTOR),
	GASDUCT(ReactorTiles.GASPIPE),
	MAGNETPIPE(ReactorTiles.MAGNETPIPE),
	HEAVYPUMP(ReactorTiles.HEAVYPUMP),
	WASTECONTAINER(ReactorTiles.WASTECONTAINER),
	WASTESTORAGE(ReactorTiles.STORAGE),
	REFLECTOR(ReactorTiles.REFLECTOR),

	TOOLDESC("Tools", ""),
	GOGGLES(ReactorItems.GOGGLES),
	REMOTE(ReactorItems.REMOTE),

	RESOURCEDESC("Resource Items", ""),
	FLUORITE(ReactorItems.FLUORITE),
	FUEL(ReactorItems.FUEL),
	DEPLETED(ReactorItems.DEPLETED),
	WASTE(ReactorItems.WASTE),
	PLUTONIUM(ReactorItems.PLUTONIUM),
	THORIUM(ReactorItems.THORIUM),
	BREEDERFUEL(ReactorItems.BREEDERFUEL),
	MAGNET(ReactorItems.MAGNET),
	PELLET(ReactorItems.PELLET),
	OLDPELLET(ReactorItems.OLDPELLET);

	private final ItemStack iconItem;
	private final String pageTitle;
	private boolean isParent = false;
	private ReactorTiles machine;
	private ReactorItems item;

	public static final ReactorBook[] tabList = values();

	private ReactorBook() {
		this("");
	}

	private ReactorBook(ReactorTiles r) {
		this(r.getName(), r.getCraftedProduct());
		machine = r;
	}

	private ReactorBook(ReactorItems i) {
		this(i.getBasicName(), i.getStackOf());
		item = i;
	}


	private ReactorBook(ItemStack item) {
		this("", item);
	}

	private ReactorBook(String name, String s) {
		this(name);
		isParent = true;
	}

	private ReactorBook(String name) {
		this(name, (ItemStack)null);
	}

	private ReactorBook(String name, ReactorItems i) {
		this(name, i.getStackOf());
	}

	private ReactorBook(String name, ReactorTiles r) {
		this(name, r.getCraftedProduct());
	}

	private ReactorBook(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	private ReactorBook(String name, Block icon) {
		this(name, new ItemStack(icon));
	}

	private ReactorBook(String name, ItemStack icon) {
		iconItem = icon;
		pageTitle = name;
	}

	public static ReactorBook getFromScreenAndPage(int screen, int page) {
		//ReikaJavaLibrary.pConsole(screen+"   "+page);
		if (screen < INTRO.getScreen())
			return TOC;
		ReactorBook h = ReactorBookData.getMapping(screen, page);
		return h != null ? h : TOC;
	}

	public static void addRelevantButtons(int j, int k, int screen, List<GuiButton> li) {
		int id = 0;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen) {
				li.add(new ImagedGuiButton(id, j-20, k+id*20, 20, 20, 0, 0, tabList[i].getTabImageFile(), RotaryCraft.class));
				//ReikaJavaLibrary.pConsole("Adding "+tabList[i]+" with ID "+id+" to screen "+screen);
				id++;
			}
		}
	}

	public static List<ReactorBook> getEntriesForScreen(int screen) {
		//ReikaJavaLibrary.pConsole(screen);
		List<ReactorBook> li = new ArrayList<ReactorBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen) {
				li.add(tabList[i]);
			}
		}
		return li;
	}

	public static List<ReactorBook> getTOCTabs() {
		ArrayList<ReactorBook> li = new ArrayList<ReactorBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public static List<ReactorBook> getMachineTabs() {
		List<ReactorBook> tabs = new ArrayList<ReactorBook>();
		for (int i = 0; i < tabList.length; i++) {
			ReactorBook h = tabList[i];
			if (h.isMachine() && !h.isParent)
				tabs.add(h);
		}
		return tabs;
	}

	public static ReactorBook[] getToolTabs() {
		int size = RESOURCEDESC.ordinal()-TOOLDESC.ordinal()-1;
		ReactorBook[] tabs = new ReactorBook[size];
		System.arraycopy(tabList, TOOLDESC.ordinal()+1, tabs, 0, size);
		return tabs;
	}

	public static ReactorBook[] getResourceTabs() {
		int size = tabList.length-RESOURCEDESC.ordinal()-1;
		ReactorBook[] tabs = new ReactorBook[size];
		System.arraycopy(tabList, RESOURCEDESC.ordinal()+1, tabs, 0, size);
		return tabs;
	}

	public static ReactorBook[] getInfoTabs() {
		int size = PROCDESC.ordinal()-INTRO.ordinal()-1;
		ReactorBook[] tabs = new ReactorBook[size];
		System.arraycopy(tabList, INTRO.ordinal()+1, tabs, 0, size);
		return tabs;
	}

	public static List<ReactorBook> getCategoryTabs() {
		ArrayList<ReactorBook> li = new ArrayList<ReactorBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public boolean isMachine() {
		return machine != null;
	}

	public ReactorTiles getMachine() {
		return machine;
	}

	public ReactorItems getItem() {
		return item;
	}

	@Override
	public ItemStack getTabIcon() {
		return iconItem;
	}

	public String getData() {
		if (this == TOC)
			return ReactorDescriptions.getTOC();
		return ReactorDescriptions.getData(this);
	}

	public String getNotes() {
		return ReactorDescriptions.getNotes(this);
	}

	@Override
	public boolean sameTextAllSubpages() {
		return false;
	}

	@Override
	public String getTitle() {
		return pageTitle;
	}

	@Override
	public boolean hasMachineRender() {
		return this.isMachine();
	}

	@Override
	public boolean hasSubpages() {
		return this.isMachine();
	}

	public String getTabImageFile() {
		//return "/Reika/RotaryCraft/Textures/GUI/Handbook/tabs_"+this.getParent().name().toLowerCase()+".png";
		return "/Reika/RotaryCraft/Textures/GUI/Handbook/tabs_"+TOC.name().toLowerCase()+".png";
	}

	public int getRelativeScreen() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset/8;
	}

	public ReactorBook getParent() {
		ReactorBook parent = null;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent) {
				if (this.ordinal() >= tabList[i].ordinal()) {
					parent = tabList[i];
				}
			}
		}
		//ReikaJavaLibrary.pConsole("Setting parent for "+this+" to "+parent);
		return parent;
	}

	public boolean isParent() {
		return isParent;
	}

	public int getBaseScreen() {
		int sc = 0;
		for (int i = 0; i < this.ordinal(); i++) {
			ReactorBook h = tabList[i];
			if (h.isParent) {
				sc += h.getNumberChildren()/8+1;
			}
		}
		return sc;
	}

	public int getNumberChildren() {
		if (!isParent)
			return 0;
		int ch = 0;
		for (int i = this.ordinal()+1; i < tabList.length; i++) {
			ReactorBook h = tabList[i];
			if (h.isParent) {
				return ch;
			}
			else {
				ch++;
			}
		}
		return ch;
	}

	public int getRelativePage() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset;
	}

	public int getRelativeTabPosn() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset-this.getRelativeScreen()*8;
	}

	public int getScreen() {
		return this.getParent().getBaseScreen()+this.getRelativeScreen();
	}

	public int getPage() {
		return (this.ordinal()-this.getParent().ordinal())%8;
	}

}
