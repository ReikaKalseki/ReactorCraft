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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Models.ModelProcessor;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;

public class RenderProcessor extends ReactorRenderBase
{
	private ModelProcessor ProcessorModel = new ModelProcessor();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityUProcessorAt(TileEntityUProcessor tile, double par2, double par4, double par6, float par8)
	{
		ModelProcessor var14;
		var14 = ProcessorModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/processor.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(0.0F, 0.01F, 0.0F);
		float var13 = 0;

		if (tile.isInWorld()) {
			switch(tile.getBlockMetadata()) {
				case 0:
					var13 = 270;
					break;
				case 1:
					var13 = 90;
					break;
				case 2:
					var13 = 0;
					break;
				case 3:
					var13 = 180;
					break;
			}
		}

		GL11.glRotatef(var13, 0, 1, 0);

		var14.renderAll(tile, null, -tile.phi, 0);

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		TileEntityUProcessor te = (TileEntityUProcessor)tile;
		if (this.doRenderModel(te))
			this.renderTileEntityUProcessorAt(te, par2, par4, par6, par8);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			//IORenderer.renderIO(tile, par2, par4, par6);

			this.renderLiquids((TileEntityUProcessor)tile, par2, par4, par6);
		}
	}

	private void renderLiquids(TileEntityUProcessor tile, double par2, double par4, double par6) {

		for (int i = 0; i < 3; i++) {
			Fluid f = null;
			int amount = 0;
			switch(i) {
				case 0:
					f = tile.getInputFluid();
					amount = tile.getInput();
					break;
				case 1:
					f = tile.getIntermediateFluid();
					amount = tile.getIntermediate();
					break;
				case 2:
					amount = tile.getOutput();
					f = tile.getOutputFluid();
					break;
			}
			if (f == null || amount == 0)
				continue;

			FluidStack liquid = new FluidStack(f, 1);

			int[] displayList = ReikaLiquidRenderer.getGLLists(liquid, tile.worldObj, false);

			if (displayList == null) {
				return;
			}

			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();

			ReikaLiquidRenderer.bindFluidTexture(f);
			ReikaLiquidRenderer.setFluidColor(liquid);

			GL11.glTranslated(par2, par4, par6);

			GL11.glScaled(this.getLiquidScaleX(tile, liquid, i), this.getLiquidScaleY(tile, liquid, i), this.getLiquidScaleZ(tile, liquid, i));
			GL11.glTranslated(this.getLiquidOffsetX(tile, liquid, i), this.getLiquidOffsetY(tile, liquid, i), this.getLiquidOffsetZ(tile, liquid, i));

			GL11.glTranslated(0, 0.01, 0);
			//GL11.glScaled(1, 1/3D, 1);
			GL11.glScaled(0.99, 0.98, 0.99);

			GL11.glCallList(displayList[(int)(amount / (3000D) * (ReikaLiquidRenderer.LEVELS - 1))]);

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	private double getLiquidScaleX(TileEntityUProcessor tile, FluidStack liq, int i) {
		if (i == 0 && tile.getBlockMetadata() >= 2)
			return 0.5;
		if (i == 0 && tile.getBlockMetadata() < 2)
			return 0.5625;

		if (i == 1 && tile.getBlockMetadata() >= 2)
			return 0.5;
		if (i == 1 && tile.getBlockMetadata() < 2)
			return 0.5625;

		if (i == 2 && tile.getBlockMetadata() >= 2)
			return 0.875;
		if (i == 2 && tile.getBlockMetadata() < 2)
			return 0.4375;
		return 1;
	}

	private double getLiquidScaleY(TileEntityUProcessor tile, FluidStack liq, int i) {
		if (i == 2)
			return 11/14D;

		return 1;
	}

	private double getLiquidScaleZ(TileEntityUProcessor tile, FluidStack liq, int i) {
		if (i == 0 && tile.getBlockMetadata() < 2)
			return 0.5;
		if (i == 0 && tile.getBlockMetadata() >= 2)
			return 0.5625;

		if (i == 1 && tile.getBlockMetadata() < 2)
			return 0.5;
		if (i == 1 && tile.getBlockMetadata() >= 2)
			return 0.5625;

		if (i == 2 && tile.getBlockMetadata() >= 2)
			return 0.4375;
		if (i == 2 && tile.getBlockMetadata() < 2)
			return 0.875;
		return 1;
	}

	private double getLiquidOffsetX(TileEntityUProcessor tile, FluidStack liq, int i) {
		if (i == 0 && tile.getBlockMetadata() == 0)
			return 0.775;
		if (i == 0 && tile.getBlockMetadata() == 3)
			return 1;

		if (i == 1 && tile.getBlockMetadata() == 0)
			return 0.775;
		if (i == 1 && tile.getBlockMetadata() == 2)
			return 1;

		if (i == 2 && tile.getBlockMetadata() >= 2)
			return 0.0625;
		if (i == 2 && tile.getBlockMetadata() == 1)
			return 1.25;
		return 0;
	}

	private double getLiquidOffsetY(TileEntityUProcessor tile, FluidStack liq, int i) {
		return 0;
	}

	private double getLiquidOffsetZ(TileEntityUProcessor tile, FluidStack liq, int i) {
		if (i == 0 && tile.getBlockMetadata() == 2)
			return 0.775;
		if (i == 0 && tile.getBlockMetadata() == 0)
			return 1;

		if (i == 1 && tile.getBlockMetadata() == 2)
			return 0.775;
		if (i == 1 && tile.getBlockMetadata() == 1)
			return 1;

		if (i == 2 && tile.getBlockMetadata() == 3)
			return 1.25;
		if (i == 2 && tile.getBlockMetadata() < 2)
			return 0.0625;
		return 0;
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "Processor.png";
	}
}
