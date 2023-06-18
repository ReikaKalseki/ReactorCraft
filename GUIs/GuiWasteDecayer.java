/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerWasteDecayer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityWasteDecayer;

public class GuiWasteDecayer extends ReactorGuiBase {

	public GuiWasteDecayer(EntityPlayer player, TileEntityWasteDecayer fuel) {
		super(new ContainerWasteDecayer(player, fuel), player, fuel);
		ySize = 175;
	}

	@Override
	public String getGuiTexture() {
		return "wastedecayer";
	}
}
