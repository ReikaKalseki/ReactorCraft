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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.RotaryCraft.API.Interfaces.Fillable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockLampMulti extends ItemBlock implements Fillable {

	public ItemBlockLampMulti(Block b) {
		super(b);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item ID, CreativeTabs cr, List li) {
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			if (i >= FluoriteTypes.colorList.length)
				this.addFluid(item, FluidRegistry.getFluid("tritium"), this.getCapacity(item));
			li.add(item);
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		int amt = this.getCurrentFillLevel(is);
		float frac = (float)amt/this.getCapacity(is)*100;
		li.add(String.format("%.2f%% full", frac));
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		boolean flag = super.onItemUse(is, ep, world, x, y, z, s, a, b, c);
		return flag;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag && this.isFull(stack)) {
			//world.setBlock(x, y, z, field_150939_a, world.getBlockMetadata(x, y, z)+FluoriteTypes.colorList.length, 3);
			world.getBlock(x, y, z).onBlockAdded(world, x, y, z);
		}
		return flag;
	}

	private int getDataValues() {
		return FluoriteTypes.colorList.length*2;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		int d = is.getItemDamage()%8;
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return ReactorBlocks.LAMP.getMultiValuedName(itemstack.getItemDamage()%8);
	}

	@Override
	public boolean isValidFluid(Fluid f, ItemStack is) {
		return f.equals(FluidRegistry.getFluid("rc tritium"));
	}

	@Override
	public int getCapacity(ItemStack is) {
		return 250;
	}

	@Override
	public int getCurrentFillLevel(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("fill") : 0;
	}

	@Override
	public int addFluid(ItemStack is, Fluid f, int amt) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			int add = Math.min(amt, this.getCapacity(is));
			if (add > 0)
				this.setLevel(is, add);
			return add;
		}
		else {
			int lvl = is.stackTagCompound.getInteger("fill");
			int add = Math.min(this.getCapacity(is)-lvl, amt);
			if (add > 0) {
				lvl += add;
				this.setLevel(is, lvl);
			}
			return add;
		}
	}

	private void setLevel(ItemStack is, int amt) {
		boolean flag = amt > 0 && !this.isFull(is);
		is.stackTagCompound.setInteger("fill", amt);
		if (flag && this.isFull(is)) {
			is.setItemDamage(is.getItemDamage()+FluoriteTypes.colorList.length);
		}
	}

	@Override
	public boolean isFull(ItemStack is) {
		return is.getItemDamage() >= FluoriteTypes.colorList.length || this.getCurrentFillLevel(is) == this.getCapacity(is);
	}

	@Override
	public Fluid getCurrentFluid(ItemStack is) {
		return this.getCurrentFillLevel(is) > 0 ? FluidRegistry.getFluid("rc tritium") : null;
	}

}
