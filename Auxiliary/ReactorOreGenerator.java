/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorOres;
import cpw.mods.fml.common.IWorldGenerator;

public class ReactorOreGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			if (world.provider.dimensionId == ore.dimensionID) {
				this.generate(ore, world, random, chunkX*16, chunkZ*16);
			}
		}
	}

	private void generate(ReactorOres ore, World world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		int id = ore.getBlockID();
		int meta = ore.getBlockMetadata();
		if (ore == ReactorOres.FLUORITE) {
			meta = FluoriteTypes.getRandomColor().ordinal();
		}
		for (int i = 0; i < ore.perChunk; i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = ore.minY + random.nextInt(ore.maxY-ore.minY);
			(new WorldGenMinable(id, meta, ore.veinSize, Block.stone.blockID)).generate(world, random, posX, posY, posZ);
			world.markBlockForRenderUpdate(posX, posY, posZ);
		}
	}

}
