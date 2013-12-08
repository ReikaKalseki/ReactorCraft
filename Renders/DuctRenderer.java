/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.ReactorCraft.Base.ReactorRenderBase;
import Reika.ReactorCraft.TileEntities.TileEntityGasDuct;
import Reika.RotaryCraft.Auxiliary.RotaryRenderList;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class DuctRenderer extends ReactorRenderBase
{
	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		TileEntityGasDuct te = (TileEntityGasDuct)tile;
		RotaryRenderList.getRenderForMachine(MachineRegistry.PIPE).renderTileEntityAt(tile, par2, par4, par6, par8);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}
}
