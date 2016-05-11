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
import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.Instantiable.GUI.TankDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerThoriumCore;
import Reika.ReactorCraft.TileEntities.Fission.Thorium.TileEntityThoriumCore;

public class GuiThoriumCore extends ReactorGuiBase {

	private final TileEntityThoriumCore tile;

	private TankDisplay input;
	private TankDisplay output;
	private TankDisplay waste;

	public GuiThoriumCore(EntityPlayer player, TileEntityThoriumCore fuel) {
		super(new ContainerThoriumCore(player, fuel), player, fuel);
		ySize = 100;
		tile = fuel;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		FluidTankInfo[] info = tile.getTankInfo(null);
		input = new TankDisplay(info[0], j+53, k+23, 16, 70);
		output = new TankDisplay(info[1], j+80, k+23, 16, 70);
		waste = new TankDisplay(info[2], j+107, k+23, 16, 70);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		super.drawGuiContainerForegroundLayer(par1, par2);

		int x = ReikaGuiAPI.instance.getMouseRealX()-j;
		int y = ReikaGuiAPI.instance.getMouseRealY()-k;

		if (ReikaGuiAPI.instance.isMouseInBox(j+52, j+70, k+22, k+72)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, "Fuel Salts", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+79, j+97, k+22, k+72)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, "Heated Fuel Salts", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+106, j+128, k+22, k+72)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, "Waste", x, y);
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

		FluidTankInfo[] info = tile.getTankInfo(null);
		input.updateTank(info[0]).render(true);
		output.updateTank(info[1]).render(true);
		waste.updateTank(info[2]).render(true);
	}

	@Override
	public String getGuiTexture() {
		return "fuelpool";
	}

}
