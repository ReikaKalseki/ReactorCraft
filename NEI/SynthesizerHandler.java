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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.GUIs.GuiSynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer.FluidSynthesis;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class SynthesizerHandler extends TemplateRecipeHandler {

	public class SynthesizerRecipe extends CachedRecipe {

		public final FluidSynthesis recipe;

		public SynthesizerRecipe(FluidSynthesis rec) {
			recipe = rec;
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
			ItemStack a = recipe.getAForDisplay();
			ItemStack b = recipe.getBForDisplay();
			if (a != null)
				stacks.add(new PositionedStack(a, 75, 33));
			if (b != null)
				stacks.add(new PositionedStack(b, 75, 15));
			return stacks;
		}
	}

	@Override
	public String getRecipeName() {
		return "Ammonia Synthesizer";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ReactorCraft/Textures/GUI/synthesizer.png";
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
	public void loadCraftingRecipes(ItemStack result) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(result);
		if (fs != null) {
			for (FluidSynthesis rec : FluidSynthesis.values()) {
				if (rec.output.equals(fs.getFluid())) {
					arecipes.add(new SynthesizerRecipe(rec));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(ingredient);
		for (FluidSynthesis rec : FluidSynthesis.values()) {
			if (fs != null && rec.input.equals(fs.getFluid())) {
				arecipes.add(new SynthesizerRecipe(rec));
			}
			else if (rec.usesItem(ingredient)) {
				arecipes.add(new SynthesizerRecipe(rec));
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiSynthesizer.class;
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(95, 8, 30, 60), "rcammonia"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("rcammonia")) {
			for (FluidSynthesis rec : FluidSynthesis.values())
				arecipes.add(new SynthesizerRecipe(rec));
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("rcammonia")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void drawExtras(int recipe) {
		SynthesizerRecipe rec = (SynthesizerRecipe)arecipes.get(recipe);
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		//ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.input.getLocalizedName(), 20, 70, 0);
		//ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.output.getLocalizedName(), 137, 70, 0);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.fluidConsumed+"mB", 20, 70, 0);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.fluidProduced+"mB", 137, 70, 0);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.minTemp+"C", 80, 65, 0);
		int j = 130;
		int k = 53;
		if (ReikaGuiAPI.instance.isMouseInBox(j+12, j+28, k+7, k+67)) {
			//ReikaGuiAPI.instance.drawTooltip(fr, rec.recipe.input.getLocalizedName());
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.input.getLocalizedName(), 80, 80, 0);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+129, j+145, k+7, k+67)) {
			//ReikaGuiAPI.instance.drawTooltip(fr, rec.recipe.output.getLocalizedName());
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, rec.recipe.output.getLocalizedName(), 80, 80, 0);
		}
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glColor4f(1, 1, 1, 1);
		IIcon input = ReikaLiquidRenderer.getFluidIconSafe(rec.recipe.input);
		IIcon output = ReikaLiquidRenderer.getFluidIconSafe(rec.recipe.output);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(12, 7, input, 16, 60);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(129, 7, output, 16, 60);
	}

}
