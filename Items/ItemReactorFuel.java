package Reika.ReactorCraft.Items;

import Reika.ReactorCraft.Base.ItemReactorMulti;


public class ItemReactorFuel extends ItemReactorMulti {

	public ItemReactorFuel(int tex) {
		super(tex);
		this.setMaxDamage(this.getDataValues());
	}

}
