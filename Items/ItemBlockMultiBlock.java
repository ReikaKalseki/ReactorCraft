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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemBlockMultiBlock extends ItemBlock {

	public ItemBlockMultiBlock(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	public BlockMultiBlock getMultiBlockInstance() {
		return (BlockMultiBlock)Block.blocksList[itemID];
	}

	private int getDataValues() {
		return this.getMultiBlockInstance().getNumberVariants();
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		return this.getMultiBlockInstance().getName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			boolean full = this.getMultiBlockInstance().checkForFullMultiBlock(world, x, y, z, ReikaPlayerAPI.getDirectionFromPlayerLook(player, false));
			if (full) {

			}
		}
		return flag;
	}
}
