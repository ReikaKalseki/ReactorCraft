/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.Shockable;

public class TileEntityIonizer extends TileEntityReactorBase implements Shockable {

	public static final int PLASMACHARGE = 600000;

	private int charge;

	private ForgeDirection facing;

	@Override
	public int getIndex() {
		return ReactorTiles.IONIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("chg", charge);

		NBT.setInteger("face", this.getFacing().ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		charge = NBT.getInteger("chg");

		facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += charge;
	}

	@Override
	public int getMinDischarge() {
		return 16384;
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

}
