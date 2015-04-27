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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelDiffuser;
import Reika.ReactorCraft.TileEntities.TileEntitySteamDiffuser;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderSteamDiffuser extends ReactorRenderBase
{
	private ModelDiffuser DiffuserModel = new ModelDiffuser();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityDiffuserAt(TileEntitySteamDiffuser tile, double par2, double par4, double par6, float par8)
	{
		ModelDiffuser var14;
		var14 = DiffuserModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/diffuser.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;

		float angle = 0;

		switch(tile.getBlockMetadata()) {
		case 0:
			angle = 90;
			break;
		case 1:
			angle = 270;
			break;
		case 2:
			angle = 180;
			break;
		case 3:
			break;
		}

		GL11.glRotated(angle, 0, 1, 0);

		var14.renderAll(tile, null, -tile.phi, 0);

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityReactorBase)tile))
			this.renderTileEntityDiffuserAt((TileEntitySteamDiffuser)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
			//IOAPI.renderIO((ShaftMachine)tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "diffuser.png";
	}
}
