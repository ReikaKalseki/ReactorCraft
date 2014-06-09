/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelMagnet;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet;

public class RenderMagnet extends ReactorRenderBase
{
	private ModelMagnet MagnetModel = new ModelMagnet();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityMagnetAt(TileEntityToroidMagnet tile, double par2, double par4, double par6, float par8)
	{
		ModelMagnet var14;
		var14 = MagnetModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/magnet.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;
		float ang = 90-tile.getAngle();
		double s = 0.4;
		double d = 0.6;
		if (!tile.isInWorld()) {
			GL11.glTranslated(0, d, 0);
			GL11.glScaled(s, s, s);
		}
		GL11.glRotated(ang, 0, 1, 0);
		var14.renderAll(tile, null, 0, 0);
		GL11.glRotated(-ang, 0, 1, 0);
		if (!tile.isInWorld()) {
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glTranslated(0, -d, 0);
		}

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityReactorBase)tile))
			this.renderTileEntityMagnetAt((TileEntityToroidMagnet)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			//IORenderer.renderIO(tile, par2, par4, par6);
			//IOAPI.renderIO((ShaftMachine)tile, par2, par4, par6);
			this.renderAngleLine((TileEntityToroidMagnet)tile, par2, par4+0.5, par6);
		}
	}

	private void renderAngleLine(TileEntityToroidMagnet tile, double par2, double par4, double par6) {
		if (tile == null)
			return;
		if (!tile.isInWorld())
			return;
		int a = tile.getAlpha();
		//a = 255;
		if (a <= 0)
			return;
		ReikaRenderHelper.prepareGeoDraw(true);
		GL11.glTranslated(par2+0.5, par4, par6+0.5);
		float ang = tile.getAngle()+90;
		Tessellator v5 = Tessellator.instance;
		GL11.glRotated(ang, 0, 1, 0);
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorRGBA(100, 192, 255, a);

		v5.addVertex(0, 0.1, 0);
		v5.addVertex(4, 0.1, 0);

		v5.addVertex(3.5, 0.1, 0.5);
		v5.addVertex(4, 0.1, 0);

		v5.addVertex(3.5, 0.1, -0.5);
		v5.addVertex(4, 0.1, 0);

		v5.draw();
		for (int i = 1; i < 4; i++)
			ReikaRenderHelper.renderVCircle(i, 0, 0, 0, ReikaColorAPI.RGBtoHex(255, 255, 255, a), Math.toRadians(90), 10);
		GL11.glRotated(-ang, 0, 1, 0);
		GL11.glTranslated(-par2-0.5, -par4, -par6-0.5);
		ReikaRenderHelper.exitGeoDraw();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "magnet.png";
	}
}
