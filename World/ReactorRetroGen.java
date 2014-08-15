/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.World;

import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.ReactorCraft.Registry.ReactorOres;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class ReactorRetroGen implements RetroactiveGenerator {

	@Override
	public void generate(Random rand, World world, int chunkX, int chunkZ) {
		this.generate(rand, chunkX, chunkZ, world, null, null);
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ReactorCraftOres";
	}

	private void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		for (int i = 0; i < ReactorOres.oreList.length; i++) {
			ReactorOres ore = ReactorOres.oreList[i];
			if (ore.canGenerateInChunk(world, chunkX, chunkZ)) {
				//ReactorOreGenerator.generate(ore, world, random, chunkX, chunkZ);
			}
		}
	}

}