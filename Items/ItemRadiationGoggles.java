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
import net.minecraft.item.ItemStack;

public class ItemRadiationGoggles extends ItemReactorBasic {

	public ItemRadiationGoggles(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int type, Entity e) {
		return type == 0;
	}

}
