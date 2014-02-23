/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.API.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.RotaryAux;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemReactorPlacer extends Item {

	public ItemReactorPlacer(int ID, int tex) {
		super(ID);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava)
				return false;
		}
		if (!this.checkValidBounds(is, ep, world, x, y, z))
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		ReactorTiles m = ReactorTiles.TEList[is.getItemDamage()];
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, m.getBlockID(), m.getBlockMetadata(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "step.stone", 1F, 1.5F);
		TileEntityReactorBase te = (TileEntityReactorBase)world.getBlockTileEntity(x, y, z);
		te.placer = ep.getEntityName();
		te.setBlockMetadata(RotaryAux.get4SidedMetadataFromPlayerLook(ep));
		if (m == ReactorTiles.INJECTOR) {
			((TileEntityFusionInjector)te).setFacing(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false).getOpposite());
		}
		if (m == ReactorTiles.COLLECTOR) {
			switch(side) {
			case 0:
				te.setBlockMetadata(1);
				break;
			case 1:
				te.setBlockMetadata(0);
				break;
			case 2:
				te.setBlockMetadata(4);
				break;
			case 3:
				te.setBlockMetadata(2);
				break;
			case 4:
				te.setBlockMetadata(5);
				break;
			case 5:
				te.setBlockMetadata(3);
				break;
			}
			return true;
		}
		if (te instanceof Temperatured) {
			int Tb = ReikaBiomeHelper.getBiomeTemp(world, x, z);
			((Temperatured)te).setTemperature(Tb);
		}
		else if (te instanceof ThermalMachine) {
			int Tb = ReikaBiomeHelper.getBiomeTemp(world, x, z);
			((ThermalMachine)te).setTemperature(Tb);
		}
		if (te instanceof ShaftMachine) {
			ShaftMachine sm = (ShaftMachine)te;
			sm.setIORenderAlpha(512);
		}/*
		if (te instanceof Feedable && !world.isRemote) {
			((Feedable) te).getOrCreateNetwork(world, x, y, z);
		}*/

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			if (ReactorTiles.TEList[i].isAvailableInCreativeInventory()) {
				ItemStack item = new ItemStack(par1, 1, i);
				par3List.add(item);
			}
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		if (ReactorTiles.TEList[is.getItemDamage()] == ReactorTiles.TURBINECORE) {
			int meta = RotaryAux.get4SidedMetadataFromPlayerLook(ep);
			BlockArray contact = new BlockArray();
			AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1);
			int r = 3;
			switch(meta) {
			case 2:
			case 3:
				for (int i = x-r; i <= x+r; i++) {
					for (int j = y-r; j <= y+r; j++) {
						if (x != i || y != j)
							contact.addBlockCoordinate(i, j, z);
					}
				}
				break;
			case 0:
			case 1:
				for (int i = z-r; i <= z+r; i++) {
					for (int j = y-r; j <= y+r; j++) {
						if (z != i || y != j)
							contact.addBlockCoordinate(x, j, i);
					}
				}
				break;
			}
			for (int i = 0; i < contact.getSize(); i++) {
				int[] xyz = contact.getNthBlock(i);
				int id2 = world.getBlockId(xyz[0], xyz[1], xyz[2]);
				int meta2 = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (!ReikaWorldHelper.softBlocks(world, xyz[0], xyz[1], xyz[2]) && !(xyz[0] == x && xyz[1] == y && xyz[2] == z)) {
					return false;
				}
			}
		}
		if (ReactorTiles.TEList[is.getItemDamage()] == ReactorTiles.MAGNET) {
			int r = 1;
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						if (!(i == 0 && Math.abs(j) <= 1 && k == 0)) {
							if (!ReikaWorldHelper.softBlocks(world, x+i, y+j, z+k)) {
								return false;
							}
							else {
								ReikaWorldHelper.dropBlockAt(world, x+i, y+j, z+k);
								world.setBlock(x+i, y+j, z+k, 0);
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IconRegister ico) {}

}
