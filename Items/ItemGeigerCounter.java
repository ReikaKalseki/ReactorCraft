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

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Base.ItemReactorTool;
import Reika.ReactorCraft.Entities.EntityRadiation;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemGeigerCounter extends ItemReactorTool {

	public ItemGeigerCounter(int tex) {
		super(tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean currentHeld) {
		if (currentHeld) {
			int r = 20;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(e.posX, e.posY, e.posZ, e.posX, e.posY, e.posZ).expand(r, r, r);
			List<EntityRadiation> li = world.getEntitiesWithinAABB(EntityRadiation.class, box);
			if (!li.isEmpty()) {
				EntityRadiation er = li.get(0);
				double dist = ReikaMathLibrary.py3d(e.posX-er.posX, e.posY-er.posY, e.posZ-er.posZ);
				if (itemRand.nextDouble()*r*16 > dist*dist) {
					float vol = dist > 1 ? 1 : 2-(float)dist;
					e.playSound("random.click", 1, 2F);
				}
			}
		}
	}

}