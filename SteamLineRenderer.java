/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Rendering.WorldPipingRenderer;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;

public class SteamLineRenderer extends WorldPipingRenderer {

	public SteamLineRenderer(int ID) {
		super(ID);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		TileEntitySteamLine tile = (TileEntitySteamLine)world.getTileEntity(x, y, z);
		GL11.glColor4f(1, 1, 1, 1);
		for (int i = 0; i < 6; i++) {
			this.renderFace(tile, x, y, z, dirs[i], 0.333333);
		}
		return true;
	}

	@Override
	protected void renderFace(TileEntity te, int x, int y, int z, ForgeDirection dir, double size) {
		TileEntitySteamLine tile = (TileEntitySteamLine)te;
		Tessellator v5 = Tessellator.instance;
		//Icon ico = Blocks.wool.getIcon(0, 15);
		IIcon ico = Blocks.stone.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		float br = 0.2F;

		v5.setColorOpaque(255, 255, 255);
		v5.addTranslation(x, y, z);
		v5.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
		if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, dir)) {
			switch(dir) {
			case DOWN:
				this.faceBrightness(ForgeDirection.SOUTH, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.EAST, v5, br);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, u, v);
				break;
			case EAST:
				this.faceBrightness(ForgeDirection.DOWN, v5, br);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.SOUTH, v5, br);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5, br);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, u, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5, br);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, u, v);
				break;
			case NORTH:
				this.faceBrightness(ForgeDirection.DOWN, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, u, v);

				this.faceBrightness(ForgeDirection.EAST, v5, br);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, du, dv);
				break;
			case SOUTH:
				this.faceBrightness(ForgeDirection.DOWN, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, u, v);

				this.faceBrightness(ForgeDirection.EAST, v5, br);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, du, dv);
				break;
			case UP:
				this.faceBrightness(ForgeDirection.SOUTH, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.EAST, v5, br);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5, br);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, u, v);
				break;
			case WEST:
				this.faceBrightness(ForgeDirection.DOWN, v5, br);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.SOUTH, v5, br);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5, br);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, u, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5, br);
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
			this.faceBrightness(dir, v5, br);
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
