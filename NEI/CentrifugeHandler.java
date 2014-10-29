/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.NEI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.GUIs.GuiCentrifuge;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CentrifugeHandler extends TemplateRecipeHandler {

	public class CentrifugeRecipe extends CachedRecipe {

		public CentrifugeRecipe() {

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
			stacks.add(new PositionedStack(ReactorStacks.depdust, 111, 51));
			stacks.add(new PositionedStack(ReactorStacks.fueldust, 39, 51));
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
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ReactorCraft.class, this.getGuiTexture());
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 11, 166, 70, 0);
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
	public void drawExtras(int recipe)
	{
		ReikaGuiAPI.instance.drawTexturedModalRect(75, 7, 224, 20, 16, 60);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (ReikaItemHelper.matchStacks(ReactorStacks.depdust, result))
			arecipes.add(new CentrifugeRecipe());
		if (ReikaItemHelper.matchStacks(ReactorStacks.fueldust, result))
			arecipes.add(new CentrifugeRecipe());
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ReikaItemHelper.matchStacks(ReactorStacks.uf6can, ingredient))
			arecipes.add(new CentrifugeRecipe());
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCentrifuge.class;
	}

}
