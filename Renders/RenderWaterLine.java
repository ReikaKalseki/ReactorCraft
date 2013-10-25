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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.TileEntities.TileEntitySteamLine;

public class RenderWaterLine extends ReactorRenderBase {

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityWaterLineAt(TileEntitySteamLine tile, double par2, double par4, double par6, float par8)
	{
		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/waterline.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		int var11 = 0;
		float var13;

		for (int i = 0; i < 6; i++) {
			this.renderFace(tile, par2, par4, par6, dirs[i]);
		}

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	private void renderFace(TileEntitySteamLine tile, double par2, double par4, double par6, ForgeDirection dir) {
		double size = 0.333333;
		Tessellator v5 = new Tessellator();
		v5.startDrawingQuads();
		if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, dir)) {
			switch(dir) {
			case DOWN:
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, 0, 0);
				break;
			case EAST:
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, 0, 0);
				break;
			case NORTH:
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, 1, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, 1, 1);
				break;
			case SOUTH:
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, 1, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, 1, 1);
				break;
			case UP:
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, 0, 0);
				break;
			case WEST:
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, 0, 0);
				break;
			default:
				break;
			}
		}
		else {
			v5.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
			switch(dir) {
			case DOWN:
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, 0, 0);
				break;
			case NORTH:
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, 1, 1);
				break;
			case EAST:
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, 0, 0);
				break;
			case WEST:
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, 0, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, 1, 1);
				break;
			case UP:
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, 0, 0);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, 0, 1);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, 1, 1);
				break;
			case SOUTH:
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, 0, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, 1, 1);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, 1, 0);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, 0, 0);
				break;
			default:
				break;
			}
		}
		v5.draw();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityReactorBase)tile))
			this.renderTileEntityWaterLineAt((TileEntitySteamLine)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {

		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "waterline.png";
	}
}
