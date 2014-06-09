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

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelSteamGrate;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamGrate;

public class RenderSteamGrate extends ReactorRenderBase
{
	private ModelSteamGrate SteamGrateModel = new ModelSteamGrate();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntitySteamGrateAt(TileEntitySteamGrate tile, double par2, double par4, double par6, float par8)
	{
		ModelSteamGrate var14;
		var14 = SteamGrateModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/steamgrate.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;

		var14.renderAll(tile, null, 0, 0);

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityReactorBase)tile))
			this.renderTileEntitySteamGrateAt((TileEntitySteamGrate)tile, par2, par4, par6, par8);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "steamgrate.png";
	}
}
