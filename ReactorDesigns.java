/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import Reika.DragonAPI.Instantiable.MultiBlockBlueprint;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;

public class ReactorDesigns {

	private static final MultiBlockBlueprint MK1000 = new MultiBlockBlueprint(8, 8, 8).addBlockAt(ReactorBlocks.MATS.getBlockID(), MatBlocks.CONCRETE.ordinal(), 0, 0);

}
