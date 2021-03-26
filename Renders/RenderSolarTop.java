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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Models.ModelSolarTop;
import Reika.ReactorCraft.TileEntities.TileEntitySolarTop;
import Reika.RotaryCraft.TileEntities.Production.TileEntitySolar;

public class RenderSolarTop extends ReactorRenderBase
{
	private ModelSolarTop TopModel = new ModelSolarTop();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntitySolarTopAt(TileEntitySolarTop tile, double par2, double par4, double par6, float par8)
	{
		ModelSolarTop var14;
		var14 = TopModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/solartop.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		boolean flip = tile.worldObj != null && tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord-1, tile.zCoord) instanceof TileEntitySolarTop;
		double d = flip ? 0 : 2;
		GL11.glTranslated(par2, par4 + d, par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;

		if (flip) {
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(90, 0, 1, 0);
		}

		int c = tile.isInWorld() ? ReikaPhysicsHelper.getColorForTemperature(200+tile.getTemperature()*2) : 0x000000;
		//ReikaJavaLibrary.pConsole(tile.getTemperature()+" > "+Integer.toHexString(c), tile.isInWorld());
		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(c, 0, 0));

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	private void renderFlare(TileEntitySolarTop te, float par8) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ReactorCraft.solarFlare;
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		ReikaRenderHelper.disableLighting();
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);

		double f = (te.getTemperature()-400)/1500D;
		double s = 6*f;
		GL11.glTranslated(0, 0.5, 0);
		GL11.glScaled(s, s, s);
		RenderManager rm = RenderManager.instance;
		GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		GL11.glTranslated(0, 0/*-i*/, 0.005);
		double s2 = 1;
		GL11.glScaled(s2, s2, s2);
		v5.startDrawingQuads();
		double f2 = ((TileEntitySolar)te.getTileEntity(te.xCoord, te.yCoord-1, te.zCoord)).getArrayOverallBrightness();
		int c = ReikaColorAPI.GStoHex((int)(f2*255));
		v5.setColorOpaque_I(c);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		TileEntitySolarTop te = (TileEntitySolarTop)tile;
		if (this.doRenderModel(te))
			this.renderTileEntitySolarTopAt(te, par2, par4, par6, par8);
		if (te.isInWorld() && te.isActive() && te.getTemperature() > 400 && MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(par2, par4 + 2, par6 + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			this.renderFlare(te, par8);
			GL11.glPopMatrix();
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "solartop.png";
	}
}
