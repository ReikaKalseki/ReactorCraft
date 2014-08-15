/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import Reika.DragonAPI.Base.TileEntityRenderBase;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.ReactorCraft.ReactorCraft;

public abstract class ReactorRenderBase extends TileEntityRenderBase implements TextureFetcher {

	@Override
	public final String getTextureFolder() {
		return "/Reika/ReactorCraft/Textures/TileEntity/";
	}

	@Override
	protected Class getModClass() {
		return ReactorCraft.class;
	}

}