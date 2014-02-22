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
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;

public class ContainerPebbleBed extends CoreContainer {

	public ContainerPebbleBed(EntityPlayer player, TileEntityPebbleBed te) {
		super(player, te);

		int[] num = {11, 11, 9, 7, 5, 3, 1};

		int id = 0;
		for (int f = 0; f < num.length; f++) {
			for (int i = 0; i < num[f]; i++) {
				int dx = 18*(11-num[f])/2;
				this.addSlotToContainer(new Slot(te, id, 21+18*i+dx, 25+18*f));
				id++;
			}
		}

		int dx = 31;
		int dy = 77;
		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18+dx, 84 + i * 18+dy));
			}
		}
		dy -= 4;
		for (int j = 0; j < 9; j++)
		{
			this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18+dx, 142+dy));
		}
	}

}
