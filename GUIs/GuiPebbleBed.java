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
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerPebbleBed;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;

public class GuiPebbleBed extends ReactorGuiBase {

	public GuiPebbleBed(EntityPlayer player, TileEntityPebbleBed fuel) {
		super(new ContainerPebbleBed(player, fuel), player, fuel);
		ySize = 237;
		xSize = 240;
	}

	@Override
	public String getGuiTexture() {
		return "pebblegui";
	}
}
