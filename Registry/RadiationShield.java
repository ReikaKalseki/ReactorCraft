/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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


public enum RadiationShield {

	STEEL(90, 95, new BlockKey(ItemStacks.steelblock)),
	CONCRETE(60, 70, new BlockKey(ReactorBlocks.MATS.getBlockInstance(), MatBlocks.CONCRETE.ordinal())),
	WATER(30, 10, new BlockKey(Blocks.water), new BlockKey(Blocks.flowing_water)),
	BEDINGOT(97.5, 100, new BlockKey(ItemStacks.bedingotblock)),
	LEAD(75, 50, "blockLead"),
	OBSIDIAN(50, 80, new BlockKey(Blocks.obsidian));

	public final double neutronAbsorbChance;
	public final double radiationDeflectChance;

	private final ArrayList<BlockKey> blocks = new ArrayList();

	private static final BlockMap<RadiationShield> blockMap = new BlockMap();

	public static final RadiationShield[] shieldList = values();

	private RadiationShield(double n, double r, BlockKey... bks) {
		neutronAbsorbChance = n;
		radiationDeflectChance = r;

		for (int i = 0; i < bks.length; i++) {
			BlockKey bk = bks[i];
			blocks.add(bk);
		}
	}

	private RadiationShield(double n, double r, String... ores) {
		this(n, r, getBlockKeysForOreDicts(ores));
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

}
