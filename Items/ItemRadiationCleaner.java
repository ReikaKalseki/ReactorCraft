/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemRadiationCleaner extends ReactorItemBase {

	public ItemRadiationCleaner(int ID, int tex) {
		super(ID, tex);
		maxStackSize = 1;
		canRepair = false;
		hasSubtypes = false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		ReikaJavaLibrary.pConsole(entity);
		return false;
	}
}
