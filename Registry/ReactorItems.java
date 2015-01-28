/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Interfaces.ItemEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.ReactorNames;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Base.ItemReactorMulti;
import Reika.ReactorCraft.Items.ItemCanister;
import Reika.ReactorCraft.Items.ItemGeigerCounter;
import Reika.ReactorCraft.Items.ItemHazmatSuit;
import Reika.ReactorCraft.Items.ItemHeavyBucket;
import Reika.ReactorCraft.Items.ItemIronFinder;
import Reika.ReactorCraft.Items.ItemNuclearWaste;
import Reika.ReactorCraft.Items.ItemPlutonium;
import Reika.ReactorCraft.Items.ItemRadiationCleaner;
import Reika.ReactorCraft.Items.ItemRadiationGoggles;
import Reika.ReactorCraft.Items.ItemReactorBasic;
import Reika.ReactorCraft.Items.ItemReactorBook;
import Reika.ReactorCraft.Items.ItemReactorPlacer;
import Reika.ReactorCraft.Items.ItemRemoteControl;
import Reika.RotaryCraft.Registry.ItemRegistry;

public enum ReactorItems implements ItemEnum {

	WASTE(0,		"item.waste", 			ItemNuclearWaste.class),
	FUEL(1,			"item.fuel",			ItemReactorMulti.class),
	DEPLETED(2, 	"item.depleted",		ItemReactorBasic.class),
	PLACER(-1,		"Part Placer",			ItemReactorPlacer.class),
	BUCKET(3,		"item.heavybucket", 	ItemHeavyBucket.class),
	RAW(4,			"Raw Materials",		ItemReactorMulti.class),
	FLUORITE(16,	"Fluorite",				ItemReactorMulti.class),
	INGOTS(32,		"Ingots",				ItemReactorMulti.class),
	CANISTER(48,	"Fluid Canister",		ItemCanister.class),
	GOGGLES(64,		"item.goggles",			ItemRadiationGoggles.class),
	CRAFTING(80,	"Crafting item", 		ItemReactorMulti.class),
	PLUTONIUM(96,	"item.plutonium",		ItemPlutonium.class),
	THORIUM(97,		"item.thorium",			ItemReactorMulti.class),
	BREEDERFUEL(98,	"item.breeder",			ItemReactorMulti.class),
	CLEANUP(99,		"item.cleaner",			ItemRadiationCleaner.class),
	MAGNET(100,		"item.magnet",			ItemReactorMulti.class),
	REMOTE(101,		"item.remotecpu",		ItemRemoteControl.class),
	PELLET(102,		"item.pellet",			ItemReactorMulti.class),
	OLDPELLET(103,	"item.depletedpellet",	ItemReactorBasic.class),
	BOOK(104,		"item.reactorbook",		ItemReactorBook.class),
	HAZHELMET(112,	"item.hazhelmet",		ItemHazmatSuit.class),
	HAZCHEST(113,	"item.hazchest",		ItemHazmatSuit.class),
	HAZLEGS(114,	"item.hazlegs",			ItemHazmatSuit.class),
	HAZBOOTS(115,	"item.hazboots",		ItemHazmatSuit.class),
	GEIGER(116, 	"item.geiger",			ItemGeigerCounter.class),
	IRONFINDER(117, "item.ironfinder",		ItemIronFinder.class);

	private String name;
	private Class itemClass;
	private int spriteIndex;
	private int spritesheet;

	public static final ReactorItems[] itemList = values();
	private static final HashMap<Item, ReactorItems> itemMap = new HashMap();

	private ReactorItems(int index, String n, Class<? extends Item> cl) {
		name = n;
		itemClass = cl;
		spriteIndex = index%256;
		spritesheet = index/256;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isHazmat())
			return new Class[]{int.class, int.class, int.class};
		return new Class[]{int.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isHazmat())
			return new Object[]{this.getSpriteIndex(), 0, this.ordinal()-HAZHELMET.ordinal()};
		return new Object[]{this.getSpriteIndex()};
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

	public boolean isHazmat() {
		switch(this) {
		case HAZHELMET:
		case HAZCHEST:
		case HAZLEGS:
		case HAZBOOTS:
			return true;
		default:
			return false;
		}
	}

	public boolean hasMetadataSprites() {
		switch(this) {
		case FUEL:
		case PLUTONIUM:
		case THORIUM:
		case PELLET:
		case WASTE:
		case BREEDERFUEL:
		case CLEANUP:
		case MAGNET:
		case GEIGER:
		case REMOTE:
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
		case PLUTONIUM:
		case THORIUM:
		case PELLET:
			if (meta == 0)
				return this.getBasicName()+" (Fresh)";
			else
				return this.getBasicName()+" ("+(meta*100/this.getNumberMetadatas())+"% Depleted)";
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
		case BREEDERFUEL:
			return this.getBasicName()+" ("+(meta*5)+"% Converted)";
		case CLEANUP:
			return this.getBasicName()+" ("+meta+" kJ)";
		case MAGNET:
			double num = ReikaMathLibrary.intpow2(4, meta)/1000D;
			return this.getBasicName()+String.format(" (%.3f %sT)", ReikaMathLibrary.getThousandBase(num), ReikaEngLibrary.getSIPrefix(num));
		default:
			return this.getBasicName();
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case FUEL:
		case PLUTONIUM:
		case THORIUM:
		case PELLET:
		case PLACER:
		case RAW:
		case FLUORITE:
		case INGOTS:
		case CANISTER:
		case CRAFTING:
		case BREEDERFUEL:
		case CLEANUP:
		case MAGNET:
			return true;
		default:
			return false;
		}
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		case FUEL:
		case PLUTONIUM:
			return 100;
		case THORIUM:
			return 40;
		case PELLET:
			return 25;
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
		case WASTE:
			return WasteManager.getNumberWastes();
		case BREEDERFUEL:
			return 20;
		case CLEANUP:
			return ItemRegistry.STRONGCOIL.getNumberMetadatas();
		case MAGNET:
			return 8;
		default:
			return 1;
		}
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getItemInstance(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getItemInstance(), amt, meta);
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

	public static ReactorItems getEntryByID(Item id) {
		return itemMap.get(id);
	}

	public static ReactorItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.getItem());
	}

	public boolean isAvailableInCreative(ItemStack item) {
		switch(this) {
		case INGOTS:
			return item.getItemDamage() != ReactorOres.ENDBLENDE.getProductMetadata();
		case FUEL:
		case THORIUM:
		case PLUTONIUM:
		case BREEDERFUEL:
		case PELLET:
			return item.getItemDamage() == 0;
		default:
			return true;
		}
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

	public static void loadMappings() {
		for (int i = 0; i < itemList.length; i++) {
			ReactorItems r = itemList[i];
			itemMap.put(r.getItemInstance(), r);
		}
	}

	public boolean matchWith(ItemStack is) {
		return is != null && is.getItem() == this.getItemInstance();
	}

}
