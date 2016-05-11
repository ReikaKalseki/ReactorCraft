/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Event;

import Reika.DragonAPI.Instantiable.Event.TileEntityEvent;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;

public class ScramEvent extends TileEntityEvent {

	public final int triggerTemperature;
	public final int rodCount;

	public ScramEvent(TileEntityCPU te, int temp) {
		super(te);

		triggerTemperature = temp;
		rodCount = te.getLayout().getNumberRods();
	}

}
