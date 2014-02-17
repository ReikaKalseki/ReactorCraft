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

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.ItemReactorTool;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.API.ChargeableTool;

public class ItemRadiationCleaner extends ItemReactorTool implements ChargeableTool {

	public ItemRadiationCleaner(int ID, int tex) {
		super(ID, tex);
		hasSubtypes = false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (is.getItemDamage() > 0) {
			int r = 5;
			AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(ep.posX, ep.posY+ep.getEyeHeight(), ep.posZ, ep.posX, ep.posY+ep.getEyeHeight(), ep.posZ).expand(r, r, r);
			List<EntityRadiation> li = world.getEntitiesWithinAABB(EntityRadiation.class, box);
			for (int i = 0; i < li.size(); i++) {
				EntityRadiation e = li.get(i);
				e.clean();
			}
			is = new ItemStack(is.itemID, is.stackSize, is.getItemDamage()-1);
		}
		return is;
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List li) {
		li.add(ReactorItems.CLEANUP.getStackOfMetadata(32000));
	}
}
