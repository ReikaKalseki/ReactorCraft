/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.TileEntities.TileEntityFusionMarker;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet.Aim;

public class RenderFusionMarker extends ReactorRenderBase
{
	public void renderTileEntityFusionMarkerAt(TileEntityFusionMarker tile, double par2, double par4, double par6, float par8)
	{

		//this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/exchanger.png");

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();

		if (tile.isInWorld()) {

			float t = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			GL11.glLineWidth(4);
			ReikaRenderHelper.prepareGeoDraw(false);
			this.renderPositions(tile, par2, par4, par6);
			ReikaRenderHelper.exitGeoDraw();
			GL11.glLineWidth(t);
		}
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.renderMarkerBody(tile, par2, par4, par6);
		GL11.glRotated(90, 0, 1, 0);
		this.renderMarkerBody(tile, par2, par4, par6);

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	private void renderMarkerBody(TileEntityFusionMarker tile, double par2, double par4, double par6) {
		IIcon ico = Blocks.redstone_torch.getIcon(0, 0);
		ReikaTextureHelper.bindTerrainTexture();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		Tessellator v5 = Tessellator.instance;
		GL11.glColor4d(0, 0.2, 1, 1);
		if (tile.isInWorld()) {
			GL11.glTranslated(0, 0.5, 0);
			v5.startDrawingQuads();
			v5.addVertexWithUV(-0.5, 0, 0, u, v);
			v5.addVertexWithUV(0.5, 0, 0, du, v);
			v5.addVertexWithUV(0.5, 1, 0, du, dv);
			v5.addVertexWithUV(-0.5, 1, 0, u, dv);

			v5.addVertexWithUV(-0.5, 1, 0, u, dv);
			v5.addVertexWithUV(0.5, 1, 0, du, dv);
			v5.addVertexWithUV(0.5, 0, 0, du, v);
			v5.addVertexWithUV(-0.5, 0, 0, u, v);

			v5.addVertexWithUV(0, 0, -0.5, u, v);
			v5.addVertexWithUV(0, 0, 0.5, du, v);
			v5.addVertexWithUV(0, 1, 0.5, du, dv);
			v5.addVertexWithUV(0, 1, -0.5, u, dv);

			v5.addVertexWithUV(0, 1, -0.5, u, dv);
			v5.addVertexWithUV(0, 1, 0.5, du, dv);
			v5.addVertexWithUV(0, 0, 0.5, du, v);
			v5.addVertexWithUV(0, 0, -0.5, u, v);
			v5.draw();

			GL11.glTranslated(0, -0.5, 0);
		}
		else {
			double d = 2.4;
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glScaled(0.8*d, d, 0.8*d);
			v5.startDrawingQuads();
			v5.addVertexWithUV(-0.5, 0.75, 0, u, dv);
			v5.addVertexWithUV(0.5, 0.75, 0, du, dv);
			v5.addVertexWithUV(0.5, -0.25, 0, du, v);
			v5.addVertexWithUV(-0.5, -0.25, 0, u, v);
			v5.draw();
			GL11.glScaled(0.8/d, 1/d, 0.8/d);
			GL11.glRotated(45, 0, 1, 0);
		}
		GL11.glColor4f(1, 1, 1, 1);
	}

	private void renderSolenoid(TileEntityFusionMarker tile, double par2, double par4, double par6) {
		Tessellator v5 = Tessellator.instance;
		GL11.glTranslated(0, 0.499, 0);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(255, 0, 0);
		v5.addVertex(8, 1, 3);
		v5.addVertex(8, 1, -3);
		v5.addVertex(7, 1, -5);
		v5.addVertex(6, 1, -6);
		v5.addVertex(5, 1, -7);
		v5.addVertex(3, 1, -8);
		v5.addVertex(-3, 1, -8);
		v5.addVertex(-5, 1, -7);
		v5.addVertex(-6, 1, -6);
		v5.addVertex(-7, 1, -5);
		v5.addVertex(-8, 1, -3);
		v5.addVertex(-8, 1, 3);
		v5.addVertex(-7, 1, 5);
		v5.addVertex(-6, 1, 6);
		v5.addVertex(-5, 1, 7);
		v5.addVertex(-3, 1, 8);
		v5.addVertex(3, 1, 8);
		v5.addVertex(5, 1, 7);
		v5.addVertex(6, 1, 6);
		v5.addVertex(7, 1, 5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(255, 0, 0);
		v5.addVertex(8, -2, 3);
		v5.addVertex(8, -2, -3);
		v5.addVertex(7, -2, -5);
		v5.addVertex(6, -2, -6);
		v5.addVertex(5, -2, -7);
		v5.addVertex(3, -2, -8);
		v5.addVertex(-3, -2, -8);
		v5.addVertex(-5, -2, -7);
		v5.addVertex(-6, -2, -6);
		v5.addVertex(-7, -2, -5);
		v5.addVertex(-8, -2, -3);
		v5.addVertex(-8, -2, 3);
		v5.addVertex(-7, -2, 5);
		v5.addVertex(-6, -2, 6);
		v5.addVertex(-5, -2, 7);
		v5.addVertex(-3, -2, 8);
		v5.addVertex(3, -2, 8);
		v5.addVertex(5, -2, 7);
		v5.addVertex(6, -2, 6);
		v5.addVertex(7, -2, 5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(255, 0, 0);
		v5.addVertex(8, -2, 3);
		v5.addVertex(8, 1, 3);
		v5.addVertex(8, -2, -3);
		v5.addVertex(8, 1, -3);
		v5.addVertex(7, -2, -5);
		v5.addVertex(7, 1, -5);
		v5.addVertex(6, -2, -6);
		v5.addVertex(6, 1, -6);
		v5.addVertex(5, -2, -7);
		v5.addVertex(5, 1, -7);
		v5.addVertex(3, -2, -8);
		v5.addVertex(3, 1, -8);
		v5.addVertex(-3, -2, -8);
		v5.addVertex(-3, 1, -8);
		v5.addVertex(-5, -2, -7);
		v5.addVertex(-5, 1, -7);
		v5.addVertex(-6, -2, -6);
		v5.addVertex(-6, 1, -6);
		v5.addVertex(-7, -2, -5);
		v5.addVertex(-7, 1, -5);
		v5.addVertex(-8, -2, -3);
		v5.addVertex(-8, 1, -3);
		v5.addVertex(-8, -2, 3);
		v5.addVertex(-8, 1, 3);
		v5.addVertex(-7, -2, 5);
		v5.addVertex(-7, 1, 5);
		v5.addVertex(-6, -2, 6);
		v5.addVertex(-6, 1, 6);
		v5.addVertex(-5, -2, 7);
		v5.addVertex(-5, 1, 7);
		v5.addVertex(-3, -2, 8);
		v5.addVertex(-3, 1, 8);
		v5.addVertex(3, -2, 8);
		v5.addVertex(3, 1, 8);
		v5.addVertex(5, -2, 7);
		v5.addVertex(5, 1, 7);
		v5.addVertex(6, -2, 6);
		v5.addVertex(6, 1, 6);
		v5.addVertex(7, -2, 5);
		v5.addVertex(7, 1, 5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(255, 0, 0);
		v5.addVertex(8, 1, 0);
		v5.addVertex(-8, 1, 0);
		v5.addVertex(8, -2, 0);
		v5.addVertex(-8, -2, 0);

		v5.addVertex(0, 1, 8);
		v5.addVertex(0, 1, -8);
		v5.addVertex(0, -2, 8);
		v5.addVertex(0, -2, -8);

		v5.addVertex(6, 1, 6);
		v5.addVertex(-6, 1, -6);
		v5.addVertex(6, -2, 6);
		v5.addVertex(-6, -2, -6);

		v5.addVertex(-6, 1, 6);
		v5.addVertex(6, 1, -6);
		v5.addVertex(-6, -2, 6);
		v5.addVertex(6, -2, -6);
		v5.draw();
		GL11.glTranslated(0, -0.499, 0);
	}

	private void renderPositions(TileEntityFusionMarker tile, double par2, double par4, double par6) {
		World world = tile.worldObj;
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;

		z += 14;

		ArrayList<Aim> li = tile.getAimPoints();

		for (int i = 0; i < li.size(); i++) {
			Aim a = li.get(i);
			if (i%10 != 0) {
				this.renderToroidBoxAt(tile, a, x, y, z, par2, par4, par6);
			}
			else {
				this.renderInjectorBoxAt(tile, a, x, y, z, par2, par4, par6);
			}
			x += a.xOffset;
			z += a.zOffset;
		}

		this.renderSolenoid(tile, par2, par4, par6);
	}

	private void renderInjectorBoxAt(TileEntityFusionMarker tile, Aim a, int x, int y, int z, double par2, double par4, double par6) {
		Tessellator v5 = Tessellator.instance;
		GL11.glTranslated(x-tile.xCoord, y-tile.yCoord+1.499, z-tile.zCoord);
		GL11.glRotated(a.angle, 0, 1, 0);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(0, 200, 255);
		v5.addVertex(-1.5, 0, -2.5);
		v5.addVertex(1.5, 0, -2.5);
		v5.addVertex(1.5, 0, 6.5);
		v5.addVertex(-1.5, 0, 6.5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(0, 200, 255);
		v5.addVertex(-1.5, -5, -2.5);
		v5.addVertex(1.5, -5, -2.5);
		v5.addVertex(1.5, -3, 6.5);
		v5.addVertex(-1.5, -3, 6.5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(0, 200, 255);
		v5.addVertex(-1.5, -5, -2.5);
		v5.addVertex(-1.5, 0, -2.5);
		v5.addVertex(1.5, -5, -2.5);
		v5.addVertex(1.5, 0, -2.5);
		v5.addVertex(1.5, -3, 6.5);
		v5.addVertex(1.5, 0, 6.5);
		v5.addVertex(-1.5, -3, 6.5);
		v5.addVertex(-1.5, 0, 6.5);
		v5.draw();
		GL11.glRotated(-a.angle, 0, 1, 0);
		GL11.glTranslated(tile.xCoord-x, tile.yCoord-y-1.499, tile.zCoord-z);
	}

	private void renderToroidBoxAt(TileEntityFusionMarker tile, Aim a, int x, int y, int z, double par2, double par4, double par6) {
		Tessellator v5 = Tessellator.instance;
		GL11.glTranslated(x-tile.xCoord, y-tile.yCoord+1.499, z-tile.zCoord);
		GL11.glRotated(a.angle, 0, 1, 0);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(255, 255, 0);
		v5.addVertex(0.25, 0, -0.5);
		v5.addVertex(0.25, 0, 0.5);
		v5.addVertex(0.25, -1, 1.5);
		v5.addVertex(0.25, -2, 1.5);
		v5.addVertex(0.25, -3, 0.5);
		v5.addVertex(0.25, -3, -0.5);
		v5.addVertex(0.25, -2, -1.5);
		v5.addVertex(0.25, -1, -1.5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque(255, 255, 0);
		v5.addVertex(-0.25, 0, -0.5);
		v5.addVertex(-0.25, 0, 0.5);
		v5.addVertex(-0.25, -1, 1.5);
		v5.addVertex(-0.25, -2, 1.5);
		v5.addVertex(-0.25, -3, 0.5);
		v5.addVertex(-0.25, -3, -0.5);
		v5.addVertex(-0.25, -2, -1.5);
		v5.addVertex(-0.25, -1, -1.5);
		v5.draw();

		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(255, 255, 0);
		v5.addVertex(0.25, 0, -0.5);
		v5.addVertex(-0.25, 0, -0.5);
		v5.addVertex(0.25, 0, 0.5);
		v5.addVertex(-0.25, 0, 0.5);
		v5.addVertex(0.25, -1, 1.5);
		v5.addVertex(-0.25, -1, 1.5);
		v5.addVertex(0.25, -2, 1.5);
		v5.addVertex(-0.25, -2, 1.5);
		v5.addVertex(0.25, -3, 0.5);
		v5.addVertex(-0.25, -3, 0.5);
		v5.addVertex(0.25, -3, -0.5);
		v5.addVertex(-0.25, -3, -0.5);
		v5.addVertex(0.25, -2, -1.5);
		v5.addVertex(-0.25, -2, -1.5);
		v5.addVertex(0.25, -1, -1.5);
		v5.addVertex(-0.25, -1, -1.5);
		v5.draw();

		GL11.glRotated(-a.angle, 0, 1, 0);
		GL11.glTranslated(tile.xCoord-x, tile.yCoord-y-1.499, tile.zCoord-z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityReactorBase)tile))
			this.renderTileEntityFusionMarkerAt((TileEntityFusionMarker)tile, par2, par4, par6, par8);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "magnet.png";
	}
}
