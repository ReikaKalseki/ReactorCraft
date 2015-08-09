/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class VolcanicGasController implements TickHandler {

	private static final Random rand = new Random();

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world != null && world.provider.dimensionId == -1 || world.provider.isHellWorld) {
			int y = 32;
			if (world.playerEntities.size() > 0) {
				EntityPlayer ep = (EntityPlayer)world.playerEntities.get(rand.nextInt(world.playerEntities.size()));
				int x = MathHelper.floor_double(ep.posX);
				int z = MathHelper.floor_double(ep.posZ);
				x = ReikaRandomHelper.getRandomPlusMinus(x, 64);
				z = ReikaRandomHelper.getRandomPlusMinus(z, 64);
				Block id = world.getBlock(x, y-1, z);
				if (id == Blocks.flowing_lava || id == Blocks.lava) {
					//world.setBlock(x, y, z, ReactorBlocks.GAS.getBlock(), 1, 3);
					//world.markBlockForUpdate(x, y, z);
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Volcanic Gas";
	}

}
