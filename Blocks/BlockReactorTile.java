/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;

public class BlockReactorTile extends Block {

	private Icon[][][] icons = new Icon[16][6][16];

	public BlockReactorTile(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setHardness(2F);
		this.setResistance(10F);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		TileEntity te = ReactorTiles.createTEFromMetadata(meta);
		return te;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			if (ReactorTiles.TEList[i].hasTextureStates()) {
				for (int k = 0; k < 16; k++) {
					if (ReactorTiles.TEList[i].hasSidedTextures()) {
						for (int j = 0; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_"+j+"_#"+k);
						}
					}
					else if (ReactorTiles.TEList[i].isEndTextured()) {
						for (int j = 0; j < 2; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_top"+"_#"+k);
						}
						for (int j = 2; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_#"+k);
						}
					}
					else {
						for (int j = 0; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_#"+k);
						}
					}
				}
			}
			else {
				if (ReactorTiles.TEList[i].hasSidedTextures()) {
					for (int j = 0; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_"+j);
					}
				}
				else if (ReactorTiles.TEList[i].isEndTextured()) {
					for (int j = 0; j < 2; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase()+"_top");
					}
					for (int j = 2; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase());
					}
				}
				else {
					for (int j = 0; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+ReactorTiles.TEList[i].name().toLowerCase());
					}
				}
			}
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iba, int x, int y, int z, int s) {
		ReactorTiles r = ReactorTiles.getTE(iba, x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (r.hasTextureStates()) {
			TileEntityReactorBase te = (TileEntityReactorBase)iba.getBlockTileEntity(x, y, z);
			int k = te.getTextureState();
			return icons[meta][s][k];
		}
		else {
			return icons[meta][s][0];
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta][s][0];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		ItemStack is = ep.getCurrentEquippedItem();
		if (r == ReactorTiles.COOLANT && is != null) {
			TileEntityWaterCell te = (TileEntityWaterCell)world.getBlockTileEntity(x, y, z);
			switch(te.getLiquidState()) {
			case 0:
				if (is.itemID == Item.bucketWater.itemID) {
					te.setLiquidState(1);
					if (!ep.capabilities.isCreativeMode)
						ep.setCurrentItemOrArmor(0, new ItemStack(Item.bucketEmpty));
					return true;
				}
				if (is.itemID == ReactorItems.BUCKET.getShiftedItemID()) {
					te.setLiquidState(2);
					if (!ep.capabilities.isCreativeMode)
						ep.setCurrentItemOrArmor(0, new ItemStack(Item.bucketEmpty));
					return true;
				}
				break;
			case 1:
				if (is.itemID == Item.bucketEmpty.itemID) {
					te.setLiquidState(0);
					if (!ep.capabilities.isCreativeMode)
						ep.setCurrentItemOrArmor(0, new ItemStack(Item.bucketWater));
					return true;
				}
				break;
			case 2:
				if (is.itemID == Item.bucketEmpty.itemID) {
					te.setLiquidState(0);
					if (!ep.capabilities.isCreativeMode)
						ep.setCurrentItemOrArmor(0, ReactorItems.BUCKET.getStackOf());
					return true;
				}
				break;
			}
		}
		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition vec, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(vec.blockX, vec.blockY, vec.blockZ);
		return ReactorTiles.TEList[meta].getCraftedProduct();
	}

}
