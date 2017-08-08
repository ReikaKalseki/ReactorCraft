/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.DragonAPI.Interfaces.OreGenerator;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;

public class BasicReactorOreGenerator implements OreGenerator {

	@Override
	public void generateOre(OreEnum ore, Random random, World world, int chunkX, int chunkZ) {
		ReactorOres r = (ReactorOres)ore;
		if (random.nextInt(ReactorOptions.DISCRETE.getValue()) == 0) {
			this.generate(r, world, random, chunkX*16, chunkZ*16);
		}
	}

	private void generate(ReactorOres ore, World world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		//ReikaJavaLibrary.pConsole(chunkX+", "+chunkZ);
		Block id = ore.getBlock();
		int meta = ore.getBlockMetadata();
		int passes = ore.perChunk*ReactorOptions.DISCRETE.getValue()/this.getGenerationFactor(ore);
		if (ore == ReactorOres.FLUORITE) {
			meta = FluoriteTypes.getRandomColor().ordinal();
			if (ReactorOptions.RAINBOW.getState()) {
				passes /= 4F;
				meta = 0;
			}
		}
		for (int i = 0; i < passes; i++) {
			int posX = chunkX+random.nextInt(16);
			int posZ = chunkZ+random.nextInt(16);
			int posY = ore.minY+random.nextInt(ore.maxY-ore.minY+1);


			if (ore.canGenAt(world, posX, posY, posZ)) {
				int size = ore.veinSize*this.getVeinSizeFactor(ore);
				if ((new WorldGenMinable(id, meta, size, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ))
					;//ReikaJavaLibrary.pConsole(ore+" @ "+posX+", "+posY+", "+posZ, ore == ReactorOres.MAGNETITE);
			}

			if (ore == ReactorOres.FLUORITE) {
				int r = 3;
				for (int k = -r; k <= r; k++) {
					for (int l = -r; l <= r; l++) {
						for (int m = -r; m <= r; m++) {
							world.func_147479_m(posX, posY, posZ);
						}
					}
				}
			}
		}
	}

	protected int getVeinSizeFactor(ReactorOres ore) {
		return 1;
	}

	protected int getGenerationFactor(ReactorOres ore) {
		return 1;
	}

}
