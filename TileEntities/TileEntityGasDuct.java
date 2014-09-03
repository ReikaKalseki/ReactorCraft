/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityGasDuct extends TileEntityReactorPiping {

	@Override
	public int getIndex() {
		return ReactorTiles.GASPIPE.ordinal();
	}

	@Override
	public IIcon getBlockIcon() {
		return Blocks.hardened_clay.getIcon(1, 0);
	}

	public boolean isConnectedToNonSelf(ForgeDirection dir) {
		if (!this.isConnectionValidForSide(dir))
			return false;
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		int dx = xCoord+dir.offsetX;
		int dy = yCoord+dir.offsetY;
		int dz = zCoord+dir.offsetZ;
		World world = worldObj;
		Block id = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		return id != this.getMachine().getBlock() || meta != this.getMachine().getBlockMetadata();
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return f.isGaseous();
	}

	@Override
	protected void onIntake(TileEntity te) {

	}

	@Override
	public Block getPipeBlockType() {
		return Blocks.hardened_clay;
	}

	@Override
	public IIcon getGlassIcon() {
		return Blocks.glass.getIcon(0, 0);
	}

}
