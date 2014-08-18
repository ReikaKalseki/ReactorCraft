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

import Reika.ReactorCraft.Base.ItemReactorTool;

import net.minecraft.init.Items;

public class ItemHeavyBucket extends ItemReactorTool {

	public ItemHeavyBucket(int tex) {
		super(tex);
		this.setContainerItem(Items.bucket);
	}

}
