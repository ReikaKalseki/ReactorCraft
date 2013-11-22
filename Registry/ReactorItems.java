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
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.ReactorNames;
import Reika.ReactorCraft.Items.ItemCanister;
import Reika.ReactorCraft.Items.ItemHeavyBucket;
import Reika.ReactorCraft.Items.ItemNuclearWaste;
import Reika.ReactorCraft.Items.ItemPlutonium;
import Reika.ReactorCraft.Items.ItemRadiationGoggles;
import Reika.ReactorCraft.Items.ItemReactorBasic;
import Reika.ReactorCraft.Items.ItemReactorPlacer;

public enum ReactorItems implements RegistrationList, IDRegistry {

	WASTE(0,		"item.waste", 			ItemNuclearWaste.class),
	FUEL(1,			"item.fuel",			ItemReactorBasic.class),
	DEPLETED(2, 	"item.depleted",		ItemReactorBasic.class),
	PLACER(-1,		"Part Placer",			ItemReactorPlacer.class),
	BUCKET(3,		"item.heavybucket", 	ItemHeavyBucket.class),
	RAW(4,			"Raw Materials",		ItemReactorBasic.class),
	FLUORITE(16,	"Fluorite",				ItemReactorBasic.class),
	INGOTS(32,		"Ingots",				ItemReactorBasic.class),
	CANISTER(48,	"Fluid Canister",		ItemCanister.class),
	GOGGLES(64,		"item.goggles",			ItemRadiationGoggles.class),
	CRAFTING(80,	"Crafting Items", 		ItemReactorBasic.class),
	PLUTONIUM(96,	"item.plutonium",		ItemPlutonium.class),
	THORIUM(97,		"item.thorium",			ItemReactorBasic.class),
	BREEDERFUEL(98,	"item.breeder",			ItemReactorBasic.class);

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

	public String getLiquidIconName() {
		return this.name().toLowerCase();
	}

	public boolean hasMetadataSprites() {
		switch(this) {
		case FUEL:
		case WASTE:
			return false;
		default:
			return true;
		}
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
		return StatCollector.translateToLocal(name);
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
		case RAW:
			return StatCollector.translateToLocal(ReactorNames.rawNames[meta]);
		case FLUORITE:
			return FluoriteTypes.colorList[meta].getItemName();
		case INGOTS:
			return ReactorOres.oreList[meta+1].getProductName();
		case CANISTER:
			return StatCollector.translateToLocal(ReactorNames.canNames[meta]);
		case CRAFTING:
			return CraftingItems.partList[meta].itemName;
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case FUEL:
		case PLACER:
		case RAW:
		case FLUORITE:
		case INGOTS:
		case CANISTER:
		case CRAFTING:
			return true;
		default:
			return false;
		}
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		case FUEL:
			return 10;
		case PLACER:
			return ReactorTiles.TEList.length;
		case RAW:
			return ReactorNames.rawNames.length;
		case FLUORITE:
			return FluoriteTypes.colorList.length;
		case INGOTS:
			return ReactorOres.oreList.length-3;
		case CANISTER:
			return ReactorNames.canNames.length;
		case CRAFTING:
			return CraftingItems.partList.length;
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

	public boolean isAvailableInCreative(ItemStack item) {
		switch(this) {
		case INGOTS:
			return item.getItemDamage() != ReactorOres.ENDBLENDE.getProductMetadata();
		default:
			return true;
		}
	}

	public int getID() {
		return this.getItemID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

}
