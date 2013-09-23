/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Entities;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.ReactorCraft.Registry.ReactorOptions;

public class RenderNeutron extends Render
{
	public RenderNeutron()
	{
		shadowSize = 0.15F;
		shadowOpaque = 0.75F;
	}

	public void renderTheNeutron(EntityNeutron par1EntityNeutron, double par2, double par4, double par6, float par8, float par9)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		Tessellator v5 = new Tessellator();
		float var16 = 0.25F;
		float var17 = 0.5F;
		float var18 = 0.25F;
		int var19 = par1EntityNeutron.getBrightnessForRender(par9);
		int var20 = var19 % 65536;
		int var21 = var19 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var20 / 1.0F, var21 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var26 = 255.0F;
		int var22 = (int)var26;
		GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		float var25 = 0.3F;
		GL11.glScalef(var25, var25, var25);
		ReikaRenderHelper.prepareGeoDraw(false);

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 1.0F, 0.0F);
		v5.setColorOpaque(0, 0, 128);
		v5.addVertex(0.0F - var17, 0.0F - var18, 0.0D);
		v5.addVertex(var16 - var17, 0.0F - var18, 0.0D);
		v5.addVertex(var16 - var17, var16 - var18, 0.0D);
		v5.addVertex(0.0F - var17, var16 - var18, 0.0D);
		v5.draw();

		ReikaRenderHelper.exitGeoDraw();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		if (ReactorOptions.VISIBLENEUTRONS.getState())
			this.renderTheNeutron((EntityNeutron)par1Entity, par2, par4+0*0.5, par6, par8, par9);
	}
}
