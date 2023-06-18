/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityWasteDecayer;

public class ContainerWasteDecayer extends CoreContainer {

	public ContainerWasteDecayer(EntityPlayer player, TileEntityWasteDecayer te) {
		super(player, te);

		int w = 5;
		int h = 3;

		int dx = (w-5)*9;
		for (int i = 0; i < h; i++) {
			for (int k = 0; k < w; k++) {
				this.addSlotToContainer(new Slot(te, i*w+k, 44+k*18+dx, 22+i*18));
			}
		}

		this.addPlayerInventoryWithOffset(player, 0, 9);
	}

}
