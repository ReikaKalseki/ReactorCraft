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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ReactorCraft.API.MagneticOreOverride;
import Reika.ReactorCraft.Base.ReactorItemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemIronFinder extends ReactorItemBase {

	private static final PlayerMap<OreCollection> cache = new PlayerMap();

	private static final HashSet<OreType> ores = new HashSet(ReikaJavaLibrary.makeListFrom(
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
			ModOreList.TESLATITE,
			ModOreList.SILICON,
			ModOreList.DILITHIUM,
			ModOreList.MIMICHITE
			));

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
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean selected) {
		if (ModList.CHROMATICRAFT.isLoaded()) {
			this.tickAuraPouch(is, world, e, slot, selected);
		}
	}

	@ModDependent(ModList.CHROMATICRAFT)
	private void tickAuraPouch(ItemStack is, World world, Entity e, int slot, boolean selected) {
		if (e instanceof EntityPlayer && ChromaItems.AURAPOUCH.matchWith(((EntityPlayer)e).inventory.mainInventory[slot]) && world.getTotalWorldTime()%8 == 0)
			e.getEntityData().setLong("ironfinder", world.getTotalWorldTime());
	}

	public static MultiMap<OreType, Coordinate> getOreNearby(EntityPlayer ep, int range) {
		OreCollection c = cache.get(ep);
		if (c == null || System.currentTimeMillis()-c.time >= 500) {
			c = new OreCollection(ep, findOreNearby(ep, range));
			cache.put(ep, c);
		}
		c.locations.lock();
		return c.locations;
	}

	private static MultiMap<OreType, Coordinate> findOreNearby(EntityPlayer ep, int range) {
		MultiMap<OreType, Coordinate> m = new MultiMap();
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
					Block b = world.getBlock(dx, dy, dz);
					if (b instanceof MagneticOreOverride) {
						if (((MagneticOreOverride)b).showOnHUD(world, dx, dy, dz))
							m.addValue(((MagneticOreOverride)b).getOreType(world, dx, dy, dz), new Coordinate(dx, dy, dz));
					}
					OreType ore = ReikaOreHelper.getFromVanillaOre(b);
					if (ore == null)
						ore = ModOreList.getModOreFromOre(b, world.getBlockMetadata(dx, dy, dz));
					if (ores.contains(ore))
						m.addValue(ore, new Coordinate(dx, dy, dz));
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
