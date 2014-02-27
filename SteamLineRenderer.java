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
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class SteamLineRenderer implements ISimpleBlockRenderingHandler {

	public final int renderID;
	private static final ForgeDirection[] dirs = ForgeDirection.values();

	public SteamLineRenderer(int ID) {
		renderID = ID;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileEntitySteamLine tile = (TileEntitySteamLine)world.getBlockTileEntity(x, y, z);
		//ReikaTextureHelper.bindTexture(ReactorCraft.class, "/Reika/ReactorCraft/Textures/TileEntity/waterline.png");
		GL11.glColor4f(1, 1, 1, 1);
		for (int i = 0; i < 6; i++) {
			this.renderFace(tile, x, y, z, dirs[i]);
		}
		//ReikaTextureHelper.bindTerrainTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

	private void renderFace(TileEntitySteamLine tile, int x, int y, int z, ForgeDirection dir) {
		double size = 0.333333;
		Tessellator v5 = Tessellator.instance;
		//Icon ico = Block.cloth.getIcon(0, 15);
		Icon ico = Block.stone.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		v5.setColorOpaque(255, 255, 255);
		v5.addTranslation(x, y, z);
		if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, dir)) {
			switch(dir) {
			case DOWN:
				this.faceBrightness(ForgeDirection.SOUTH, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.EAST, v5);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, u, v);
				break;
			case EAST:
				this.faceBrightness(ForgeDirection.DOWN, v5);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.SOUTH, v5);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, u, v);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5);
				v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, u, v);
				break;
			case NORTH:
				this.faceBrightness(ForgeDirection.DOWN, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, u, v);

				this.faceBrightness(ForgeDirection.EAST, v5);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, du, dv);
				break;
			case SOUTH:
				this.faceBrightness(ForgeDirection.DOWN, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, u, v);

				this.faceBrightness(ForgeDirection.EAST, v5);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, du, dv);
				break;
			case UP:
				this.faceBrightness(ForgeDirection.SOUTH, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.EAST, v5);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.WEST, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5);
				v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, u, v);
				break;
			case WEST:
				this.faceBrightness(ForgeDirection.DOWN, v5);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, du, dv);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, v);

				this.faceBrightness(ForgeDirection.SOUTH, v5);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.UP, v5);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, du, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, u, v);
				v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, dv);
				v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, dv);

				this.faceBrightness(ForgeDirection.NORTH, v5);
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
			this.faceBrightness(dir, v5);
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

	private void faceBrightness(ForgeDirection dir, Tessellator v5) {
		float f = 1;
		switch(dir.getOpposite()) {
		case DOWN:
			f = 0.4F;
			break;
		case EAST:
			f = 0.5F;
			break;
		case NORTH:
			f = 0.65F;
			break;
		case SOUTH:
			f = 0.65F;
			break;
		case UP:
			f = 1F;
			break;
		case WEST:
			f = 0.5F;
			break;
		default:
			break;
		}
		f*= 0.2;
		v5.setColorOpaque_F(f, f, f);
	}

}
