/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ReactorCraft.Blocks.BlockTritiumLamp;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TritiumLampRenderer implements ISimpleBlockRenderingHandler {

	public final int renderID;

	public static int renderPass;

	public TritiumLampRenderer(int id) {
		renderID = id;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = block.getIcon(0, metadata);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addTranslation(-0.5F, -0.5F, -0.5F);

		if (metadata >= FluoriteTypes.colorList.length) {
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			v5.setNormal(0, 1, 0);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, u, dv);

			v5.addVertexWithUV(0, 1, 1, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, du, v);
			v5.addVertexWithUV(0, 1, 0, u, v);

			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.addVertexWithUV(0, 0, 1, u, dv);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 1, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, u, v);

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.addVertexWithUV(1, 1, 0, u, v);
			v5.addVertexWithUV(1, 1, 1, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 0, 0, u, dv);
		}

		ico = ((BlockTritiumLamp)block).getTopIcon();
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();
		double d = 0.0025;
		v5.setNormal(0, 0.6F, 0);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(0, 1+d, 1, u, dv);
		v5.addVertexWithUV(1, 1+d, 1, du, dv);
		v5.addVertexWithUV(1, 1+d, 0, du, v);
		v5.addVertexWithUV(0, 1+d, 0, u, v);

		ico = ((BlockTritiumLamp)block).getBottomIcon();
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();
		v5.addVertexWithUV(0, -d, 0, u, v);
		v5.addVertexWithUV(1, -d, 0, du, v);
		v5.addVertexWithUV(1, -d, 1, du, dv);
		v5.addVertexWithUV(0, -d, 1, u, dv);


		ico = ((BlockTritiumLamp)block).getFrameIcon();
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();

		v5.setColorOpaque_I(0xbfbfbf);
		v5.addVertexWithUV(0, 1, -d, u, dv);
		v5.addVertexWithUV(1, 1, -d, du, dv);
		v5.addVertexWithUV(1, 0, -d, du, v);
		v5.addVertexWithUV(0, 0, -d, u, v);

		v5.setColorOpaque_I(0xbfbfbf);
		v5.addVertexWithUV(0, 0, 1+d, u, dv);
		v5.addVertexWithUV(1, 0, 1+d, du, dv);
		v5.addVertexWithUV(1, 1, 1+d, du, v);
		v5.addVertexWithUV(0, 1, 1+d, u, v);

		v5.setColorOpaque_I(0xdfdfdf);
		v5.addVertexWithUV(-d, 0, 0, u, v);
		v5.addVertexWithUV(-d, 0, 1, du, v);
		v5.addVertexWithUV(-d, 1, 1, du, dv);
		v5.addVertexWithUV(-d, 1, 0, u, dv);

		v5.setColorOpaque_I(0xdfdfdf);
		v5.addVertexWithUV(1+d, 1, 0, u, v);
		v5.addVertexWithUV(1+d, 1, 1, du, v);
		v5.addVertexWithUV(1+d, 0, 1, du, dv);
		v5.addVertexWithUV(1+d, 0, 0, u, dv);

		v5.addTranslation(0.5F, 0.5F, 0.5F);

		v5.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		//rb.renderStandardBlockWithColorMultiplier(block, x, y, z, 1, 1, 1);
		v5.addTranslation(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		v5.setBrightness(meta >= FluoriteTypes.colorList.length ? 240 : block.getMixedBrightnessForBlock(world, x, y, z));
		if (renderPass == 1) {
			v5.setColorRGBA_I(0xffffff, 192);

			IIcon ico = block.getIcon(0, meta);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, u, dv);

			v5.addVertexWithUV(0, 1, 1, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, du, v);
			v5.addVertexWithUV(0, 1, 0, u, v);

			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.addVertexWithUV(0, 0, 1, u, dv);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 1, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, u, v);

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.addVertexWithUV(1, 1, 0, u, v);
			v5.addVertexWithUV(1, 1, 1, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 0, 0, u, dv);
		}
		else {
			IIcon ico = ((BlockTritiumLamp)block).getTopIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			double d = 0.0025;
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(-d, 1+d, 1+d, u, dv);
			v5.addVertexWithUV(1+d, 1+d, 1+d, du, dv);
			v5.addVertexWithUV(1+d, 1+d, -d, du, v);
			v5.addVertexWithUV(-d, 1+d, -d, u, v);

			ico = ((BlockTritiumLamp)block).getBottomIcon();
			u = ico.getMinU();
			v = ico.getMinV();
			du = ico.getMaxU();
			dv = ico.getMaxV();
			v5.addVertexWithUV(-d, -d, -d, u, v);
			v5.addVertexWithUV(1+d, -d, -d, du, v);
			v5.addVertexWithUV(1+d, -d, 1+d, du, dv);
			v5.addVertexWithUV(-d, -d, 1+d, u, dv);


			ico = ((BlockTritiumLamp)block).getFrameIcon();
			u = ico.getMinU();
			v = ico.getMinV();
			du = ico.getMaxU();
			dv = ico.getMaxV();

			v5.setColorOpaque_I(0xbfbfbf);
			v5.addVertexWithUV(-d, 1+d, -d, u, dv);
			v5.addVertexWithUV(1+d, 1+d, -d, du, dv);
			v5.addVertexWithUV(1+d, -d, -d, du, v);
			v5.addVertexWithUV(-d, -d, -d, u, v);

			v5.setColorOpaque_I(0xbfbfbf);
			v5.addVertexWithUV(-d, -d, 1+d, u, dv);
			v5.addVertexWithUV(1+d, -d, 1+d, du, dv);
			v5.addVertexWithUV(1+d, 1+d, 1+d, du, v);
			v5.addVertexWithUV(-d, 1+d, 1+d, u, v);

			v5.setColorOpaque_I(0xdfdfdf);
			v5.addVertexWithUV(-d, -d, -d, u, v);
			v5.addVertexWithUV(-d, -d, 1+d, du, v);
			v5.addVertexWithUV(-d, 1+d, 1+d, du, dv);
			v5.addVertexWithUV(-d, 1+d, -d, u, dv);

			v5.setColorOpaque_I(0xdfdfdf);
			v5.addVertexWithUV(1+d, 1+d, -d, u, v);
			v5.addVertexWithUV(1+d, 1+d, 1+d, du, v);
			v5.addVertexWithUV(1+d, -d, 1+d, du, dv);
			v5.addVertexWithUV(1+d, -d, -d, u, dv);

			/*
			rb.enableAO = false;
			v5.addTranslation(-x, -y, -z);
			rb.renderFaceXNeg(block, x-d, y, z, ((BlockTritiumLamp)block).getFrameIcon());
			rb.renderFaceXPos(block, x+d, y, z, ((BlockTritiumLamp)block).getFrameIcon());
			rb.renderFaceZNeg(block, x, y, z-d, ((BlockTritiumLamp)block).getFrameIcon());
			rb.renderFaceZPos(block, x, y, z+d, ((BlockTritiumLamp)block).getFrameIcon());
			v5.addTranslation(x, y, z);
			 */
		}

		v5.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}
