/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.Instantiable.GUI.TankDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;

public class GuiElectrolyzer extends ReactorGuiBase {

	private TileEntityElectrolyzer tile;

	private TankDisplay heavy;
	private TankDisplay light;
	private TankDisplay input;

	public GuiElectrolyzer(EntityPlayer ep, TileEntityElectrolyzer te) {
		super(new ContainerElectrolyzer(ep, te), ep, te);
		tile = te;
		ySize = 175;
		xSize = 176;
	}

	@Override
	public String getGuiTexture() {
		return "electrolyzer";
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		FluidTankInfo[] info = tile.getTankInfo(null);
		heavy = new TankDisplay(info[0], j+98, k+18, 16, 60);
		light = new TankDisplay(info[1], j+134, k+18, 16, 60);
		input = new TankDisplay(info[2], j+17, k+18, 16, 60);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		super.drawGuiContainerForegroundLayer(par1, par2);

		int x = ReikaGuiAPI.instance.getMouseRealX()-j;
		int y = ReikaGuiAPI.instance.getMouseRealY()-k;

		FluidTankInfo[] info = tile.getTankInfo(null);
		FluidStack h = info[0].fluid;
		FluidStack l = info[1].fluid;
		FluidStack i = info[2].fluid;
		String heavy = h != null ? h.getFluid().getLocalizedName() : "Empty";
		String light = l != null ? l.getFluid().getLocalizedName() : "Empty";
		String in = i != null ? i.getFluid().getLocalizedName() : "Empty";
		if (ReikaGuiAPI.instance.isMouseInBox(j+97, j+114, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, heavy, x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+133, j+150, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, light, x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+16, j+33, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, in, x, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		/*
		int i2 = tile.getChlorineScaled(60);
		this.drawTexturedModalRect(j+17, k+78-i2, 208, 80-i2, 16, i2);

		int i4 = tile.getSodiumScaled(60);
		this.drawTexturedModalRect(j+134, k+78-i4, 224, 80-i4, 16, i4);
		 */
		int i6 = tile.getTimerScaled(66);
		FluidTankInfo[] info = tile.getTankInfo(null);
		int dy = tile.getStackInSlot(0) != null ? 61 : 124;
		this.drawTexturedModalRect(j+65, k+17, 177, dy, i6, 62);

		heavy.updateTank(info[0]).render(true);
		light.updateTank(info[1]).render(true);
		input.updateTank(info[2]).render(true);
	}

}
