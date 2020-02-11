/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.NEI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.GUIs.GuiCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge.Centrifuging;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CentrifugeHandler extends TemplateRecipeHandler {

	public class CentrifugeRecipe extends CachedRecipe {

		private final Centrifuging recipe;

		public CentrifugeRecipe(Centrifuging e) {
			recipe = e;
		}

		@Override
		public PositionedStack getResult() {
			return null;//new PositionedStack(null, 131, 24);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return null;
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			if (recipe.getOutputB() != null)
				stacks.add(new PositionedStack(recipe.getOutputB(), 111, 51));
			stacks.add(new PositionedStack(recipe.getOutputA(), 39, 51));
			return stacks;
		}
	}

	@Override
	public String getRecipeName() {
		return "Centrifuge";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ReactorCraft/Textures/GUI/centrifuge.png";
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ReactorCraft.class, this.getGuiTexture());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 11, 166, 70, ReikaGuiAPI.NEI_DEPTH);
	}

	@Override
	public void drawForeground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindTexture(ReactorCraft.class, this.getGuiTexture());
		this.drawExtras(recipe);
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(59, 8, 40, 60), "recentri"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("recentri")) {
			for (Centrifuging e : TileEntityCentrifuge.getRecipes())
				arecipes.add(new CentrifugeRecipe(e));
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("recentri")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void drawExtras(int recipe) {
		//ReikaGuiAPI.instance.drawTexturedModalRect(75, 7, 224, 20, 16, 60);
		CentrifugeRecipe cr = (CentrifugeRecipe)arecipes.get(recipe);
		String ch = String.format("%.2f", cr.recipe.chanceOfAOverB)+"%";
		String ch2 = String.format("%.2f", 100-cr.recipe.chanceOfAOverB)+"%";
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, ch, 49, 70, 0x000000);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, ch2, 122, 70, 0x000000);

		ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, cr.recipe.fluidAmount+"mB", 85, 70, 0x000000);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, cr.recipe.minSpeed+" rad/s", 85, 85, 0x000000);

		IIcon ico = ReikaLiquidRenderer.getFluidIconSafe(cr.recipe.input);
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTerrainTexture();
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(75, 7, ico, 16, 60);
		ReikaTextureHelper.bindTexture(ReactorCraft.class, this.getGuiTexture());
		ReikaGuiAPI.instance.drawTexturedModalRect(75, 7, 223, 83, 16, 60);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (Centrifuging c : TileEntityCentrifuge.getRecipes()) {
			if (c.produces(result)) {
				arecipes.add(new CentrifugeRecipe(c));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ReikaItemHelper.matchStacks(ReactorStacks.uf6can, ingredient))
			arecipes.add(new CentrifugeRecipe(Centrifuging.UF6));
		else if (FluidContainerRegistry.isFilledContainer(ingredient)) {
			FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(ingredient);
			if (fs != null) {
				Centrifuging c = TileEntityCentrifuge.getRecipe(fs.getFluid());
				if (c != null) {
					arecipes.add(new CentrifugeRecipe(c));
				}
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCentrifuge.class;
	}

}
