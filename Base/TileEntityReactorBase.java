/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;

public abstract class TileEntityReactorBase extends TileEntityBase {

	protected int temperature;

	@Override
	public int getTileEntityBlockID() {
		return ReactorBlocks.TILEENTITY.getBlockID();
	}

	@Override
	protected String getTEName() {
		return ReactorTiles.TEList[this.getIndex()].getName();
	}

	public abstract int getIndex();

	public int getTextureState() {
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		temperature = NBT.getInteger("temp");

	}
}
