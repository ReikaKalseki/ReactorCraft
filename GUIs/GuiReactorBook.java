/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Registry.CraftingItems;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.HandbookAuxData;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.MachineRecipeRenderer;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesBlastFurnace;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesBlastFurnace.BlastCrafting;
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
		return h.isMachine() ? 1 : 0;
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
		ReactorBook h = (ReactorBook)this.getEntry();
		if (h.isMachine()) {
			List<ItemStack> out = ReikaJavaLibrary.makeListFrom(h.getMachine().getCraftedProduct());
			if (out == null || out.size() <= 0)
				return;
			ReikaGuiAPI.instance.drawCustomRecipes(ri, fontRendererObj, out, HandbookAuxData.getWorktable(), posX+72-18, posY+18, posX-1620, posY+32);
		}
		if (this.getGuiLayout() == PageType.CRAFTING) {
			List<ItemStack> out = ReikaJavaLibrary.makeListFrom(h.getItem().getStackOf());
			if (out == null || out.size() <= 0)
				return;
			ReikaGuiAPI.instance.drawCustomRecipes(ri, fontRendererObj, out, CraftingManager.getInstance().getRecipeList(), posX+72, posY+18, posX+162, posY+32);
		}
		if (h == ReactorBook.MAGNET) {
			ItemStack in = ReactorStacks.lodestone;
			ItemStack out = ReactorItems.MAGNET.getStackOf();
			int k = (int)((System.nanoTime()/2000000000)%ReactorItems.MAGNET.getNumberMetadatas());
			if (k != 0) {
				in = ReactorItems.MAGNET.getStackOfMetadata(k-1);
				out = ReactorItems.MAGNET.getStackOfMetadata(k);
			}
			MachineRecipeRenderer.instance.drawCompressor(posX+66, posY+14, in, posX+120, posY+41, out);
		}
		if (h == ReactorBook.PELLET) {
			ItemStack in = CraftingItems.GRAPHITE.getItem();
			ItemStack in2 = CraftingItems.UDUST.getItem();
			ItemStack out = ReactorItems.PELLET.getStackOf();
			BlastCrafting r = RecipesBlastFurnace.getRecipes().getAllCraftingMaking(out).get(0);
			MachineRecipeRenderer.instance.drawBlastFurnaceCrafting(posX+99, posY+18, posX+180, posY+32, r);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, r.temperature+"C", posX+56, posY+66, 0);
		}
	}

	@Override
	protected void doRenderMachine(double x, double y, HandbookEntry he) {
		ReactorBook h = (ReactorBook)he;
		ReactorTiles rt = h.getMachine();
		if (rt != null) {
			TileEntity te = rt.createTEInstanceForRender();
			double sc = 48;
			int r = (int)(System.nanoTime()/20000000)%360;
			double a = 0;
			double b = 0;
			double c = 0;
			if (rt == ReactorTiles.STEAMLINE) {
				a = 0.33;
				b = 0.25;
				c = 0.33;
			}
			if (rt == ReactorTiles.CONTROL)
				b = -0.1875;
			if (rt.hasRender() && !rt.isPipe()) {
				double dx = -x;
				double dy = -y-21;
				double dz = 0;
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(sc, -sc, sc);
				GL11.glRotatef(renderq, 1, 0, 0);
				GL11.glRotatef(r, 0, 1, 0);
				GL11.glTranslated(a, b, c);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, 0, -0.5, 0);
				GL11.glTranslated(-a, -b, -c);
				GL11.glRotatef(-r, 0, 1, 0);
				GL11.glRotatef(-renderq, 1, 0, 0);
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
			}
			else {
				double dx = x;
				double dy = y;
				double dz = 0;
				GL11.glTranslated(dx, dy, dz);
				GL11.glScaled(sc, -sc, sc);
				GL11.glRotatef(renderq, 1, 0, 0);
				GL11.glRotatef(r, 0, 1, 0);
				ReikaTextureHelper.bindTerrainTexture();
				GL11.glTranslated(a, b, c);
				rb.renderBlockAsItem(rt.getBlockInstance(), rt.getBlockMetadata(), 1);
				GL11.glTranslated(-a, -b, -c);
				GL11.glRotatef(-r, 0, 1, 0);
				GL11.glRotatef(-renderq, 1, 0, 0);
				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
				GL11.glTranslated(-dx, -dy, -dz);
			}
		}
	}

	@Override
	protected void drawAuxGraphics(int posX, int posY) {
		ReactorBook h = (ReactorBook)this.getEntry();
		ReikaGuiAPI api = ReikaGuiAPI.instance;

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
		if (h.isParent())
			return PageType.PLAIN;
		if (subpage == 1)
			return PageType.PLAIN;
		if (h.isMachine())
			return PageType.MACHINERENDER;
		if (h.getParent() == ReactorBook.TOOLDESC)
			return PageType.CRAFTING;
		if (h == ReactorBook.FUEL)
			return PageType.CRAFTING;
		if (h == ReactorBook.BREEDERFUEL)
			return PageType.CRAFTING;
		if (h == ReactorBook.MAGNET)
			return PageType.COMPACTOR;
		if (h == ReactorBook.PELLET)
			return PageType.BLASTFURNACE;
		return PageType.PLAIN;
	}

	@Override
	protected void bindTexture() {
		ReactorBook h = (ReactorBook)this.getEntry();
		if (h == ReactorBook.FUSIONINFO) {
			ReikaTextureHelper.bindTexture(ReactorCraft.class, "Textures/GUI/Handbook/fusion.png");
		}
		else if (h == ReactorBook.FISSIONINFO) {
			ReikaTextureHelper.bindTexture(ReactorCraft.class, "Textures/GUI/Handbook/fission.png");
		}
		else
			super.bindTexture();
	}

	@Override
	public List<HandbookEntry> getAllTabsOnScreen() {
		List<ReactorBook> li = ReactorBook.getEntriesForScreen(screen);
		return new ArrayList(li);
	}

}
