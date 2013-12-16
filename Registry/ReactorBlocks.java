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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Blocks.BlockCorium;
import Reika.ReactorCraft.Blocks.BlockCoriumFlowing;
import Reika.ReactorCraft.Blocks.BlockDuct;
import Reika.ReactorCraft.Blocks.BlockFluorite;
import Reika.ReactorCraft.Blocks.BlockFluoriteOre;
import Reika.ReactorCraft.Blocks.BlockReactorMat;
import Reika.ReactorCraft.Blocks.BlockReactorOre;
import Reika.ReactorCraft.Blocks.BlockReactorTile;
import Reika.ReactorCraft.Blocks.BlockReactorTileModelled;
import Reika.ReactorCraft.Blocks.BlockSteam;
import Reika.ReactorCraft.Blocks.BlockSteamLine;
import Reika.ReactorCraft.Items.ItemBlockFluorite;
import Reika.ReactorCraft.Items.ItemBlockReactorMat;
import Reika.ReactorCraft.Items.ItemBlockReactorOre;

public enum ReactorBlocks implements RegistryEnum {

	REACTOR(		BlockReactorTile.class, 							"Reactor", 					false),
	MATS(			BlockReactorMat.class, ItemBlockReactorMat.class, 	"Reactor Materials", 		false),
	CORIUMFLOWING(	BlockCoriumFlowing.class, 							"Molten Corium (Flowing)", 	false),
	CORIUMSTILL(	BlockCorium.class, 									"Molten Corium", 			false),
	MODELREACTOR(	BlockReactorTileModelled.class, 					"ReactorModelled", 			true),
	MACHINE(		BlockReactorTile.class, 							"Machine", 					false),
	MODELMACHINE(	BlockReactorTileModelled.class, 					"MachineModelled", 			true),
	ORE(			BlockReactorOre.class, ItemBlockReactorOre.class,	"Ore", 						false),
	FLUORITE(		BlockFluorite.class, ItemBlockFluorite.class,		"Fluorite",					false),
	FLUORITEORE(	BlockFluoriteOre.class, ItemBlockFluorite.class,	"Fluorite Ore",				false),
	STEAM(			BlockSteam.class,									"Steam",					false),
	DUCT(			BlockDuct.class,									"Duct",						false),
	LINE(			BlockSteamLine.class,								"Line",						false);

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private boolean model;

	public static final ReactorBlocks[] blockList = values();

	private ReactorBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, boolean m) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		model = m;
	}

	private ReactorBlocks(Class <? extends Block> cl, String n, boolean m) {
		this(cl, null, n, m);
	}

	public int getBlockID() {
		return ReactorCraft.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		switch(this) {
		case MATS:
		case ORE:
		case FLUORITE:
		case FLUORITEORE:
			return Material.rock;
		case CORIUMFLOWING:
		case CORIUMSTILL:
			return Material.lava;
		case REACTOR:
			return Material.iron;
		default:
			return Material.iron;
		}
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockID(), this.getBlockMaterial()};
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
			return FluoriteTypes.colorList[meta].getBlockName();
		case FLUORITEORE:
			return FluoriteTypes.colorList[meta].getOreName();
		default:
			return "";
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

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 1600+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public String getCategory() {
		return "Reactor Blocks";
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockVariable() {
		return ReactorCraft.blocks[this.ordinal()];
	}

	public boolean isModelled() {
		return model;
	}

	public int getID() {
		return this.getBlockID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

}
