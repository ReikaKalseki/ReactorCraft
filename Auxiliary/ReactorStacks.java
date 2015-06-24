/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import net.minecraft.item.ItemStack;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;

public class ReactorStacks {

	public static final ItemStack hf = ReactorItems.RAW.getStackOfMetadata(0);
	public static final ItemStack fueldust = ReactorItems.RAW.getStackOfMetadata(1);
	public static final ItemStack depdust = ReactorItems.RAW.getStackOfMetadata(2);
	public static final ItemStack ammonium = ReactorItems.RAW.getStackOfMetadata(3);
	public static final ItemStack lime = ReactorItems.RAW.getStackOfMetadata(4);
	public static final ItemStack calcite = ReactorItems.RAW.getStackOfMetadata(5);
	public static final ItemStack lodestone = ReactorItems.RAW.getStackOfMetadata(6);
	public static final ItemStack thordust = ReactorItems.RAW.getStackOfMetadata(7);
	public static final ItemStack emeralddust = ReactorItems.RAW.getStackOfMetadata(8);

	public static final ItemStack emptycan = ReactorItems.CANISTER.getStackOfMetadata(0);
	public static final ItemStack uf6can = ReactorItems.CANISTER.getStackOfMetadata(1);
	public static final ItemStack hfcan = ReactorItems.CANISTER.getStackOfMetadata(2);
	public static final ItemStack nh3can = ReactorItems.CANISTER.getStackOfMetadata(3);
	public static final ItemStack nacan = ReactorItems.CANISTER.getStackOfMetadata(4);
	public static final ItemStack h2can = ReactorItems.CANISTER.getStackOfMetadata(5);
	public static final ItemStack h3can = ReactorItems.CANISTER.getStackOfMetadata(6);
	public static final ItemStack clcan = ReactorItems.CANISTER.getStackOfMetadata(7);
	public static final ItemStack ocan = ReactorItems.CANISTER.getStackOfMetadata(8);
	public static final ItemStack co2can = ReactorItems.CANISTER.getStackOfMetadata(9);
	public static final ItemStack hotco2can = ReactorItems.CANISTER.getStackOfMetadata(10);
	public static final ItemStack hotnacan = ReactorItems.CANISTER.getStackOfMetadata(11);
	public static final ItemStack lican = ReactorItems.CANISTER.getStackOfMetadata(12);
	public static final ItemStack lifbecan = ReactorItems.CANISTER.getStackOfMetadata(13);
	public static final ItemStack hotlifbecan = ReactorItems.CANISTER.getStackOfMetadata(14);

	public static final ItemStack maxMagnet = ReactorItems.MAGNET.getStackOfMetadata(ReactorItems.MAGNET.getNumberMetadatas()-1);
	public static final ItemStack weakerMagnet = ReactorItems.MAGNET.getStackOfMetadata(ReactorItems.MAGNET.getNumberMetadatas()-2);

	public static final ItemStack insulCore = new ItemStack(ReactorBlocks.HEATERMULTI.getBlockInstance(), 1, 1);

}
