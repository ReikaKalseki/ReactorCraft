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
import Reika.RotaryCraft.RenderableDuct;
import Reika.RotaryCraft.Renders.PipeRenderer;

public class DuctRenderer extends ReactorRenderBase
{
	private static final PipeRenderer pipe = new PipeRenderer();

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		RenderableDuct te = (RenderableDuct)tile;
		//RotaryRenderList.getRenderForMachine(MachineRegistry.PIPE).renderTileEntityAt(tile, par2, par4, par6, par8);
		pipe.renderTileEntityAt(tile, par2, par4, par6, par8);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}
}
