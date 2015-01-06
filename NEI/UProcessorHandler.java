/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.IC2Handler;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.GUIs.GuiProcessor;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class UProcessorHandler extends TemplateRecipeHandler {

	public class UProcessorRecipe extends CachedRecipe {

		public UProcessorRecipe() {

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
			List<ItemStack> li = ReikaRecipeHelper.getMutableOreDictList("ingotUranium");
			li.add(0, ReactorOres.PITCHBLENDE.getProduct());
			if (ModList.IC2.isLoaded()) {
				li.add(IC2Handler.getInstance().getPurifiedCrushedUranium());
			}
			int meta = (int)((System.nanoTime()/1000000000)%li.size());
			ItemStack i = li.get(meta);
			stacks.add(new PositionedStack(i, 39, 47));
			meta = (int)((System.nanoTime()/1000000000)%FluoriteTypes.colorList.length);
			ItemStack f = ReactorItems.FLUORITE.getStackOfMetadata(meta);
			stacks.add(new PositionedStack(f, 39, 11));
			return stacks;
		}
	}

	@Override
	public String getRecipeName() {
		return "Uranium Processor";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ReactorCraft/Textures/GUI/processor.png";
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
		if (ReikaItemHelper.matchStacks(ReactorStacks.uf6can, result))
			arecipes.add(new UProcessorRecipe());
		if (ReikaItemHelper.matchStacks(ReactorStacks.hfcan, result))
			arecipes.add(new UProcessorRecipe());
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (TileEntityUProcessor.isUF6Ingredient(ingredient))
			arecipes.add(new UProcessorRecipe());
		if (ReikaItemHelper.matchStacks(ReactorStacks.hfcan, ingredient))
			arecipes.add(new UProcessorRecipe());
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiProcessor.class;
	}

	@Override
	public void drawExtras(int recipe)
	{
		ReikaGuiAPI.instance.drawTexturedModalRect(93, 7, 208, 20, 16, 60);
		ReikaGuiAPI.instance.drawTexturedModalRect(93+18, 7, 208-16, 20, 16, 60);
		ReikaGuiAPI.instance.drawTexturedModalRect(93+36, 7, 208+16, 20, 16, 60);
	}

}
