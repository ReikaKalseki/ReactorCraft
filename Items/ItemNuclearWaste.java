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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.ReactorCraft.RadiationEffects;
import Reika.ReactorCraft.WasteManager;
import Reika.ReactorCraft.Base.ReactorItemBase;
import Reika.ReactorCraft.Entities.EntityNuclearWaste;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNuclearWaste extends ReactorItemBase {

	public ItemNuclearWaste(int ID, int tex) {
		super(ID, tex);
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack)
	{
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityNuclearWaste ei = new EntityNuclearWaste(world, location.posX, location.posY, location.posZ, itemstack);
		ei.motionX = location.motionX;
		ei.motionY = location.motionY;
		ei.motionZ = location.motionZ;
		ei.delayBeforeCanPickup = 10;
		return ei;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int p4, boolean p5) {
		if (e instanceof EntityLiving)
			RadiationEffects.applyEffects((EntityLiving)e);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean adv) {
		NBTTagCompound nbt = is.stackTagCompound;
		if (nbt != null) {
			Isotopes atom = Isotopes.getIsotope(nbt.getInteger("iso"));
			li.add(atom.getDisplayName());
			li.add("Half Life: "+atom.getHalfLifeAsDisplay());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		List<Isotopes> waste = WasteManager.getWasteList();
		for (int i = 0; i < waste.size(); i++) {
			ItemStack item = new ItemStack(ID, 1, 0);
			item.stackTagCompound = new NBTTagCompound();
			item.stackTagCompound.setInteger("iso", waste.get(i).ordinal());
			li.add(item);
		}
	}

}
