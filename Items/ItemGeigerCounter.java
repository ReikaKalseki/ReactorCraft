/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.Collections;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaEntityHelper.EntityDistanceComparator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Base.ItemReactorTool;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.API.Interfaces.ChargeableTool;

public class ItemGeigerCounter extends ItemReactorTool implements ChargeableTool {

	public ItemGeigerCounter(int tex) {
		super(tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean currentHeld) {
		if (currentHeld && is.getItemDamage() > 0) {
			int r = 20;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(e.posX, e.posY, e.posZ, e.posX, e.posY, e.posZ).expand(r, r, r);
			List<EntityRadiation> li = world.getEntitiesWithinAABB(EntityRadiation.class, box);
			if (!li.isEmpty()) {
				Collections.sort(li, new EntityDistanceComparator(e.posX, e.posY, e.posZ));
				EntityRadiation er = li.get(0);
				double dist = ReikaMathLibrary.py3d(e.posX-er.posX, e.posY-er.posY, e.posZ-er.posZ);
				if (itemRand.nextDouble()*r*16 > dist*dist) {
					float vol = dist > 1 ? 1 : 2-(float)dist;
					e.playSound("random.click", 1, 2F);
				}
			}
			if (itemRand.nextInt(8) == 0)
				is.setItemDamage(is.getItemDamage()-1);
		}
	}

	@Override
	public int setCharged(ItemStack is, int charge, boolean strongcoil) {
		int ret = is.getItemDamage();
		is.setItemDamage(charge);
		return ret;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs tab, List li) {
		li.add(ReactorItems.GEIGER.getStackOfMetadata(0));
		li.add(ReactorItems.GEIGER.getStackOfMetadata(32000));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		li.add("Charge: "+is.getItemDamage()+" kJ");
	}

}
