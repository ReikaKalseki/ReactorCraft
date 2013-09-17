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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Feedable {

	public boolean feed();

	public boolean feedIn(ItemStack is);

	public ItemStack feedOut();

	public boolean hasNetworkAdjacent(World world, int x, int y, int z);

	public FuelNetwork getOrCreateNetwork(World world, int x, int y, int z);

	public FuelNetwork getNetwork();

	public void setNetwork(FuelNetwork fuel);

}
