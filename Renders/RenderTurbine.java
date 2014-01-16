/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelTurbine;
import Reika.ReactorCraft.TileEntities.TileEntityTurbineCore;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderTurbine extends ReactorRenderBase
{
	private ModelTurbine[] models = new ModelTurbine[5];

	public RenderTurbine() {
		super();
		this.buildModels();
	}

	private void buildModels() {
		for (int i = 0; i < models.length; i++) {
			models[i] = new ModelTurbine(i);
		}
	}

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityTurbineCoreAt(TileEntityTurbineCore tile, double par2, double par4, double par6, float par8)
	{
		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/turbine.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		switch(tile.getBlockMetadata()) {
		case 0:
			GL11.glRotatef(90, 0, 1, 0);
			break;
		case 1:
			GL11.glRotatef(270, 0, 1, 0);
			break;
		case 2:
			GL11.glRotatef(180, 0, 1, 0);
			break;
		case 3:
			GL11.glRotatef(0, 0, 1, 0);
			break;
		}

		if (tile.isInWorld())
			models[tile.getStage()].renderAll(ReikaJavaLibrary.makeListFrom(tile.getDamage()), -tile.phi, 0);
		else {
			double sc = 0.6;
			GL11.glScaled(sc, sc, sc);
			GL11.glTranslated(-0.1, 0.8, 0);
			models[0].renderAll(null, -tile.phi, 0);
			GL11.glTranslated(0.1, -0.8, 0);
			GL11.glScaled(1D/sc, 1D/sc, 1D/sc);
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
			this.renderTileEntityTurbineCoreAt((TileEntityTurbineCore)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
			//IOAPI.renderIO((ShaftMachine)tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "turbine.png";
	}
}
