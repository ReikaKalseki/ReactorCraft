/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.TileEntityBase;
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

	@Override
	protected final boolean doRenderModel(TileEntityBase te) {
		return this.isValidMachineRenderPass(te);
	}

	@Override
	protected final DragonAPIMod getOwnerMod() {
		return ReactorCraft.instance;
	}

}
