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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemIronFinder extends ReactorItemBase {

	public ItemIronFinder(int tex) {
		super(tex);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {/*
		//ReikaChatHelper.writeString(String.format("%.3f", look.xCoord)+" "+String.format("%.3f", look.yCoord)+" "+String.format("%.3f", look.zCoord));
		int iron = 0;
		for (float i = 0; i <= 12; i += 0.2) {
			int[] xyz = ReikaVectorHelper.getPlayerLookBlockCoords(ep, i);
			Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
			int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
			ItemStack ore = new ItemStack(id, 1, meta);
			if (id == Blocks.iron_ore.blockID) {
				iron++;
			}
			if (ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("oreIron"), ore)) {
				iron++;
			}
		}
		ReikaChatHelper.write(iron+" Iron Ore Detected Within 12m!");*/
		return is;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean isHeld) {/*
		if (isHeld && (e.ticksExisted&15) == 0) {
			int x = MathHelper.floor_double(e.posX);
			int y = MathHelper.floor_double(e.posY);
			int z = MathHelper.floor_double(e.posZ);

int r = 6;
BlockArray iron = getIronOreNearby(world, x, y, z, r);
			ReikaChatHelper.clearChat();
			if (iron.isEmpty()) {
				ReikaChatHelper.write("No iron within "+r+"m.");
			}
			else {
				StringBuilder sb = new StringBuilder();
				sb.append(iron.getSize()+" iron within "+r+"m:\n");
				for (int i = 0; i < iron.getSize(); i++) {
					int[] xyz = iron.getNthBlock(i);
					sb.append("  ");
					sb.append(Arrays.toString(xyz));
					if (i%3 == 0)
						sb.append("\n");
					else
						sb.append(", ");
				}
				ReikaChatHelper.write(sb.toString());
			}
		}*/
	}

	public static BlockArray getIronOreNearby(World world, int x, int y, int z, int range) {
		BlockArray iron = new BlockArray();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					ItemStack ore = ReikaBlockHelper.getWorldBlockAsItemStack(world, dx, dy, dz);
					if (ReikaItemHelper.matchStackWithBlock(ore, Blocks.iron_ore) || ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("oreIron"), ore)) {
						iron.addBlockCoordinate(dx, dy, dz);
					}
				}
			}
		}
		return iron;
	}

}
