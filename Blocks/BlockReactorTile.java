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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.TileEntityUProcessor;
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
		TileEntity te = ReactorTiles.createTEFromIDAndMetadata(blockID, meta);
		return te;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			if (r.hasTextureStates()) {
				for (int k = 0; k < r.getTextureStates(); k++) {
					if (r.hasSidedTextures()) {
						for (int j = 0; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_"+j+"_#"+k);
						}
					}
					else if (r.isEndTextured()) {
						for (int j = 0; j < 2; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_top"+"_#"+k);
						}
						for (int j = 2; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_#"+k);
						}
					}
					else {
						for (int j = 0; j < 6; j++) {
							icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_#"+k);
						}
					}
				}
			}
			else {
				if (r.hasSidedTextures()) {
					for (int j = 0; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_"+j);
					}
				}
				else if (r.isEndTextured()) {
					for (int j = 0; j < 2; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase()+"_top");
					}
					for (int j = 2; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase());
					}
				}
				else {
					for (int j = 0; j < 6; j++) {
						icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase());
					}
				}
			}
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iba, int x, int y, int z, int s) {
		ReactorTiles r = ReactorTiles.getTE(iba, x, y, z);
		if (r == null)
			return null;
		if (r.hasTextureStates()) {
			TileEntityReactorBase te = (TileEntityReactorBase)iba.getBlockTileEntity(x, y, z);
			int k = te.getTextureState();
			return icons[r.ordinal()][s][k];
		}
		else {
			return icons[r.ordinal()][s][0];
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
		if (ep.isSneaking())
			return false;
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
		if (r == ReactorTiles.HEAVYPUMP && is != null && is.itemID == Item.bucketEmpty.itemID) {
			TileEntityHeavyPump te = (TileEntityHeavyPump)world.getBlockTileEntity(x, y, z);
			if (te.hasABucket()) {
				te.subtractBucket();
				ep.setCurrentItemOrArmor(0, ReactorItems.BUCKET.getStackOf());
				return true;
			}
		}
		if (r == ReactorTiles.PROCESSOR && is != null && is.itemID == ReactorItems.CANISTER.getShiftedItemID()) {
			TileEntityUProcessor te = (TileEntityUProcessor)world.getBlockTileEntity(x, y, z);
			if (is.getItemDamage() == ReactorStacks.emptycan.getItemDamage() && te.getUF6() >= FluidContainerRegistry.BUCKET_VOLUME) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.uf6can.copy());
				te.drain(null, FluidContainerRegistry.BUCKET_VOLUME, true);
			}
			else if (is.getItemDamage() == ReactorStacks.hfcan.getItemDamage() && te.canAcceptMoreHF(FluidContainerRegistry.BUCKET_VOLUME)) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan.copy());
				te.addHF(FluidContainerRegistry.BUCKET_VOLUME);
			}
			return true;
		}
		if (r == ReactorTiles.CENTRIFUGE && is != null && is.itemID == ReactorItems.CANISTER.getShiftedItemID()) {
			TileEntityCentrifuge te = (TileEntityCentrifuge)world.getBlockTileEntity(x, y, z);
			if (is.getItemDamage() == ReactorStacks.emptycan.getItemDamage() && te.getUF6() >= FluidContainerRegistry.BUCKET_VOLUME) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.uf6can.copy());
				te.removeFluid(FluidContainerRegistry.BUCKET_VOLUME);
			}
			else if (is.getItemDamage() == ReactorStacks.uf6can.getItemDamage() && te.canAcceptMoreUF6(FluidContainerRegistry.BUCKET_VOLUME)) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan.copy());
				te.addUF6(FluidContainerRegistry.BUCKET_VOLUME);
			}
			return true;
		}

		if (ReactorCraft.hasGui(world, x, y, z, ep)) {
			ep.openGui(ReactorCraft.instance, 0, world, x, y, z);
			return true;
		}

		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition vec, World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, vec.blockX, vec.blockY, vec.blockZ);
		return r != null ? r.getCraftedProduct() : null;
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory)
			ReikaItemHelper.dropInventory(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return 255;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		li.add(ReactorTiles.getTE(world, x, y, z).getCraftedProduct());
		return li;
	}

}
