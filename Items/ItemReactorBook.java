/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemReactorBook extends ReactorItemBase {

	public ItemReactorBook(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer ep)
	{
		ep.openGui(ReactorCraft.instance, 10, world, 0, 0, 0);
		return itemstack;
	}

}
