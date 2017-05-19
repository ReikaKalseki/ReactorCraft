/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import Reika.ReactorCraft.Models.ModelMiniTurbine;
import Reika.ReactorCraft.Models.ModelTurbine;


public class RenderMiniTurbine extends RenderTurbine {


	@Override
	protected Class<? extends ModelTurbine> getModelClass() {
		return ModelMiniTurbine.class;
	}

	@Override
	protected String getTextureName() {
		return "miniturbine3";
	}
}
