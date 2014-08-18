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
import Reika.ReactorCraft.Container.ContainerWasteStorage;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;

import net.minecraft.entity.player.EntityPlayer;

public class GuiWasteStorage extends ReactorGuiBase {

	public GuiWasteStorage(EntityPlayer player, TileEntityWasteStorage fuel) {
		super(new ContainerWasteStorage(player, fuel), player, fuel);
		ySize = 186;
	}

	@Override
	public String getGuiTexture() {
		return "wastestorage";
	}
}
