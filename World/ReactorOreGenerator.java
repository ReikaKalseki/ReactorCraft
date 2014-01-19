/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.World;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import cpw.mods.fml.common.IWorldGenerator;

public class ReactorOreGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			if (ore.canGenerateInChunk(world, chunkX, chunkZ)) {
				this.generate(ore, world, random, chunkX*16, chunkZ*16);
			}
		}
	}

	public static void generate(ReactorOres ore, World world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		//ReikaJavaLibrary.pConsole(chunkX+", "+chunkZ);
		int id = ore.getBlockID();
		int meta = ore.getBlockMetadata();
		int passes = ore.perChunk;
		if (ore == ReactorOres.FLUORITE) {
			meta = FluoriteTypes.getRandomColor().ordinal();
			if (ReactorOptions.RAINBOW.getState()) {
				passes /= 4F;
				meta = 0;
			}
		}
		for (int i = 0; i < passes; i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = ore.minY + random.nextInt(ore.maxY-ore.minY+1);


			if (ore.canGenAt(world, posX, posY, posZ)) {
				if ((new WorldGenMinable(id, meta, ore.veinSize, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ))
					;//ReikaJavaLibrary.pConsole(ore+" @ "+posX+", "+posY+", "+posZ, ore == ReactorOres.MAGNETITE);
			}

			if (ore == ReactorOres.FLUORITE) {
				int r = 3;
				for (int k = -r; k <= r; k++) {
					for (int l = -r; l <= r; l++) {
						for (int m = -r; m <= r; m++) {
							world.markBlockForRenderUpdate(posX, posY, posZ);
						}
					}
				}
			}
		}
	}

}
