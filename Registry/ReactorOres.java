/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;

public enum ReactorOres {

	FLUORITE(		32, 60, 8, 	12, 0, 	0,	0.4F,	"ore.fluorite"),
	PITCHBLENDE(	8, 	24, 16, 3, 	0,	1,	1F,		"ore.pitchblende"),
	CADMIUM(		12, 32, 9, 	3, 	0,	2,	0.7F,	"ore.cadmium"),
	INDIUM(			0, 	16, 7, 	2, 	0,	2,	1F,		"ore.indium"),
	SILVER(			16, 40, 9, 	2, 	0,	2,	0.5F, 	"ore.silver", ReactorOptions.SILVERORE.getState()),
	ENDBLENDE(		0, 	64, 16, 6, 	1,	1,	1F,		"ore.pitchblende"),
	AMMONIUM(		32,	32,	8,	6,	-1,	1,	0.8F,	"ore.ammonium"),
	CALCITE(		32, 60,	4,	12,	0,	0,	0.4F,	"ore.calcite", ReactorOptions.CALCITEORE.getState()),
	MAGNETITE(		60,	128,16,	7,	0,	1,	0.8F,	"ore.magnetite", ReactorOptions.MAGNETORE.getState());

	public final int minY;
	public final int maxY;
	public final int veinSize;
	public final int perChunk;
	public final boolean shouldGen;
	public final int dimensionID;
	public final String oreName;
	public final float xpDropped;
	public final int harvestLevel;

	public static final ReactorOres[] oreList = values();

	private static final EnumMap<ReactorOres, Boolean> equivalents = new EnumMap(ReactorOres.class);

	private ReactorOres(int min, int max, int size, int count, int dim, int level, float xp, String name) {
		this(min, max, size, count, dim, level, xp, name, true);
	}

	private ReactorOres(int min, int max, int size, int count, int dim, int level, float xp, String name, boolean gen) {
		minY = min;
		maxY = max;
		veinSize = size;
		perChunk = count;
		shouldGen = gen;
		dimensionID = dim;
		oreName = StatCollector.translateToLocal(name);
		xpDropped = xp;
		harvestLevel = level;
	}

	@Override
	public String toString() {
		return this.name()+" "+perChunk+"x"+veinSize+" between "+minY+" and "+maxY;
	}

	public static ReactorOres getOre(IBlockAccess iba, int x, int y, int z) {
		Block id = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (id == ReactorBlocks.FLUORITEORE.getBlockInstance())
			return FLUORITE;
		if (id != ReactorBlocks.ORE.getBlockInstance())
			return null;
		return oreList[meta];
	}

	public static ReactorOres getOre(Block id, int meta) {
		if (id == ReactorBlocks.FLUORITEORE.getBlockInstance())
			return FLUORITE;
		if (id != ReactorBlocks.ORE.getBlockInstance())
			return null;
		return oreList[meta];
	}

	public String getTextureName() {
		return "ReactorCraft:"+this.name().toLowerCase();
	}

	public String getDictionaryName() {
		if (this == ENDBLENDE)
			return PITCHBLENDE.getDictionaryName();
		return "ore"+ReikaStringParser.capFirstChar(this.name());
	}

	public String getProductDictionaryName() {
		switch(this) {
		case FLUORITE:
			return "gem"+ReikaStringParser.capFirstChar(this.name());
		case PITCHBLENDE:
		case ENDBLENDE:
			return "ingotUranium";
		case CALCITE:
		case MAGNETITE:
			return "gem"+ReikaStringParser.capFirstChar(this.name());
		case AMMONIUM:
			return "dust"+ReikaStringParser.capFirstChar(this.name());
		default:
			return "ingot"+ReikaStringParser.capFirstChar(this.name());
		}
	}

	public Block getBlock() {
		if (this == FLUORITE)
			return ReactorBlocks.FLUORITEORE.getBlockInstance();
		else
			return ReactorBlocks.ORE.getBlockInstance();
	}


	public int getBlockMetadata() {
		if (this == FLUORITE)
			return FluoriteTypes.WHITE.ordinal();
		else
			return this.ordinal();
	}

	public ItemStack getOreBlock() {
		return new ItemStack(this.getBlock(), 1, this.getBlockMetadata());
	}

	public ItemStack getProduct() {
		switch(this) {
		case FLUORITE:
			return ReactorItems.FLUORITE.getStackOfMetadata(FluoriteTypes.WHITE.ordinal());
		case ENDBLENDE:
			return PITCHBLENDE.getProduct();
		case AMMONIUM:
			return ReactorStacks.ammonium.copy();
		case CALCITE:
			return ReactorStacks.calcite.copy();
		case MAGNETITE:
			return ReactorStacks.lodestone.copy();
		default:
			return ReactorItems.INGOTS.getStackOfMetadata(this.getProductMetadata());
		}
	}

	public List<ItemStack> getOreDrop(int meta) {
		switch(this) {
		case FLUORITE:
			return ReikaJavaLibrary.makeListFrom(ReactorItems.FLUORITE.getStackOfMetadata(meta));
		case CALCITE:
			return ReikaJavaLibrary.makeListFrom(ReactorStacks.calcite.copy());
		case AMMONIUM:
			return ReikaJavaLibrary.makeListFrom(ReactorStacks.ammonium.copy(), new ItemStack(Blocks.netherrack));
		case MAGNETITE:
			return ReikaJavaLibrary.makeListFrom(ReactorStacks.lodestone.copy());
		default:
			return ReikaJavaLibrary.makeListFrom(new ItemStack(ReactorBlocks.ORE.getBlockInstance(), 1, meta));
		}
	}

	public int getProductMetadata() {
		return this.ordinal()-1;
	}

	public String getProductName() {
		switch(this) {
		case PITCHBLENDE:
		case ENDBLENDE:
			return StatCollector.translateToLocal("item.uranium");
		default:
			return StatCollector.translateToLocal("item."+this.name().toLowerCase());
		}
	}

	public Block getReplaceableBlock() {
		switch(dimensionID) {
		case 0:
			return Blocks.stone;
		case 1:
			return Blocks.end_stone;
		case -1:
			return Blocks.netherrack;
		case 7:
			return Blocks.stone;
		default:
			return Blocks.stone;
		}
	}

	public boolean isValidDimension(int id) {
		if (id == dimensionID)
			return true;
		if (id == ReikaTwilightHelper.getDimensionID() && dimensionID == 0)
			return true;
		if (id == ExtraUtilsHandler.getInstance().darkID && dimensionID == 0)
			return true;
		if (dimensionID == 0 && id != -1 && id != 1)
			return true;
		return false;
	}

	public boolean isValidBiome(BiomeGenBase biome) {
		switch(this) {
		case PITCHBLENDE:
			if ("Rainbow Forest".equals(biome.biomeName))
				return true;
			return biome == BiomeGenBase.river || biome == BiomeGenBase.ocean || biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore;
		default:
			return true;
		}
	}

	public boolean canGenerateInChunk(World world, int chunkX, int chunkZ) {
		int id = world.provider.dimensionId;
		if (!this.shouldGen())
			return false;
		if (!this.isValidDimension(id))
			return false;
		if (id == ExtraUtilsHandler.getInstance().darkID)
			return true;
		return this.isValidBiome(world.getBiomeGenForCoords(chunkX, chunkZ)) || id == ReikaTwilightHelper.getDimensionID();
	}

	private boolean shouldGen() {
		return shouldGen || !this.hasEquivalents();
	}

	public boolean hasEquivalents() {
		Boolean b = equivalents.get(this);
		if (b == null) {
			ArrayList<ItemStack> li = OreDictionary.getOres(this.getDictionaryName());
			boolean flag = false;
			for (int i = 0; i < li.size() && !flag; i++) {
				ItemStack is = li.get(i);
				if (!ReikaItemHelper.matchStacks(is, this.getOreBlock())) {
					b = true;
					flag = true;
				}
			}
			b = flag;
			equivalents.put(this, b);
		}
		return b.booleanValue();
	}

	public boolean canGenAt(World world, int x, int y, int z) {
		if (this == AMMONIUM)
			return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.lava) != null || ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.flowing_lava) != null;
		return true;
	}

	public boolean dropsSelf(int meta) {
		List<ItemStack> li = this.getOreDrop(meta);
		return li.size() == 1 && li.get(0).getItem() == Item.getItemFromBlock(ReactorBlocks.ORE.getBlockInstance()) && li.get(0).getItemDamage() == meta;
	}
}
