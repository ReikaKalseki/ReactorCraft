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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.GUIs.GuiElectrolyzer;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class ElectrolyzerHandler extends TemplateRecipeHandler {

	public class ElectrolyzerRecipe extends CachedRecipe {

		private Fluid input;
		private ItemStack itemIn;

		private Fluid lightOut;
		private Fluid heavyOut;

		public ElectrolyzerRecipe(Fluid in) {
			input = in;

			heavyOut = FluidRegistry.getFluid("rc oxygen");
			lightOut = FluidRegistry.getFluid("rc deuterium");
		}

		public ElectrolyzerRecipe(ItemStack in) {
			itemIn = in;

			heavyOut = FluidRegistry.getFluid("rc sodium");
			lightOut = FluidRegistry.getFluid("rc chlorine");
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public PositionedStack getIngredient()
		{
			return itemIn != null ? new PositionedStack(itemIn, 39, 30) : null;
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
			arecipes.add(new ElectrolyzerRecipe(ItemStacks.salt));
			arecipes.add(new ElectrolyzerRecipe(FluidRegistry.getFluid("rc heavy water")));
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
		Fluid o1 = er.heavyOut;
		Fluid o2 = er.lightOut;

		ReikaTextureHelper.bindTerrainTexture();
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(93, 7, o1.getIcon(), 16, 60);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(129, 7, o2.getIcon(), 16, 60);

		if (er.input != null) {
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(12, 7, er.input.getIcon(), 16, 60);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (ReikaItemHelper.matchStacks(ReactorStacks.nacan, result) || ReikaItemHelper.matchStacks(ReactorStacks.clcan, result))
			arecipes.add(new ElectrolyzerRecipe(ItemStacks.salt));
		if (ReikaItemHelper.matchStacks(ReactorStacks.h2can, result) || ReikaItemHelper.matchStacks(ReactorStacks.ocan, result))
			arecipes.add(new ElectrolyzerRecipe(FluidRegistry.getFluid("rc heavy water")));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (TileEntityElectrolyzer.isSalt(ingredient))
			arecipes.add(new ElectrolyzerRecipe(ingredient));
		if (ReikaItemHelper.matchStacks(ingredient, ReactorItems.BUCKET.getStackOfMetadata(0)))
			arecipes.add(new ElectrolyzerRecipe(FluidRegistry.getFluid("rc heavy water")));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiElectrolyzer.class;
	}

}
