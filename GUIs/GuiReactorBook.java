/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorDescriptions;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.ReactorStructures;
import Reika.ReactorCraft.Registry.CraftingItems;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.HandbookAuxData;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.MachineRecipeRenderer;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesBlastFurnace;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipesBlastFurnace.BlastCrafting;
import Reika.RotaryCraft.GUIs.GuiHandbook;
import Reika.RotaryCraft.Registry.BlockRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class GuiReactorBook extends GuiHandbook {

	private int structureMode;

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
	public int getMaxScreen() {
		return ReactorBook.RESOURCEDESC.getScreen()+ReactorBook.RESOURCEDESC.getNumberChildren()/GuiHandbook.PAGES_PER_SCREEN;
	}

	@Override
	protected void onInitGui(int j, int k, HandbookEntry h) {
		for (int i = 0; i < ReactorStructures.structureList.length; i++) {
			StructureRenderer s = ReactorStructures.structureList[i].getRenderer();
			s.resetRotation();
			if (h != ReactorBook.STRUCTURES)
				s.reset();
		}

		if (h == ReactorBook.STRUCTURES && subpage > 0) {
			buttonList.add(new GuiButton(20, j+xSize-77, k+6, 20, 20, "3D"));
			buttonList.add(new GuiButton(21, j+xSize-57, k+6, 20, 20, "2D"));
			buttonList.add(new GuiButton(22, j+xSize-97, k+6, 20, 20, "N#"));

			if (structureMode == 1) {
				buttonList.add(new GuiButton(23, j+xSize-77, k+40, 20, 20, "+"));
				buttonList.add(new GuiButton(24, j+xSize-57, k+40, 20, 20, "-"));
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id == 20) {
			structureMode = 0;
			this.initGui();
			ReactorStructures.structureList[subpage-1].getRenderer().resetStepY();
			return;
		}
		else if (b.id == 21) {
			structureMode = 1;
			this.initGui();
			ReactorStructures.structureList[subpage-1].getRenderer().resetStepY();
			return;
		}
		else if (b.id == 22) {
			structureMode = 2;
			this.initGui();
			ReactorStructures.structureList[subpage-1].getRenderer().resetStepY();
			return;
		}
		else if (b.id == 23) {
			ReactorStructures.structureList[subpage-1].getRenderer().incrementStepY();
			this.initGui();
			return;
		}
		else if (b.id == 24) {
			ReactorStructures.structureList[subpage-1].getRenderer().decrementStepY();
			this.initGui();
			return;
		}
		super.actionPerformed(b);
	}

	@Override
	public int getMaxSubpage() {
		ReactorBook h = ReactorBook.getFromScreenAndPage(screen, page);
		if (h == ReactorBook.STRUCTURES) {
			return ReactorStructures.structureList.length;
		}
		if (h == ReactorBook.SHIELDING)
			return 1;
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
				return ReactorBook.THORIUMDESC.getScreen();
			case 8:
				return ReactorBook.FUSIONDESC.getScreen();
			case 9:
				return ReactorBook.ACCDESC.getScreen();
			case 10:
				return ReactorBook.TOOLDESC.getScreen();
			case 11:
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
		else if (h == ReactorBook.PELLET) {
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
	protected void drawAuxGraphics(int posX, int posY, float ptick) {
		ReactorBook h = (ReactorBook)this.getEntry();
		ReikaGuiAPI api = ReikaGuiAPI.instance;

		if (h == ReactorBook.STRUCTURES) {
			if (subpage == 0) {

			}
			else {
				ReactorStructures s = ReactorStructures.structureList[subpage-1];
				StructureRenderer r = s.getRenderer();

				fontRendererObj.drawString(s.getName(), posX+8, posY+16, 0x000000);

				if (structureMode == 0) {

					if (Mouse.isButtonDown(0) && this.getGuiTick() > 2) {
						r.rotate(0.25*Mouse.getDY(), 0.25*Mouse.getDX(), 0);
					}
					else if (Mouse.isButtonDown(1)) {
						r.resetRotation();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
						r.rotate(0, 0.75, 0);
					}
					else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
						r.rotate(0, -0.75, 0);
					}
					else if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
						r.rotate(-0.75, 0, 0);
					}
					else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
						r.rotate(0.75, 0, 0);
					}

					r.draw3D(posX, posY, ptick, true);
				}
				else if (structureMode == 1) {
					r.drawSlice(posX, posY);
				}
				else if (structureMode == 2) {
					this.drawTally(s, posX, posY);
				}
			}
		}
	}

	private void drawTally(ReactorStructures s, int j, int k) {
		ItemHashMap<Integer> map = s.getStructure(worldObj, 0, 0, 0, ForgeDirection.EAST).tally();
		int i = 0;
		int n = 8;
		List<ItemStack> c = new ArrayList(map.keySet());
		Collections.sort(c, ReikaItemHelper.comparator);
		for (ItemStack is : c) {
			int dx = j+10+(i/n)*50;
			int dy = k+30+(i%n)*22;
			ItemStack is2 = is.copy();
			ReactorBlocks b = ReactorBlocks.getFromItem(is2);
			if (b != null && b.isMachine()) {
				is2 = ReactorTiles.getMachineFromIDandMetadata(b.getBlockInstance(), is2.getItemDamage()).getCraftedProduct();
			}
			BlockRegistry b2 = BlockRegistry.getFromItem(is2);
			if (b2 != null && b2.isMachine()) {
				is2 = MachineRegistry.getMachineFromIDandMetadata(b2.getBlockInstance(), is2.getItemDamage()).getCraftedProduct();
			}
			ReikaGuiAPI.instance.drawItemStackWithTooltip(itemRender, fontRendererObj, is2, dx, dy);
			fontRendererObj.drawString(String.valueOf(map.get(is)), dx+20, dy+5, 0x000000);
			i++;
		}
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
		if (this.isOnTOC())
			return PageType.TOC;
		if (h.isParent())
			return PageType.PLAIN;
		if (h == ReactorBook.STRUCTURES && subpage > 0)
			return PageType.SOLID;
		if (subpage >= 1)
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
