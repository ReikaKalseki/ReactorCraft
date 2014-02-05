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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.RotaryCraft;

public class TileEntityMagneticPipe extends TileEntityReactorPiping {

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNETPIPE.ordinal();
	}

	@Override
	public Icon getBlockIcon() {
		return Block.blockGold.getIcon(0, 0);
	}

	@Override
	public Block getPipeBlockType() {
		return Block.blockGold;
	}

	@Override
	public boolean isConnectedToNonSelf(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return f != null && f.equals(FluidRegistry.getFluid("fusion plasma"));
	}

	@Override
	protected void onIntake(TileEntity te) {

	}

	@Override
	public Icon getGlassIcon() {
		return RotaryCraft.blastglass.getIcon(0, 0);
	}

}
