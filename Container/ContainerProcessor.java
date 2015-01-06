/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;

public class ContainerProcessor extends CoreContainer {

	private TileEntityUProcessor proc;

	public ContainerProcessor(EntityPlayer player, TileEntityUProcessor te) {
		super(player, te);

		this.addSlotToContainer(new Slot(te, 0, 44, 22));
		this.addSlotToContainer(new Slot(te, 1, 44, 40));
		this.addSlotToContainer(new Slot(te, 2, 44, 58));

		this.addPlayerInventoryWithOffset(player, 0, 9);

		proc = te;
	}

	/**
	 * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
	 */
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);

			icrafting.sendProgressBarUpdate(this, 0, proc.HF_timer);
			icrafting.sendProgressBarUpdate(this, 1, proc.UF6_timer);
			icrafting.sendProgressBarUpdate(this, 2, proc.getWater());
			icrafting.sendProgressBarUpdate(this, 3, proc.getHF());
			icrafting.sendProgressBarUpdate(this, 4, proc.getUF6());
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		switch(par1) {
		case 0: proc.HF_timer = par2; break;
		case 1: proc.UF6_timer = par2; break;
		case 2: proc.setWater(par2); break;
		case 3: proc.setHF(par2); break;
		case 4: proc.setUF6(par2); break;
		}
	}
}
