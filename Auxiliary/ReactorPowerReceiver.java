/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;

public interface ReactorPowerReceiver extends ShaftPowerReceiver {

	public int getMinTorque();

	public int getMinSpeed();

	public long getMinPower();

}
