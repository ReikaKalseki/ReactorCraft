/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Rendering.WorldPipingRenderer;
import Reika.ReactorCraft.Base.TileEntityLine;
import Reika.ReactorCraft.TileEntities.TileEntityHeatPipe;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;

public class SteamLineRenderer extends WorldPipingRenderer {

	public SteamLineRenderer(int ID) {
		super(ID);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		TileEntityLine tile = (TileEntityLine)world.getTileEntity(x, y, z);
		GL11.glColor4f(1, 1, 1, 1);
		for (int i = 0; i < 6; i++) {
			this.renderFace(tile, x, y, z, dirs[i], 0.333333);
		}
		return true;
	}

	@Override
	protected void renderFace(TileEntity te, int x, int y, int z, ForgeDirection dir, double size) {
		TileEntityLine tile = (TileEntityLine)te;
		Tessellator v5 = Tessellator.instance;
		IIcon ico = tile.getTexture();
		//IIcon ico = Blocks.stone.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		v5.setColorOpaque(255, 255, 255);
		float r = 1;
		float g = 1;
		float b = 1;
		if (te instanceof TileEntityHeatPipe) {
			g = 0.65F;
			b = 0.3F;
		}
		else if (te instanceof TileEntitySteamLine) {
			r = g = b = 0.5F;
		}
		v5.addTranslation(x, y, z);
		v5.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
		if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, dir)) {
			switch(dir) {
				case DOWN:
					this.faceBrightnessColor(ForgeDirection.SOUTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.EAST, v5, r, g, b);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, u, v);

					this.faceBrightnessColor(ForgeDirection.WEST, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.NORTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, u, v);
					break;
				case EAST:
					this.faceBrightnessColor(ForgeDirection.DOWN, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, v);

					this.faceBrightnessColor(ForgeDirection.SOUTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.UP, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, u, v);
					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.NORTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, u, v);
					break;
				case NORTH:
					this.faceBrightnessColor(ForgeDirection.DOWN, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, u, v);

					this.faceBrightnessColor(ForgeDirection.EAST, v5, r, g, b);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);

					this.faceBrightnessColor(ForgeDirection.WEST, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, u, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, du, dv);

					this.faceBrightnessColor(ForgeDirection.UP, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, du, dv);
					break;
				case SOUTH:
					this.faceBrightnessColor(ForgeDirection.DOWN, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, u, v);

					this.faceBrightnessColor(ForgeDirection.EAST, v5, r, g, b);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);

					this.faceBrightnessColor(ForgeDirection.WEST, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, u, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, du, dv);

					this.faceBrightnessColor(ForgeDirection.UP, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, du, dv);
					break;
				case UP:
					this.faceBrightnessColor(ForgeDirection.SOUTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.EAST, v5, r, g, b);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, u, v);

					this.faceBrightnessColor(ForgeDirection.WEST, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.NORTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, u, v);
					break;
				case WEST:
					this.faceBrightnessColor(ForgeDirection.DOWN, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, v);

					this.faceBrightnessColor(ForgeDirection.SOUTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.UP, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, u, v);
					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, dv);

					this.faceBrightnessColor(ForgeDirection.NORTH, v5, r, g, b);
					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, u, v);
					break;
				default:
					break;
			}
		}
		else {
			this.faceBrightnessColor(dir, v5, r, g, b);
			v5.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
			switch(dir) {
				case DOWN:
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, u, v);
					break;
				case NORTH:
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, du, dv);
					break;
				case EAST:
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, u, v);
					break;
				case WEST:
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, u, v);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, du, dv);
					break;
				case UP:
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, u, v);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, u, dv);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, du, dv);
					break;
				case SOUTH:
					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, u, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, du, dv);
					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, du, v);
					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, u, v);
					break;
				default:
					break;
			}
		}
		v5.addTranslation(-x, -y, -z);
	}
}
