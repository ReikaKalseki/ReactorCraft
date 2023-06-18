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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Models.ModelExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHeatExchanger;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityHeatExchanger.Exchange;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderExchanger extends ReactorRenderBase
{
	private ModelExchanger ExchangerModel = new ModelExchanger();

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityHeatExchangerAt(TileEntityHeatExchanger tile, double par2, double par4, double par6, float par8)
	{
		ModelExchanger var14;
		var14 = ExchangerModel;

		this.bindTextureByName("/Reika/ReactorCraft/Textures/TileEntity/exchanger2.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int var11 = 0;
		float var13;

		var14.renderAll(tile, null);

		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	private void renderFluid(Fluid f, boolean inner, World world) {
		FluidStack liquid = new FluidStack(f, 1);

		int[] displayList = ReikaLiquidRenderer.getGLLists(liquid, world, false);

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

		GL11.glTranslated(0, 0.0625, 0);

		if (inner) {
			GL11.glTranslated(0.375, 0.001, 0.375);
			GL11.glScaled(0.25, 0.95, 0.25);
		}
		else {
			GL11.glTranslated(-0.04, 0.34, -0.04);
			GL11.glScaled(1.08, 0.19, 1.08);
		}

		GL11.glCallList(displayList[ReikaLiquidRenderer.LEVELS - 1]);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityReactorBase)tile))
			this.renderTileEntityHeatExchangerAt((TileEntityHeatExchanger)tile, par2, par4, par6, par8);
		if (((TileEntityReactorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);

			if (MinecraftForgeClient.getRenderPass() == 1) {
				Exchange e = ((TileEntityHeatExchanger)tile).getCurrentRecipe();
				if (e != null) {
					GL11.glPushMatrix();
					GL11.glTranslated(par2, par4, par6);
					this.renderFluid(e.hotFluid, true, tile.worldObj);
					this.renderFluid(e.coldFluid, false, tile.worldObj);
					GL11.glPopMatrix();
				}
			}
			//IOAPI.renderIO((ShaftMachine)tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "exchanger2.png";
	}
}
