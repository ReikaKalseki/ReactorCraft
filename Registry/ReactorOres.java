package Reika.ReactorCraft.Registry;

import net.minecraft.world.IBlockAccess;


public enum ReactorOres {

	FLUORITE(		32, 56, 8, 12, 0, 	false,	"Fluorite"),
	PITCHBLENDE(	8, 24, 16, 1, 0,	true,	"Pitchblende"), //only if !Industrial ??
	CADMIUM(		12, 32, 9, 3, 0,	true,	"Cadmium Ore"),
	INDIUM(			0, 16, 7, 2, 0,		true,	"Indium Ore"),
	SILVER(			16, 40, 9, 2, 0,	true, 	"Silver Ore");

	public final int minY;
	public final int maxY;
	public final int veinSize;
	public final int perChunk;
	public final boolean shouldGen;
	public final int dimensionID;
	public final String oreName;
	public final boolean dropsSelf;

	public static final ReactorOres[] oreList = values();

	private ReactorOres(int min, int max, int size, int count, int dim, boolean drop, String name) {
		this(min, max, size, count, dim, drop, name, true);
	}

	private ReactorOres(int min, int max, int size, int count, int dim, boolean drop, String name, boolean gen) {
		minY = min;
		maxY = max;
		veinSize = size;
		perChunk = count;
		shouldGen = gen;
		dimensionID = dim;
		oreName = name;
		dropsSelf = drop;
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

	public int getBlockID() {
		if (this == FLUORITE)
			return ReactorBlocks.FLUORITEORE.getBlockID();
		else
			return ReactorBlocks.ORE.getBlockID();
	}


	public int getBlockMetadata() {
		if (this == FLUORITE)
			return 0;
		else
			return this.ordinal();
	}
}
