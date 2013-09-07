package Reika.ReactorCraft.Registry;

public enum ReactorOres {

	FLUORITE(		0, 48, 3, 8, 0),
	PITCHBLENDE(	0, 16, 9, 1, 0), //only if !Industrial ??
	CADMIUM(		0, 32, 8, 4, 0),
	INDIUM(			0, 16, 8, 4, 0),
	SILVER(			0, 40, 8, 4, 0);

	public final int minY;
	public final int maxY;
	public final int veinSize;
	public final int perChunk;
	public final boolean shouldGen;
	public final int dimensionID;

	public static final ReactorOres[] oreList = values();

	private ReactorOres(int min, int max, int size, int dim, int count) {
		this(min, max, size, count, dim, true);
	}

	private ReactorOres(int min, int max, int size, int count, int dim, boolean gen) {
		minY = min;
		maxY = max;
		veinSize = size;
		perChunk = count;
		shouldGen = gen;
		dimensionID = dim;
	}

}
