/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerProcessor;
import Reika.ReactorCraft.TileEntities.TileEntityUProcessor;

public class GuiProcessor extends ReactorGuiBase {

	private TileEntityUProcessor tile;

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
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		mc.renderEngine.bindTexture("/font/glyph_AA.png");

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

		int i2 = tile.getWaterScaled(60);
		this.drawTexturedModalRect(j+98, k+78-i2, 208, 80-i2, 16, i2);

		int i3 = tile.getHFScaled(60);
		this.drawTexturedModalRect(j+116, k+78-i3, 192, 80-i3, 16, i3);

		int i4 = tile.getUF6Scaled(60);
		this.drawTexturedModalRect(j+134, k+78-i4, 224, 80-i4, 16, i4);

		int i5 = tile.getHFTimerScaled(24);
		this.drawTexturedModalRect(j+67, k+21, 176, 92, i5, 17);

		int i6 = tile.getUF6TimerScaled(24);
		this.drawTexturedModalRect(j+67, k+58, 176, 92, i6, 17);
	}

}
