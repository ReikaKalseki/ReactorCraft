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
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Container.ContainerNuclearCore;

import net.minecraft.entity.player.EntityPlayer;

public class GuiNuclearCore extends ReactorGuiBase {

	public GuiNuclearCore(EntityPlayer player, TileEntityNuclearCore fuel) {
		super(new ContainerNuclearCore(player, fuel), player, fuel);
		ySize = 182;
	}

	@Override
	public String getGuiTexture() {
		return "fuelrod";
	}

}