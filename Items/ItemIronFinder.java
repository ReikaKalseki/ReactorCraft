/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ReactorCraft.Base.ReactorItemBase;

public class ItemIronFinder extends ReactorItemBase {

	private static final PlayerMap<OreCollection> cache = new PlayerMap();

	private static final OreType[] ores = {
		ReikaOreHelper.IRON,
		ReikaOreHelper.REDSTONE,
		ModOreList.NICKEL,
		ModOreList.COBALT,
		ModOreList.CERTUSQUARTZ,
		ModOreList.NIKOLITE,
		ModOreList.MAGNETITE,
		ModOreList.NETHERIRON,
		ModOreList.NETHERREDSTONE,
		ModOreList.NETHERNICKEL,
		ModOreList.NETHERNIKOLITE,
		ModOreList.TESLATITE
	};

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

	public static MultiMap<OreType, Coordinate> getOreNearby(EntityPlayer ep, int range) {
		OreCollection c = cache.get(ep);
		if (c == null || System.currentTimeMillis()-c.time >= 500) {
			c = new OreCollection(ep, findOreNearby(ep, range, ores));
			cache.put(ep, c);
		}
		return c.locations;
	}

	private static MultiMap<OreType, Coordinate> findOreNearby(EntityPlayer ep, int range, OreType... ores) {
		MultiMap<OreType, Coordinate> m = new MultiMap();
		for (int o = 0; o < ores.length; o++) {
			OreType ore = ores[o];
			if (ore.existsInGame()) {
				World world = ep.worldObj;
				int x = MathHelper.floor_double(ep.posX);
				int y = MathHelper.floor_double(ep.posY+ep.getEyeHeight());
				int z = MathHelper.floor_double(ep.posZ);
				Collection<Coordinate> c = new ArrayList();
				for (int i = -range; i <= range; i++) {
					for (int j = -range; j <= range; j++) {
						for (int k = -range; k <= range; k++) {
							int dx = x+i;
							int dy = y+j;
							int dz = z+k;
							ItemStack block = ReikaBlockHelper.getWorldBlockAsItemStack(world, dx, dy, dz);
							if (ReikaItemHelper.listContainsItemStack(ore.getAllOreBlocks(), block)) {
								m.addValue(ore, new Coordinate(dx, dy, dz));
							}
						}
					}
				}
			}
		}
		return m;
	}

	private static class OreCollection {

		public final long time;
		public final MultiMap<OreType, Coordinate> locations;
		public final String player;

		private OreCollection(EntityPlayer ep, MultiMap<OreType, Coordinate> c) {
			locations = c;
			player = ep.getCommandSenderName();
			time = System.currentTimeMillis();
		}

	}

}
