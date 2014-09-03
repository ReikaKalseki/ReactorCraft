/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;

public class ContainerNuclearCore extends CoreContainer {

	public ContainerNuclearCore(EntityPlayer player, TileEntityNuclearCore te) {
		super(player, te);

		this.addSlotToContainer(new Slot(te, 0, 80, 23));
		this.addSlotToContainer(new Slot(te, 1, 80, 41));
		this.addSlotToContainer(new Slot(te, 2, 80, 59));
		this.addSlotToContainer(new Slot(te, 3, 80, 77));

		this.addSlotToContainer(new SlotFurnace(player, te, 4, 53, 23));
		this.addSlotToContainer(new SlotFurnace(player, te, 5, 53, 41));
		this.addSlotToContainer(new SlotFurnace(player, te, 6, 53, 59));
		this.addSlotToContainer(new SlotFurnace(player, te, 7, 53, 77));

		this.addSlotToContainer(new SlotFurnace(player, te, 8, 107, 23));
		this.addSlotToContainer(new SlotFurnace(player, te, 9, 107, 41));
		this.addSlotToContainer(new SlotFurnace(player, te, 10, 107, 59));
		this.addSlotToContainer(new SlotFurnace(player, te, 11, 107, 77));

		this.addPlayerInventoryWithOffset(player, 0, 16);
	}

}
