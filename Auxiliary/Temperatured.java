/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

public interface Temperatured {

	public abstract double getTemperature();

	public void setTemperature(int T);

	public int getMaxTemperature();

}
