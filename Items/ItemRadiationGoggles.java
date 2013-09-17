package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ReactorCraft.Entities.EntityRadiation;

public class ItemRadiationGoggles extends ItemReactorBasic {

	public ItemRadiationGoggles(int ID, int tex) {
		super(ID, tex);
	}

	//have a look in thaum goggles
	@Override
	public void onArmorTickUpdate(World world, EntityPlayer ep, ItemStack is) {
		int x = (int)Math.floor(ep.posX);
		int y = (int)Math.floor(ep.posY);
		int z = (int)Math.floor(ep.posZ);
		int r = 24;
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x, y, z).expand(r, r, r);
		List<EntityRadiation> inbox = world.getEntitiesWithinAABB(EntityRadiation.class, box);
		boolean held = false;
		for (int i = 0; i < inbox.size(); i++) {
			EntityRadiation e = inbox.get(i);
			e.setRendered();
		}
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int type) {
		return type == 0;
	}

}
