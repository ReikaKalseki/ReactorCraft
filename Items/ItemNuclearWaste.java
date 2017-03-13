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

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Base.ItemReactorMulti;
import Reika.ReactorCraft.Entities.EntityNuclearWaste;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNuclearWaste extends ItemReactorMulti {

	public ItemNuclearWaste(int tex) {
		super(tex);
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		EntityNuclearWaste ei = new EntityNuclearWaste(world, location.posX, location.posY, location.posZ, itemstack);
		ei.motionX = location.motionX;
		ei.motionY = location.motionY;
		ei.motionZ = location.motionZ;
		ei.delayBeforeCanPickup = 10;
		return ei;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int p4, boolean p5) {
		if (e instanceof EntityLivingBase) {
			if (RadiationEffects.instance.applyEffects((EntityLivingBase)e, RadiationIntensity.HIGHLEVEL)) {
				if (e instanceof EntityPlayer) {
					if (!((EntityPlayer)e).capabilities.isCreativeMode)
						ReactorAchievements.HOLDWASTE.triggerAchievement((EntityPlayer)e);
				}
			}
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean adv) {
		List<Isotopes> iso = WasteManager.getWasteList();
		Isotopes atom = iso.get(is.getItemDamage());
		li.add(atom.getDisplayName());
		li.add("Half Life: "+atom.getHalfLifeAsDisplay());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item ID, CreativeTabs cr, List li)
	{
		List<Isotopes> waste = WasteManager.getWasteList();
		for (int i = 0; i < waste.size(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ReactorItems.WASTE.getBasicName();
	}

}
