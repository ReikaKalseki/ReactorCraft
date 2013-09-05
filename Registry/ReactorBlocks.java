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
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Blocks.BlockMeltdown;
import Reika.ReactorCraft.Blocks.BlockMeltdownFlowing;
import Reika.ReactorCraft.Blocks.BlockReactorMat;
import Reika.ReactorCraft.Blocks.BlockReactorTile;
import Reika.ReactorCraft.Items.ItemBlockReactorMat;

public enum ReactorBlocks implements RegistrationList, IDRegistry {

	TILEENTITY(BlockReactorTile.class, "Tile Entity"),
	MATS(BlockReactorMat.class, ItemBlockReactorMat.class, "Reactor Materials"),
	MELTDOWNFLOWING(BlockMeltdownFlowing.class, "Molten Reactor Fuel (Flowing)"),
	MELTDOWNSTILL(BlockMeltdown.class, "Molten Reactor Fuel");

	private Class blockClass;
	private String blockName;
	private Class itemBlock;

	public static final ReactorBlocks[] blockList = values();

	private ReactorBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
	}

	private ReactorBlocks(Class <? extends Block> cl, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = null;
	}

	public int getBlockID() {
		return ReactorCraft.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		switch(this) {
		case MATS:
			return Material.rock;
		case MELTDOWNFLOWING:
		case MELTDOWNSTILL:
			return Material.lava;
		case TILEENTITY:
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
		case TILEENTITY:
			return ReactorTiles.TEList.length;
		case MATS:
			return MatBlocks.matList.length;
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

}
