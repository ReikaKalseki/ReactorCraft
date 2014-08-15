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

import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;

import net.minecraft.entity.player.EntityPlayer;

public class GuiWasteContainer extends ReactorGuiBase {

	public GuiWasteContainer(EntityPlayer player, TileEntityWasteContainer fuel) {
		super(new ContainerWasteContainer(player, fuel), player, fuel);
		ySize = 175;
	}

	@Override
	public String getGuiTexture() {
		return "wastecontainer2";
	}
}