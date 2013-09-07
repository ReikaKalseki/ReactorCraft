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
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ShaftMachine;

public abstract class TileEntityReactorBase extends TileEntityBase {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	protected StepTimer thermalTicker = new StepTimer(20);

	protected double temperature;
	public float phi;

	@Override
	public int getTileEntityBlockID() {
		return ReactorTiles.TEList[this.getIndex()].getBlockID();
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

		NBT.setDouble("temp", temperature);

		NBT.setFloat("ang", phi);

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		temperature = NBT.getDouble("temp");

		phi = NBT.getFloat("ang");

	}

	public boolean isThisTE(int id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	public final String getName() {
		return this.getTEName();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		ReactorTiles r = ReactorTiles.TEList[this.getIndex()];
		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
	}
}
