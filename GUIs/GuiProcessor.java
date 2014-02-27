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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.Instantiable.Rendering.TankDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerProcessor;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;

public class GuiProcessor extends ReactorGuiBase {

	private TileEntityUProcessor tile;

	private TankDisplay water;
	private TankDisplay acid;
	private TankDisplay uf6;

	public GuiProcessor(EntityPlayer player, TileEntityUProcessor proc) {
		super(new ContainerProcessor(player, proc), player, proc);
		ySize = 175;
		tile = proc;
	}

	@Override
	public String getGuiTexture() {
		return "processor";
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		FluidTankInfo[] info = tile.getTankInfo(null);
		water = new TankDisplay(info[0], j+98, k+18, 16, 60);
		acid = new TankDisplay(info[1], j+116, k+18, 16, 60);
		uf6 = new TankDisplay(info[2], j+134, k+18, 16, 60);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		super.drawGuiContainerForegroundLayer(par1, par2);

		int x = ReikaGuiAPI.instance.getMouseRealX()-j;
		int y = ReikaGuiAPI.instance.getMouseRealY()-k;

		if (ReikaGuiAPI.instance.isMouseInBox(j+97, j+114, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRenderer, "Water", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+115, j+132, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRenderer, "Hydrofluoric Acid", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+133, j+150, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRenderer, "Uranium Hexafluoride", x, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		/*
		int i2 = tile.getWaterScaled(60);
		this.drawTexturedModalRect(j+98, k+78-i2, 208, 80-i2, 16, i2);

		int i3 = tile.getHFScaled(60);
		this.drawTexturedModalRect(j+116, k+78-i3, 192, 80-i3, 16, i3);

		int i4 = tile.getUF6Scaled(60);
		this.drawTexturedModalRect(j+134, k+78-i4, 224, 80-i4, 16, i4);
		 */

		FluidTankInfo[] info = tile.getTankInfo(null);

		int i5 = tile.getHFTimerScaled(24);
		this.drawTexturedModalRect(j+67, k+21, 176, 92, i5, 17);

		int i6 = tile.getUF6TimerScaled(24);
		this.drawTexturedModalRect(j+67, k+58, 176, 92, i6, 17);

		water.updateTank(tile.getTankInfo(null)[0]);
		acid.updateTank(tile.getTankInfo(null)[1]);
		uf6.updateTank(tile.getTankInfo(null)[2]);

		water.render(true);
		acid.render(true);
		uf6.render(true);
	}

}
