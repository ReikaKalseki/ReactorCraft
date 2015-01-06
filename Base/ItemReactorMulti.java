/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import Reika.ReactorCraft.Registry.ReactorItems;


public class ItemReactorMulti extends ReactorItemBase {

	public ItemReactorMulti(int tex) {
		super(tex);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	public int getDataValues() {
		ReactorItems i = ReactorItems.getEntryByID(this);
		if (i == null)
			return 0;
		return i.getNumberMetadatas();
	}


}
