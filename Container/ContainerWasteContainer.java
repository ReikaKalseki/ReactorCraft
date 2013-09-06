/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;

public class ContainerWasteContainer extends CoreContainer {

	public ContainerWasteContainer(EntityPlayer player, TileEntityWasteContainer te) {
		super(player, te);

		int w = te.WIDTH;
		int h = te.HEIGHT;

		int dx = 0;
		if (w > 5) {
			dx = -(w-5)*9;
		}
		if (w < 5) {
			dx = (w-5)*9;
		}
		for (int i = 0; i < h; i++) {
			for (int k = 0; k < w; k++) {
				this.addSlotToContainer(new Slot(te, i*5+k, 44+k*18+dx, 22+i*18));
			}
		}

		this.addPlayerInventoryWithOffset(player, 0, 9);
	}

}
