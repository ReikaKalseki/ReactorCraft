/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.GUIs.GuiProcessor;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor.Processes;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class UProcessorHandler extends TemplateRecipeHandler {

	public class UProcessorRecipe extends CachedRecipe {

		private final Processes process;

		public UProcessorRecipe(Processes p) {
			process = p;
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
			List<ItemStack> li = process.getInputItemList();
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
		Processes p = TileEntityUProcessor.getProcessByFluidOutputItem(result);
		if (p != null) {
			arecipes.add(new UProcessorRecipe(p));
			return;
		}

		if (ReikaItemHelper.matchStacks(ReactorStacks.hfcan, result))
			arecipes.add(new UProcessorRecipe(Processes.UF6));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() == ReactorItems.FLUORITE.getItemInstance()) {
			for (int i = 0; i < Processes.list.length; i++) {
				arecipes.add(new UProcessorRecipe(Processes.list[i]));
			}
			return;
		}

		Processes p = TileEntityUProcessor.getProcessByMainItem(ingredient);
		if (p != null) {
			arecipes.add(new UProcessorRecipe(p));
			return;
		}

		p = TileEntityUProcessor.getProcessByFluidItem(ingredient);
		if (p != null) {
			arecipes.add(new UProcessorRecipe(p));
			return;
		}

		if (ReikaItemHelper.matchStacks(ReactorStacks.hfcan, ingredient))
			arecipes.add(new UProcessorRecipe(Processes.UF6));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiProcessor.class;
	}

	@Override
	public void drawExtras(int recipe)
	{
		UProcessorRecipe r = (UProcessorRecipe)arecipes.get(recipe);
		Processes p = r.process;

		ReikaTextureHelper.bindTerrainTexture();
		Fluid f = p.inputFluid;
		if (f != null)
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(93, 7, f.getIcon(), 16, 60);
		f = p.intermediateFluid;
		if (f != null)
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(93+18, 7, f.getIcon(), 16, 60);
		f = p.outputFluid;
		if (f != null)
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(93+36, 7, f.getIcon(), 16, 60);
	}

}
