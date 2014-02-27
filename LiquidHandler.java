/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.BCPipeHandler;
import Reika.DragonAPI.ModInteract.ThermalHandler;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class LiquidHandler {

	@ForgeSubscribe
	public void onFluidEntersPipe(FluidEvent.FluidMotionEvent evt) {
		World world = evt.world;
		int x = evt.x;
		int y = evt.y;
		int z = evt.z;
		FluidStack liq = evt.fluid;
		if (liq != null) {
			if (liq.getFluid().equals(FluidRegistry.getFluid("fusion plasma"))) {
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (!(te instanceof TileEntityReactorBase)) {
					ForgeDirection[] dirs = ForgeDirection.values();
					BlockArray blocks = new BlockArray();
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = dirs[i];
						int dx = x+dir.offsetX;
						int dy = y+dir.offsetY;
						int dz = z+dir.offsetZ;
						int id = world.getBlockId(dx, dy, dz);
						int meta = world.getBlockMetadata(dx, dy, dz);
						TileEntity te2 = world.getBlockTileEntity(dx, dy, dz);
						if (!(te2 instanceof TileEntityReactorBase)) {
							blocks.recursiveAdd(world, dx, dy, dz, id);
						}
					}
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						TileEntity te2 = world.getBlockTileEntity(xyz[0], xyz[1], xyz[2]);
						if (!(te2 instanceof TileEntityReactorBase)) {
							ReikaSoundHelper.playSoundAtBlock(world, xyz[0], xyz[1], xyz[2], "random.fizz", 0.4F, 1);
							ReikaParticleHelper.LAVA.spawnAroundBlock(world, xyz[0], xyz[1], xyz[2], 36);
							world.setBlock(xyz[0], xyz[1], xyz[2], Block.lavaMoving.blockID);
							int r = 4;
							for (int dx = xyz[0]-r; dx <= xyz[0]+r; dx++) {
								for (int dy = xyz[1]-r; dy <= xyz[1]+r; dy++) {
									for (int dz = xyz[2]-r; dz <= xyz[2]+r; dz++) {
										ReikaWorldHelper.temperatureEnvironment(world, xyz[0], xyz[1], xyz[2], TileEntityFusionHeater.PLASMA_TEMP);
									}
								}
							}
						}
					}
				}
			}
			else if (this.isCorrosive(liq.getFluid())) {
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (te instanceof IFluidHandler) {
					IFluidHandler ifl = (IFluidHandler)te;
					ForgeDirection[] dirs = ForgeDirection.values();
					BlockArray blocks = new BlockArray();
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = dirs[i];
						int dx = x+dir.offsetX;
						int dy = y+dir.offsetY;
						int dz = z+dir.offsetZ;
						int id = world.getBlockId(dx, dy, dz);
						int meta = world.getBlockMetadata(dx, dy, dz);
						TileEntity te2 = world.getBlockTileEntity(dx, dy, dz);
						if (te2 instanceof IFluidHandler) {
							blocks.recursiveAdd(world, dx, dy, dz, id);
						}
					}
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						if (this.isCorrodable(world, xyz[0], xyz[1], xyz[2])) {
							ReikaSoundHelper.playSoundAtBlock(world, xyz[0], xyz[1], xyz[2], "random.fizz", 0.4F, 1);
							ReikaParticleHelper.SMOKE.spawnAroundBlock(world, x, y, z, 6);
							world.setBlock(xyz[0], xyz[1], xyz[2], 0);
						}
						else if (this.isExplodable(world, xyz[0], xyz[1], xyz[2])) {
							world.createExplosion(null, xyz[0]+0.5, xyz[1]+0.5, xyz[2]+0.5, 2F, true);
							world.setBlock(xyz[0], xyz[1], xyz[2], 0);
						}
					}
				}
			}
		}
	}

	public boolean isCorrosive(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("uranium hexafluoride")))
			return true;
		if (f.equals(FluidRegistry.getFluid("hydrofluoric acid")))
			return true;
		return false;
	}

	public boolean isCorrodable(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		MachineRegistry m = MachineRegistry.getMachine(world, x, y, z);
		if (m == MachineRegistry.PIPE)
			return true;
		if (!(te instanceof IFluidHandler))
			return false;
		if (id == BCPipeHandler.getInstance().pipeID) {
			BCPipeHandler.Types type = BCPipeHandler.getInstance().getPipeType(te);
			if (type == BCPipeHandler.Types.GOLD)
				return true;
			if (type == BCPipeHandler.Types.IRON)
				return true;
		}
		if (id == ThermalHandler.getInstance().liquiductID) {
			return ThermalHandler.getInstance().getConduitType(te) == ThermalHandler.Types.LIQUID;
		}
		return false;
	}

	public boolean isExplodable(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof IFluidHandler))
			return false;
		if (id == BCPipeHandler.getInstance().pipeID) {
			BCPipeHandler.Types type = BCPipeHandler.getInstance().getPipeType(te);
			if (type == BCPipeHandler.Types.DIAMOND)
				return true;
			if (type == BCPipeHandler.Types.EMERALD)
				return true;
		}
		return false;
	}

}
