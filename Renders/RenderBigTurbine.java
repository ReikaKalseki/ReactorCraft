/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Renders;

import Reika.ReactorCraft.Models.ModelBigTurbine;
import Reika.ReactorCraft.Models.ModelTurbine;

public class RenderBigTurbine extends RenderTurbine
{

	@Override
	protected Class<? extends ModelTurbine> getModelClass() {
		return ModelBigTurbine.class;
	}

	@Override
	protected String getTextureName() {
		return "bigturbine";
	}
}
