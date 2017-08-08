/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class ReactorItemRenderer implements IItemRenderer {


	public ReactorItemRenderer() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float a = 0; float b = 0;

		RenderBlocks rb = (RenderBlocks)data[0];
		if (type == type.ENTITY) {
			a = -0.5F;
			b = -0.5F;
			GL11.glScalef(0.5F, 0.5F, 0.5F);
		}
		if (item.getItemDamage() >= ReactorTiles.TEList.length)
			return;
		ReactorTiles machine = ReactorTiles.TEList[item.getItemDamage()];
		if (machine.isPipe()) {
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(ReactorBlocks.DUCT.getBlockInstance(), machine.getBlockMetadata(), 1);
		}
		else if (machine.hasRender())
			TileEntityRendererDispatcher.instance.renderTileEntityAt(machine.createTEInstanceForRender(), a, -0.1D, b, 0.0F);
		else {
			ReikaTextureHelper.bindTerrainTexture();
			if (type == type.EQUIPPED || type == type.EQUIPPED_FIRST_PERSON) {
				double d = 0.5;
				GL11.glTranslated(d, d, d);
			}
			rb.renderBlockAsItem(machine.getBlockInstance(), machine.getBlockMetadata(), 1);
		}
	}
}
