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

import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.Auxiliary.SlotNuclearWaste;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerWasteStorage extends CoreContainer {

	public ContainerWasteStorage(EntityPlayer player, TileEntityWasteStorage te) {
		super(player, te);

		this.addSlotToContainer(new SlotNuclearWaste(te, 0, 70, 20));
		this.addSlotToContainer(new SlotNuclearWaste(te, 1, 90, 20));

		this.addSlotToContainer(new SlotNuclearWaste(te, 2, 50, 40));
		this.addSlotToContainer(new SlotNuclearWaste(te, 3, 70, 40));
		this.addSlotToContainer(new SlotNuclearWaste(te, 4, 90, 40));
		this.addSlotToContainer(new SlotNuclearWaste(te, 5, 110, 40));

		this.addSlotToContainer(new SlotNuclearWaste(te, 6, 50, 60));
		this.addSlotToContainer(new SlotNuclearWaste(te, 7, 70, 60));
		this.addSlotToContainer(new SlotNuclearWaste(te, 8, 90, 60));
		this.addSlotToContainer(new SlotNuclearWaste(te, 9, 110, 60));

		this.addSlotToContainer(new SlotNuclearWaste(te, 10, 70, 80));
		this.addSlotToContainer(new SlotNuclearWaste(te, 11, 90, 80));

		this.addPlayerInventoryWithOffset(player, 0, 20);
	}

}
