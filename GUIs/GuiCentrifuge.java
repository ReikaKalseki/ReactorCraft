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
import Reika.ReactorCraft.Container.ContainerCentrifuge;
import Reika.ReactorCraft.TileEntities.TileEntityCentrifuge;

public class GuiCentrifuge extends ReactorGuiBase {

	private TileEntityCentrifuge tile;

	public GuiCentrifuge(EntityPlayer player, TileEntityCentrifuge proc) {
		super(new ContainerCentrifuge(player, proc), player, proc);
		ySize = 166;
		tile = proc;
	}

	@Override
	public String getGuiTexture() {
		return "centrifuge";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		mc.renderEngine.bindTexture("/font/glyph_AA.png");

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRenderer, tile.getName(), xSize/2, 5, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		int i4 = tile.getUF6Scaled(60);
		this.drawTexturedModalRect(j+80, k+78-i4, 224, 80-i4, 16, i4);

		int i5 = tile.getProcessingScaled(48);
		this.drawTexturedModalRect(j+104, k+18, 216, 84, 4, i5);
	}

}
