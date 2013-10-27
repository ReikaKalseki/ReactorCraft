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
import Reika.ReactorCraft.Registry.ReactorItems;

public class ReactorStacks {

	public static final ItemStack hf = ReactorItems.RAW.getStackOfMetadata(0);
	public static final ItemStack fueldust = ReactorItems.RAW.getStackOfMetadata(1);
	public static final ItemStack depdust = ReactorItems.RAW.getStackOfMetadata(2);
	public static final ItemStack ammonium = ReactorItems.RAW.getStackOfMetadata(3);
	public static final ItemStack lime = ReactorItems.RAW.getStackOfMetadata(4);

	public static final ItemStack emptycan = ReactorItems.CANISTER.getStackOfMetadata(0);
	public static final ItemStack uf6can = ReactorItems.CANISTER.getStackOfMetadata(1);
	public static final ItemStack hfcan = ReactorItems.CANISTER.getStackOfMetadata(2);
	public static final ItemStack nh3can = ReactorItems.CANISTER.getStackOfMetadata(3);

}
