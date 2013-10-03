/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;


public enum ReactorOres {

	FLUORITE(		32, 56, 8, 	12, 0, 	0, 0.2F,	"Fluorite"),
	PITCHBLENDE(	8, 	24, 16, 3, 	0,	2, 1F,		"Pitchblende"),
	CADMIUM(		12, 32, 9, 	3, 	0,	2, 0.7F,	"Cadmium Ore"),
	INDIUM(			0, 	16, 7, 	2, 	0,	2, 1F,		"Indium Ore"),
	SILVER(			16, 40, 9, 	2, 	0,	2, 0.5F, 	"Silver Ore", ReactorOptions.SILVERORE.getState()),
	ENDBLENDE(		0, 	64, 16, 6, 	1,	2, 1F,		"Pitchblende");

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
		oreName = name;
		xpDropped = xp;
		harvestLevel = level;
	}

	@Override
	public String toString() {
		return this.name()+" "+perChunk+"x"+veinSize+" between "+minY+" and "+maxY;
	}

	public static ReactorOres getOre(IBlockAccess iba, int x, int y, int z) {
		int id = iba.getBlockId(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (id == ReactorBlocks.FLUORITEORE.getBlockID())
			return FLUORITE;
		if (id != ReactorBlocks.ORE.getBlockID())
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
		default:
			return "ingot"+ReikaStringParser.capFirstChar(this.name());
		}
	}

	public int getBlockID() {
		if (this == FLUORITE)
			return ReactorBlocks.FLUORITEORE.getBlockID();
		else
			return ReactorBlocks.ORE.getBlockID();
	}


	public int getBlockMetadata() {
		if (this == FLUORITE)
			return FluoriteTypes.WHITE.ordinal();
		else
			return this.ordinal();
	}

	public ItemStack getOreBlock() {
		return new ItemStack(this.getBlockID(), 1, this.getBlockMetadata());
	}

	public ItemStack getProduct() {
		switch(this) {
		case FLUORITE:
			return ReactorItems.FLUORITE.getStackOfMetadata(FluoriteTypes.WHITE.ordinal());
		case ENDBLENDE:
			return PITCHBLENDE.getProduct();
		default:
			return ReactorItems.INGOTS.getStackOfMetadata(this.getProductMetadata());
		}
	}

	public int getProductMetadata() {
		return this.ordinal()-1;
	}

	public String getProductName() {
		switch(this) {
		case FLUORITE:
			return "Fluorite Crystal";
		case PITCHBLENDE:
		case ENDBLENDE:
			return "Raw Uranium Ingot";
		default:
			return oreName.substring(0, oreName.length()-4)+" Ingot";
		}
	}

	public int getReplaceableBlock() {
		switch(dimensionID) {
		case 0:
			return Block.stone.blockID;
		case 1:
			return Block.whiteStone.blockID;
		case -1:
			return Block.netherrack.blockID;
		case 7:
			return Block.stone.blockID;
		default:
			return Block.stone.blockID;
		}
	}

	public boolean isValidDimension(int id) {
		if (id == dimensionID)
			return true;
		if (id == ReikaTwilightHelper.getDimensionID() && dimensionID == 0)
			return true;
		return false;
	}

	public boolean isValidBiome(BiomeGenBase biome) {
		switch(this) {
		case PITCHBLENDE:
			return biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore;
		default:
			return true;
		}
	}

	public boolean canGenerateInChunk(World world, int chunkX, int chunkZ) {
		int id = world.provider.dimensionId;
		if (!shouldGen)
			return false;
		if (!this.isValidDimension(id))
			return false;
		return this.isValidBiome(world.getBiomeGenForCoords(chunkX, chunkZ)) || id == ReikaTwilightHelper.getDimensionID();
	}
}
