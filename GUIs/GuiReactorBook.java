/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;
import Reika.RotaryCraft.GUIs.GuiHandbook;

public class GuiReactorBook extends GuiHandbook {

	public GuiReactorBook(EntityPlayer p5ep, World world, int s, int p) {
		super(p5ep, world, s, p);
	}

	@Override
	protected void reloadXMLData() {
		ReactorDescriptions.reload();
	}

	@Override
	protected void addTabButtons(int j, int k) {
		ReactorBook.addRelevantButtons(j, k, screen, buttonList);
	}

	@Override
	public int getMaxPage() {
		return ReactorBook.RESOURCEDESC.getScreen()+ReactorBook.RESOURCEDESC.getNumberChildren()/8;
	}

	@Override
	public int getMaxSubpage() {
		ReactorBook h = ReactorBook.getFromScreenAndPage(screen, page);
		return 1;
	}

	@Override
	protected int getNewScreenByTOCButton(int id) {
		switch(id) {
		case 1:
			return ReactorBook.INTRO.getScreen();
		case 2:
			return ReactorBook.PROCDESC.getScreen();
		case 3:
			return ReactorBook.GENDESC.getScreen();
		case 4:
			return ReactorBook.HTGRDESC.getScreen();
		case 5:
			return ReactorBook.FISSIONDESC.getScreen();
		case 6:
			return ReactorBook.BREEDERDESC.getScreen();
		case 7:
			return ReactorBook.FUSIONDESC.getScreen();
		case 8:
			return ReactorBook.ACCDESC.getScreen();
		case 9:
			return ReactorBook.TOOLDESC.getScreen();
		case 10:
			return ReactorBook.RESOURCEDESC.getScreen();
		}
		return 0;
	}

	@Override
	protected boolean isOnTOC() {
		return this.getEntry() == ReactorBook.TOC;
	}

	@Override
	protected void drawAuxData(int posX, int posY) {

	}

	@Override
	protected void doRenderMachine(double x, double y, HandbookEntry he) {

	}

	@Override
	protected void drawAuxGraphics(int posX, int posY) {

	}

	@Override
	protected HandbookEntry getEntry() {
		return ReactorBook.getFromScreenAndPage(screen, page);
	}

	@Override
	public boolean isLimitedView() {
		return false;
	}

	@Override
	protected PageType getGuiLayout() {
		ReactorBook h = (ReactorBook)this.getEntry();
		if (subpage == 1)
			return PageType.PLAIN;
		if (h.isMachine());
		return PageType.PLAIN;
	}

	@Override
	public List<HandbookEntry> getAllTabsOnScreen() {
		List<ReactorBook> li = ReactorBook.getEntriesForScreen(screen);
		return new ArrayList(li);
	}

}
