/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

public abstract class ItemReactorTool extends ReactorItemBase {

	public ItemReactorTool(int ID, int tex) {
		super(ID, tex);

		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
	}

}
