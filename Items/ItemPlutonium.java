package Reika.ReactorCraft.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemPlutonium extends ReactorItemBase {

	public ItemPlutonium(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity ep, int slot, boolean flag) {
		if (ep instanceof EntityLivingBase) {
			((EntityLivingBase) ep).addPotionEffect(new PotionEffect(ReactorCraft.radiation.id, 1200, 0));
		}
	}
}
