/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelFlywheel;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.TileEntities.TileEntityReactorFlywheel;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderTurbineWheel extends ReactorRenderBase {

	private ModelFlywheel model = new ModelFlywheel();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "flywheel";
	}

	public void renderTileEntityReactorFlywheelAt(TileEntityReactorFlywheel tile, double par2, double par4, double par6, float par8)
	{
		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/flywheel.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4+2, par6+1);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		switch(tile.getBlockMetadata()) {
		case 0:
			GL11.glRotatef(270, 0, 1, 0);
			break;
		case 1:
			GL11.glRotatef(90, 0, 1, 0);
			break;
		case 2:
			GL11.glRotatef(0, 0, 1, 0);
			break;
		case 3:
			GL11.glRotatef(180, 0, 1, 0);
			break;
		}

		if (tile.isInWorld()) {
			if (tile.hasMultiBlock())
				model.renderAll(tile, null, tile.phi, 0);
			else {
				GL11.glTranslated(-0.5, -0.5, -0.5);
				Tessellator v5 = Tessellator.instance;
				IIcon ico = ReactorBlocks.FLYWHEELMULTI.getBlockInstance().getIcon(0, 1);
				ReikaTextureHelper.bindTerrainTexture();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				v5.startDrawingQuads();
				v5.setNormal(0, -1, 0);
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(1, 1, 0, du, v);
				v5.addVertexWithUV(1, 1, 1, du, dv);
				v5.addVertexWithUV(0, 1, 1, u, dv);

				v5.setNormal(0, -0.1F, 0);
				v5.addVertexWithUV(0, 2, 1, u, dv);
				v5.addVertexWithUV(1, 2, 1, du, dv);
				v5.addVertexWithUV(1, 2, 0, du, v);
				v5.addVertexWithUV(0, 2, 0, u, v);

				v5.setNormal(0, -0.25F, 0);
				v5.addVertexWithUV(1, 2, 1, du, dv);
				v5.addVertexWithUV(1, 1, 1, du, v);
				v5.addVertexWithUV(1, 1, 0, u, v);
				v5.addVertexWithUV(1, 2, 0, u, dv);

				v5.setNormal(0, -0.5F, 0);
				v5.addVertexWithUV(0, 2, 1, u, dv);
				v5.addVertexWithUV(0, 1, 1, u, v);
				v5.addVertexWithUV(1, 1, 1, du, v);
				v5.addVertexWithUV(1, 2, 1, du, dv);

				v5.addVertexWithUV(1, 2, 0, du, dv);
				v5.addVertexWithUV(1, 1, 0, du, v);
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(0, 2, 0, u, dv);

				v5.setNormal(0, -0.25F, 0);
				v5.addVertexWithUV(0, 2, 0, u, dv);
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(0, 1, 1, du, v);
				v5.addVertexWithUV(0, 2, 1, du, dv);
				v5.draw();
			}
		}
		else {
			GL11.glRotatef(180, 0, 1, 0);
			double sc = 0.25;
			double a = 0;
			double b = 0.75;
			double c = 0;
			GL11.glTranslated(a, b, c);
			GL11.glScaled(sc, sc, sc);
			model.renderAll(tile, null, -tile.phi, 0);
			GL11.glScaled(1D/sc, 1D/sc, 1D/sc);
			GL11.glTranslated(-a, -b, -c);
			GL11.glRotatef(-180, 0, 1, 0);
		}

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityReactorBase)tile))
			this.renderTileEntityReactorFlywheelAt((TileEntityReactorFlywheel)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
			//IOAPI.renderIO((ShaftMachine)tile, par2, par4, par6);
		}
	}

}
