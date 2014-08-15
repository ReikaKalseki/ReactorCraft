/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import Reika.DragonAPI.Instantiable.Rendering.TankDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerSynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidTankInfo;

public class GuiSynthesizer extends ReactorGuiBase {

	private TileEntitySynthesizer tile;

	public GuiSynthesizer(EntityPlayer ep, TileEntitySynthesizer te) {
		super(new ContainerSynthesizer(ep, te), ep, te);
		tile = te;
		ySize = 175;
		xSize = 176;
	}

	private TankDisplay input;
	private TankDisplay output;

	@Override
	public String getGuiTexture() {
		return "synthesizer";
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		FluidTankInfo[] info = tile.getTankInfo(null);
		input = new TankDisplay(info[0], j+17, k+18, 16, 60);
		output = new TankDisplay(info[1], j+134, k+18, 16, 60);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		super.drawGuiContainerForegroundLayer(par1, par2);

		int x = ReikaGuiAPI.instance.getMouseRealX()-j;
		int y = ReikaGuiAPI.instance.getMouseRealY()-k;

		if (ReikaGuiAPI.instance.isMouseInBox(j+16, j+33, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, "Water", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+133, j+150, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, "Ammonia", x, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		/*
		int i2 = tile.getWaterScaled(60);
		this.drawTexturedModalRect(j+17, k+78-i2, 208, 80-i2, 16, i2);

		int i4 = tile.getAmmoniaScaled(60);
		this.drawTexturedModalRect(j+134, k+78-i4, 224, 80-i4, 16, i4);
		 */
		int i6 = tile.getTimerScaled(24);
		this.drawTexturedModalRect(j+103, k+26, 176, 92, i6, 34);

		FluidTankInfo[] info = tile.getTankInfo(null);
		input.updateTank(info[0]).render(true);
		output.updateTank(info[1]).render(true);
	}

}