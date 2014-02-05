/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityGasDuct extends TileEntityReactorPiping {

	@Override
	public int getIndex() {
		return ReactorTiles.GASPIPE.ordinal();
	}

	@Override
	public Icon getBlockIcon() {
		return Block.hardenedClay.getIcon(1, 0);
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
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		return id != this.getMachine().getBlockID() || meta != this.getMachine().getBlockMetadata();
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
		return Block.hardenedClay;
	}

	@Override
	public Icon getGlassIcon() {
		return Block.glass.getIcon(0, 0);
	}

}
