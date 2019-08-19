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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.GUIs.GuiElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer.Electrolysis;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class ElectrolyzerHandler extends TemplateRecipeHandler {

	public class ElectrolyzerRecipe extends CachedRecipe {

		private final Electrolysis recipe;

		public ElectrolyzerRecipe(Electrolysis in) {
			recipe = in;
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public PositionedStack getIngredient() {
			return recipe.hasItemRequirement() ? new PositionedStack(recipe.getItemListForDisplay(), 39, 30) : null;
		}
	}

	@Override
	public String getRecipeName() {
		return "Electrolyzer";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ReactorCraft/Textures/GUI/electrolyzer.png";
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
		transferRects.add(new RecipeTransferRect(new Rectangle(59, 8, 70, 60), "recelect"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("recelect")) {
			for (Electrolysis e : Electrolysis.getRecipes())
				arecipes.add(new ElectrolyzerRecipe(e));
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("recelect")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void drawExtras(int recipe)
	{
		//ReikaGuiAPI.instance.drawTexturedModalRect(75, 7, 224, 20, 16, 60);
		ElectrolyzerRecipe er = (ElectrolyzerRecipe)arecipes.get(recipe);
		Fluid o1 = er.recipe.lowerOutput != null ? er.recipe.lowerOutput.getFluid() : null;
		Fluid o2 = er.recipe.upperOutput != null ? er.recipe.upperOutput.getFluid() : null;

		ReikaTextureHelper.bindTerrainTexture();
		if (o1 != null)
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(93, 7, ReikaLiquidRenderer.getFluidIconSafe(o1), 16, 60);
		if (o2 != null)
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(129, 7, ReikaLiquidRenderer.getFluidIconSafe(o2), 16, 60);

		if (er.recipe.requiredFluid != null) {
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(12, 7, ReikaLiquidRenderer.getFluidIconSafe(er.recipe.requiredFluid.getFluid()), 16, 60);
		}

		if (er.recipe.consumeItem) {
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, "(Use)", 48, 51, 0x000000);
		}
		else {
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, "(Cat)", 48, 51, 0x000000);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(result);
		if (fs != null) {
			for (Electrolysis e : Electrolysis.getRecipes()) {
				if (e.makes(fs.getFluid())) {
					arecipes.add(new ElectrolyzerRecipe(e));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (Electrolysis e : Electrolysis.getRecipes()) {
			if (e.uses(ingredient)) {
				arecipes.add(new ElectrolyzerRecipe(e));
			}
		}
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(ingredient);
		if (fs != null) {
			for (Electrolysis e : Electrolysis.getRecipes()) {
				if (e.uses(fs.getFluid())) {
					arecipes.add(new ElectrolyzerRecipe(e));
				}
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiElectrolyzer.class;
	}

}
