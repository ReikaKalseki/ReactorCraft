/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.API.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.RotaryAux;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemReactorPlacer extends Item {

	public ItemReactorPlacer(int tex) {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
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
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
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
			world.setBlock(x, y, z, m.getBlock(), m.getBlockMetadata(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "step.stone", 1F, 1.5F);
		TileEntityReactorBase te = (TileEntityReactorBase)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		te.setBlockMetadata(RotaryAux.get4SidedMetadataFromPlayerLook(ep));
		if (m == ReactorTiles.INJECTOR) {
			((TileEntityFusionInjector)te).setFacing(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false).getOpposite());
		}
		if (m == ReactorTiles.GENERATOR) {
			((TileEntityReactorGenerator)te).setFacing(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false));
		}
		if (m.isTurbine()) {
			((TileEntityTurbineCore)te).setLubricant(is);
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
			int Tb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
			((Temperatured)te).setTemperature(Tb);
		}
		else if (te instanceof ThermalMachine) {
			int Tb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
			((ThermalMachine)te).setTemperature(Tb);
		}
		else if (te instanceof TemperatureTE) {
			int Tb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
			((TemperatureTE)te).addTemperature(Tb);
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
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
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
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
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
				Block id2 = world.getBlock(xyz[0], xyz[1], xyz[2]);
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
								world.setBlockToAir(x+i, y+j, z+k);
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
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ReactorTiles r = ReactorTiles.TEList[is.getItemDamage()];
		if (r.isPowerReceiver()) {
			ReactorPowerReceiver te = (ReactorPowerReceiver)r.createTEInstanceForRender();
			int trq = te.getMinTorque();
			int spd = te.getMinSpeed();
			long pow = te.getMinPower();
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				if (pow > 1)
					li.add(String.format("Minimum Power: %.3f %sW", ReikaMathLibrary.getThousandBase(pow), ReikaEngLibrary.getSIPrefix(pow)));
				if (trq > 1)
					li.add(String.format("Minimum Torque: %.3f %sNm", ReikaMathLibrary.getThousandBase(trq), ReikaEngLibrary.getSIPrefix(trq)));
				if (spd > 1)
					li.add(String.format("Minimum Speed: %.3f %srad/s", ReikaMathLibrary.getThousandBase(spd), ReikaEngLibrary.getSIPrefix(spd)));
			}
			else {
				if (pow > 1 || trq > 1 || spd > 1) {
					StringBuilder sb = new StringBuilder();
					sb.append("Hold ");
					sb.append(EnumChatFormatting.GREEN.toString());
					sb.append("Shift");
					sb.append(EnumChatFormatting.GRAY.toString());
					sb.append(" for power data");
					li.add(sb.toString());
				}
			}
		}
		if (r.isTurbine() && is.stackTagCompound != null) {
			int lube = is.stackTagCompound.getInteger("lube");
			li.add(String.format("Lubricant: %d mB", lube));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ReactorItems ir = ReactorItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

}
