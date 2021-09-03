/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.BlockRegistry;


public enum RadiationShield {

	STEEL("Steel", 90, 95, BlockKey.fromItem(ItemStacks.steelblock)),
	CONCRETE("Concrete", 60, 70, new BlockKey(ReactorBlocks.MATS.getBlockInstance(), MatBlocks.CONCRETE.ordinal())),
	WATER("Water", 30, 10, new BlockKey(Blocks.water), new BlockKey(Blocks.flowing_water)),
	BEDINGOT("Bedrock Ingot", 97.5, 100, BlockKey.fromItem(ItemStacks.bedingotblock)),
	LEAD("Lead", 75, 50, "blockLead"),
	OBSIDIAN("Obsidian", 50, 80, new BlockKey(Blocks.obsidian)),
	BLASTGLASS("Blast Glass", 80, 20, new BlockKey(BlockRegistry.BLASTGLASS.getBlockInstance()));

	public final double neutronAbsorbChance;
	public final double radiationDeflectChance;
	public final String displayName;

	private final ArrayList<BlockKey> blocks = new ArrayList();

	private static final BlockMap<RadiationShield> blockMap = new BlockMap();

	public static final RadiationShield[] shieldList = values();

	private RadiationShield(String s, double n, double r, BlockKey... bks) {
		displayName = s;
		neutronAbsorbChance = n;
		radiationDeflectChance = r;

		for (int i = 0; i < bks.length; i++) {
			BlockKey bk = bks[i];
			blocks.add(bk);
		}
	}

	private RadiationShield(String s, double n, double r, String... ores) {
		this(s, n, r, getBlockKeysForOreDicts(ores));
	}

	private static BlockKey[] getBlockKeysForOreDicts(String[] ores) {
		ArrayList<BlockKey> li = new ArrayList();
		for (int i = 0; i < ores.length; i++) {
			String s = ores[i];
			ArrayList<ItemStack> items = OreDictionary.getOres(s);
			for (ItemStack is : items) {
				Block b = Block.getBlockFromItem(is.getItem());
				if (b != null) {
					li.add(new BlockKey(b, is.getItemDamage()));
				}
			}
		}
		return li.toArray(new BlockKey[li.size()]);
	}

	public static RadiationShield getFrom(Block b, int meta) {
		return blockMap.get(b, meta);
	}

	static {
		for (int i = 0; i < shieldList.length; i++) {
			RadiationShield rs = shieldList[i];
			for (BlockKey bk : rs.blocks)
				blockMap.put(bk, rs);
		}
	}

	public static String getDataAsString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < shieldList.length; i++) {
			RadiationShield type = shieldList[i];
			sb.append(type.displayName+": "+type.neutronAbsorbChance+"% neutron absorption, "+type.radiationDeflectChance+" radiation containment");
			sb.append("\n");
		}
		return sb.toString();
	}

}
