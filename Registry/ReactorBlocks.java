/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.Instantiable.MetadataItemBlock;
import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Blocks.BlockCoriumFlowing;
import Reika.ReactorCraft.Blocks.BlockDuct;
import Reika.ReactorCraft.Blocks.BlockFluorite;
import Reika.ReactorCraft.Blocks.BlockFluoriteOre;
import Reika.ReactorCraft.Blocks.BlockPoisonGas;
import Reika.ReactorCraft.Blocks.BlockReactorMat;
import Reika.ReactorCraft.Blocks.BlockReactorOre;
import Reika.ReactorCraft.Blocks.BlockReactorTile;
import Reika.ReactorCraft.Blocks.BlockReactorTileModelled;
import Reika.ReactorCraft.Blocks.BlockSteam;
import Reika.ReactorCraft.Blocks.BlockSteamLine;
import Reika.ReactorCraft.Blocks.BlockThoriumFuel;
import Reika.ReactorCraft.Blocks.BlockTritiumLamp;
import Reika.ReactorCraft.Blocks.Multi.BlockFlywheelMulti;
import Reika.ReactorCraft.Blocks.Multi.BlockGeneratorMulti;
import Reika.ReactorCraft.Blocks.Multi.BlockHeaterMulti;
import Reika.ReactorCraft.Blocks.Multi.BlockInjectorMulti;
import Reika.ReactorCraft.Blocks.Multi.BlockSolenoidMulti;
import Reika.ReactorCraft.Blocks.Multi.BlockTurbineMulti;
import Reika.ReactorCraft.Items.ItemBlockFluorite;
import Reika.ReactorCraft.Items.ItemBlockLampMulti;
import Reika.ReactorCraft.Items.ItemBlockMultiBlock;
import Reika.ReactorCraft.Items.ItemBlockReactorMat;
import Reika.ReactorCraft.Items.ItemBlockReactorOre;

public enum ReactorBlocks implements BlockEnum {

	REACTOR(		BlockReactorTile.class, 									"Reactor", 					false),
	MATS(			BlockReactorMat.class, 			ItemBlockReactorMat.class, 	"Reactor Materials", 		false),
	CORIUMFLOWING(	BlockCoriumFlowing.class, 									"Molten Corium (Flowing)", 	false),
	MODELREACTOR(	BlockReactorTileModelled.class, 							"ReactorModelled", 			true),
	MACHINE(		BlockReactorTile.class, 									"Machine", 					false),
	MODELMACHINE(	BlockReactorTileModelled.class, 							"MachineModelled", 			true),
	ORE(			BlockReactorOre.class, 			ItemBlockReactorOre.class,	"Ore", 						false),
	FLUORITE(		BlockFluorite.class, 			ItemBlockFluorite.class,	"Fluorite",					false),
	FLUORITEORE(	BlockFluoriteOre.class, 		ItemBlockFluorite.class,	"Fluorite Ore",				false),
	STEAM(			BlockSteam.class,											"Steam",					false),
	DUCT(			BlockDuct.class,											"Duct",						false),
	LINE(			BlockSteamLine.class,										"Line",						false),
	INJECTORMULTI(	BlockInjectorMulti.class, 		ItemBlockMultiBlock.class,	"multiblock.injector",		false),
	HEATERMULTI(	BlockHeaterMulti.class, 		ItemBlockMultiBlock.class,	"multiblock.heater",		false),
	SOLENOIDMULTI(	BlockSolenoidMulti.class,		ItemBlockMultiBlock.class,	"multiblock.solenoid",		false),
	GENERATORMULTI(	BlockGeneratorMulti.class,		ItemBlockMultiBlock.class,	"multiblock.generator",		false),
	TURBINEMULTI(	BlockTurbineMulti.class,		ItemBlockMultiBlock.class,	"multiblock.turbine",		false),
	FLYWHEELMULTI(	BlockFlywheelMulti.class,		ItemBlockMultiBlock.class,	"multiblock.flywheel",		false),
	LAMP(			BlockTritiumLamp.class,			ItemBlockLampMulti.class,	"reactor.lamp",				false),
	THORIUM(		BlockThoriumFuel.class, 		MetadataItemBlock.class,	"Thorium Fuel", 			false),
	CHLORINE(		BlockPoisonGas.class,										"Chlorine Gas",				false),
	HF(				BlockPoisonGas.class,										"Fluorine Gas",				false);

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private boolean model;

	public static final ReactorBlocks[] blockList = values();

	private static final HashMap<Block, ReactorBlocks> blockMap = new HashMap();
	private static final HashMap<Item, ReactorBlocks> itemMap = new HashMap();

	private ReactorBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, boolean m) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		model = m;
	}

	private ReactorBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, n, false);
	}

	private ReactorBlocks(Class <? extends Block> cl, String n, boolean m) {
		this(cl, null, n, m);
	}

	public static ReactorBlocks getFromItem(ItemStack is) {
		return itemMap.get(is.getItem());
	}

	public static ReactorBlocks getBlock(Block b) {
		return blockMap.get(b);
	}

	public Material getBlockMaterial() {
		switch(this) {
			case MATS:
			case ORE:
			case FLUORITE:
			case FLUORITEORE:
				return Material.rock;
			case CORIUMFLOWING:
				//case CORIUMSTILL:
			case THORIUM:
			case CHLORINE:
			case HF:
				return Material.lava;
			case REACTOR:
				return Material.iron;
			case LAMP:
				return Material.glass;
			default:
				return Material.iron;
		}
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (blockClass == BlockPoisonGas.class)
			return new Class[]{Fluid.class, Material.class};
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (blockClass == BlockPoisonGas.class)
			return new Object[]{this.getFluid(), this.getBlockMaterial()};
		return new Object[]{this.getBlockMaterial()};
	}

	private Fluid getFluid() {
		if (this == HF)
			return ReactorCraft.HF;
		if (this == CHLORINE)
			return ReactorCraft.CL;
		return null;
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(blockName);
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return blockName;
	}

	@Override
	public String getMultiValuedName(int meta) {
		switch(this) {
			case MATS:
				return MatBlocks.matList[meta].getName();
			case ORE:
				return ReactorOres.oreList[meta].oreName;
			case FLUORITE:
				return FluoriteTypes.colorList[meta%8].getBlockName();
			case FLUORITEORE:
				return FluoriteTypes.colorList[meta%8].getOreName();
			case LAMP:
				return FluoriteTypes.colorList[meta].getName()+" "+StatCollector.translateToLocal(this.getBasicName());
			default:
				return this.getBasicName();
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		return true;
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
			case REACTOR:
			case MODELREACTOR:
			case MACHINE:
			case MODELMACHINE:
				return ReactorTiles.getTilesOfBlock(this).size();
			case MATS:
				return MatBlocks.matList.length;
			case ORE:
				return ReactorOres.oreList.length;
			case FLUORITE:
			case FLUORITEORE:
				return FluoriteTypes.colorList.length;
			default:
				return 1;
		}
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return itemBlock != null;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockInstance() {
		return ReactorCraft.blocks[this.ordinal()];
	}

	public boolean isModelled() {
		return model;
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

	public boolean matchItem(ItemStack is) {
		return is.getItem() == this.getItem();
	}

	public ItemStack getStackOfMetadata(int meta) {
		return new ItemStack(this.getBlockInstance(), 1, meta);
	}

	public boolean isMachine() {
		return BlockReactorTile.class.isAssignableFrom(blockClass);
	}

	public static void loadMappings() {
		for (int i = 0; i < blockList.length; i++) {
			ReactorBlocks r = blockList[i];
			Block b = r.getBlockInstance();
			blockMap.put(b, r);
			itemMap.put(Item.getItemFromBlock(b), r);
		}
	}

}
