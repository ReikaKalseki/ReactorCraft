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

import net.minecraft.item.ItemBlock;

public class ItemBlockReactorMat extends ItemBlock {

	public ItemBlockReactorMat(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

}
