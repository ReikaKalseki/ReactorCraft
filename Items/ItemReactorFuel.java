/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import Reika.ReactorCraft.Base.ItemReactorMulti;


public class ItemReactorFuel extends ItemReactorMulti {

	public ItemReactorFuel(int tex) {
		super(tex);
		this.setMaxDamage(this.getDataValues());
		this.setNoRepair();
	}

	@Override
	public boolean isDamageable()
	{
		return true;
	}

}
