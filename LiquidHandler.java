/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCPipeHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalDuctHandler;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.RotaryCraft.Registry.MachineRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LiquidHandler {

	@SubscribeEvent
	public void onFluidEntersPipe(FluidEvent.FluidMotionEvent evt) {
		World world = evt.world;
		int x = evt.x;
		int y = evt.y;
		int z = evt.z;
		FluidStack liq = evt.fluid;
		if (liq != null) {
			Fluid f = liq.getFluid();
			PipeReactions r = this.getReaction(f, world, x, y, z);
			if (r != null) {
				BlockArray blocks = new BlockArray();
				blocks.recursiveAddWithMetadata(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
				for (int i = 0; i < blocks.getSize(); i++) {
					Coordinate c = blocks.getNthBlock(i);
					r.doEffect(world, c);
				}
			}
		}
	}

	public boolean isCorrosive(Fluid f) {
		return f == ReactorCraft.UF6 || f == ReactorCraft.HF || f == ReactorCraft.CL;
	}

	public boolean isCorrodable(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		MachineRegistry m = MachineRegistry.getMachine(world, x, y, z);
		if (m == MachineRegistry.PIPE)
			return true;
		if (id == BCPipeHandler.getInstance().pipeID) {
			BCPipeHandler.Materials type = BCPipeHandler.getInstance().getPipeType(world.getTileEntity(x, y, z));
			if (type == BCPipeHandler.Materials.GOLD || type == BCPipeHandler.Materials.IRON)
				return true;
		}
		if (ThermalDuctHandler.getInstance().isDuct(id))
			return true;
		return false;
	}

	public boolean isExplodable(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		if (id == BCPipeHandler.getInstance().pipeID) {
			BCPipeHandler.Materials type = BCPipeHandler.getInstance().getPipeType(world.getTileEntity(x, y, z));
			if (type == BCPipeHandler.Materials.DIAMOND || type == BCPipeHandler.Materials.EMERALD)
				return true;
		}
		return false;
	}

	private PipeReactions getReaction(Fluid f, World world, int x, int y, int z) {
		if (f == null)
			return null;
		else if (f == ReactorCraft.PLASMA)
			return world.getTileEntity(x, y, z) instanceof TileEntityReactorBase ? null : PipeReactions.MELT;
		else if (this.isCorrosive(f) && this.isCorrodable(world, x, y, z))
			return PipeReactions.CORRODE;
		else if (this.isCorrosive(f) && this.isExplodable(world, x, y, z))
			return PipeReactions.EXPLODE;
		return null;
	}

	private static enum PipeReactions {
		MELT,
		CORRODE,
		EXPLODE,
		FIREEXPLODE,
		;

		private void doEffect(World world, Coordinate c) {
			switch(this) {
				case MELT:
					ReikaSoundHelper.playSoundAtBlock(world, c.xCoord, c.yCoord, c.zCoord, "random.fizz", 0.4F, 1);
					ReikaParticleHelper.LAVA.spawnAroundBlock(world, c.xCoord, c.yCoord, c.zCoord, 36);
					c.setBlock(world, Blocks.flowing_lava);
					int r = 4;
					for (int dx = c.xCoord-r; dx <= c.xCoord+r; dx++) {
						for (int dy = c.yCoord-r; dy <= c.yCoord+r; dy++) {
							for (int dz = c.zCoord-r; dz <= c.zCoord+r; dz++) {
								ReikaWorldHelper.temperatureEnvironment(world, c.xCoord, c.yCoord, c.zCoord, TileEntityFusionHeater.PLASMA_TEMP);
							}
						}
					}
					break;
				case CORRODE:
					ReikaSoundHelper.playSoundAtBlock(world, c.xCoord, c.yCoord, c.zCoord, "random.fizz", 0.4F, 1);
					ReikaParticleHelper.SMOKE.spawnAroundBlock(world, c.xCoord, c.yCoord, c.zCoord, 6);
					c.setBlock(world, Blocks.air);
					break;
				case EXPLODE:
				case FIREEXPLODE:
					c.setBlock(world, Blocks.air);
					world.newExplosion(null, c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, 2F, true, this == FIREEXPLODE);
					break;
			}
		}
	}

}
