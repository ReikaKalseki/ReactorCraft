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
import net.minecraft.inventory.SlotFurnace;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;

public class ContainerFuelRod extends CoreContainer {

	public ContainerFuelRod(EntityPlayer player, TileEntityFuelRod te) {
		super(player, te);

		this.addSlotToContainer(new Slot(te, 0, 80, 23));
		this.addSlotToContainer(new Slot(te, 1, 80, 41));
		this.addSlotToContainer(new Slot(te, 2, 80, 59));
		this.addSlotToContainer(new Slot(te, 3, 80, 77));

		this.addSlotToContainer(new SlotFurnace(player, te, 4, 50, 23));
		this.addSlotToContainer(new SlotFurnace(player, te, 5, 50, 41));
		this.addSlotToContainer(new SlotFurnace(player, te, 6, 50, 59));
		this.addSlotToContainer(new SlotFurnace(player, te, 7, 50, 77));

		this.addSlotToContainer(new SlotFurnace(player, te, 8, 110, 23));
		this.addSlotToContainer(new SlotFurnace(player, te, 9, 110, 41));
		this.addSlotToContainer(new SlotFurnace(player, te, 10, 110, 59));
		this.addSlotToContainer(new SlotFurnace(player, te, 11, 110, 77));

		this.addPlayerInventoryWithOffset(player, 0, 16);
	}

}
