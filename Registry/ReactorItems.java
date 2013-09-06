/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.WasteManager;
import Reika.ReactorCraft.Items.ItemDepleted;
import Reika.ReactorCraft.Items.ItemFuelPellet;
import Reika.ReactorCraft.Items.ItemHeavyBucket;
import Reika.ReactorCraft.Items.ItemLiquid;
import Reika.ReactorCraft.Items.ItemNuclearWaste;
import Reika.ReactorCraft.Items.ItemReactorPlacer;

public enum ReactorItems implements RegistrationList, IDRegistry {

	WASTE(0,	"Nuclear Waste", 		ItemNuclearWaste.class),
	FUEL(1,		"Uranium Pellet",		ItemFuelPellet.class),
	DEPLETED(2, "Depleted Uranium",		ItemDepleted.class),
	PLACER(-1,	"Part Placer",			ItemReactorPlacer.class),
	WATER(-1,	"Heavy Water",			ItemLiquid.class),
	BUCKET(3,	"Heavy Water Bucket", 	ItemHeavyBucket.class);

	private String name;
	private Class itemClass;
	private int spriteIndex;
	private int spritesheet;

	public static final ReactorItems[] itemList = ReactorItems.values();

	private ReactorItems(int index, String n, Class<? extends Item> cl) {
		name = n;
		itemClass = cl;
		spriteIndex = index%256;
		spritesheet = index/256;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, int.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getItemID(), this.getSpriteIndex()};
	}

	public int getSpriteIndex() {
		return spriteIndex;
	}

	public int getSpriteSheet() {
		return spritesheet;
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name);
	}

	@Override
	public Class getObjectClass() {
		return itemClass;
	}

	@Override
	public String getBasicName() {
		return name;
	}

	@Override
	public String getMultiValuedName(int meta) {
		switch(this) {
		case FUEL:
			if (meta == 0)
				return this.getBasicName()+" (Fresh)";
			else
				return this.getBasicName()+" ("+(meta*10)+"% Depleted)";
		case PLACER:
			return ReactorTiles.TEList[meta].getName();
		case WASTE:
			return this.getBasicName();
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case FUEL:
		case PLACER:
		case WASTE:
			return true;
		default:
			return false;
		}
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		case WASTE:
			return WasteManager.getNumberWastes();
		case FUEL:
			return 10;
		case PLACER:
			return ReactorTiles.TEList.length;
		default:
			return 1;
		}
	}

	public int getItemID() {
		return ReactorCraft.config.getItemID(this.ordinal());
	}

	public int getShiftedItemID() {
		return ReactorCraft.config.getItemID(this.ordinal())+256;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 18000+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String getCategory() {
		return "Item IDs";
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getShiftedItemID(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getShiftedItemID(), amt, meta);
	}

	public ItemStack getStackOf() {
		return this.getCraftedProduct(1);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return this.getCraftedMetadataProduct(1, meta);
	}

	public Item getItemInstance() {
		return ReactorCraft.items[this.ordinal()];
	}

	public static ReactorItems getEntryByID(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedItemID() == id)
				return itemList[i];
		}
		throw new RegistrationException(ReactorCraft.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
	}

	public static ReactorItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.itemID);
	}

}
