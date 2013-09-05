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
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;

public class ContainerFuelRod extends CoreContainer {

	public ContainerFuelRod(EntityPlayer player, TileEntityFuelRod te) {
		super(player, te);

		this.addSlotToContainer(new Slot(te, 0, 80, 23));
		this.addSlotToContainer(new Slot(te, 1, 80, 41));
		this.addSlotToContainer(new Slot(te, 2, 80, 59));
		this.addSlotToContainer(new Slot(te, 3, 80, 77));

		this.addPlayerInventoryWithOffset(player, 0, 16);
	}

}
