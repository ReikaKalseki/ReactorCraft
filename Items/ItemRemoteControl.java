/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.LinkableReactorCore;
import Reika.ReactorCraft.Base.ReactorItemBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.RotaryCraft.API.Interfaces.ChargeableTool;

public class ItemRemoteControl extends ReactorItemBase implements ChargeableTool {

	public ItemRemoteControl(int tex) {
		super(tex);
		maxStackSize = 1;
		canRepair = false;
		hasSubtypes = false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (this.canUse(is, world, ep)) {
			TileEntity te = this.getLinkedCPU(is);
			if (te instanceof TileEntityCPU) {
				int x = is.stackTagCompound.getInteger("cx");
				int y = is.stackTagCompound.getInteger("cy");
				int z = is.stackTagCompound.getInteger("cz");
				int dim = is.stackTagCompound.getInteger("id");
				World w = DimensionManager.getWorld(dim);
				Block id = w.getBlock(x, y, z);
				int meta = w.getBlockMetadata(x, y, z);
				if (id == ReactorTiles.CPU.getBlock() && meta == ReactorTiles.CPU.getBlockMetadata()) {
					is.setItemDamage(is.getItemDamage()-1);
					ep.openGui(ReactorCraft.instance, 0, w, x, y, z);
				}
			}
		}
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		TileEntity te = world.getTileEntity(x, y, z);
		if (r == ReactorTiles.CPU) {
			if (te != null) {
				this.setLinkedCPU(is, te);
				ReikaChatHelper.sendChatToPlayer(ep, "Linking to "+ReactorTiles.CPU.getName()+" at "+new Coordinate(te));
				return true;
			}
		}
		else if (r != null && r.isLinkableReactorCore() && this.getLinkedCPU(is) != null) {
			((TileEntityCPU)this.getLinkedCPU(is)).addTemperatureCheck((LinkableReactorCore)te);
			ReikaChatHelper.sendChatToPlayer(ep, "Linking "+r.getName()+" to "+ReactorTiles.CPU.getName()+" at "+new Coordinate(this.getLinkedCPU(is)));
		}
		return false;
	}

	public TileEntity getLinkedCPU(ItemStack is) {
		if (is.getItem() == this) {
			if (is.stackTagCompound != null) {
				int x = is.stackTagCompound.getInteger("cx");
				int y = is.stackTagCompound.getInteger("cy");
				int z = is.stackTagCompound.getInteger("cz");
				int dim = is.stackTagCompound.getInteger("id");
				TileEntity te = DimensionManager.getWorld(dim).getTileEntity(x, y, z);
				return te;
			}
		}
		return null;
	}

	public boolean canUse(ItemStack is, World world, EntityPlayer ep) {
		if (is.getItemDamage() > 0 && is.stackTagCompound != null) {
			int x = is.stackTagCompound.getInteger("cx");
			int y = is.stackTagCompound.getInteger("cy");
			int z = is.stackTagCompound.getInteger("cz");
			int dim = is.stackTagCompound.getInteger("id");
			if (dim == world.provider.dimensionId || this.canWorkInterdimensionally(is)) {
				int ex = MathHelper.floor_double(ep.posX);
				int ey = MathHelper.floor_double(ep.posY);
				int ez = MathHelper.floor_double(ep.posZ);
				double dd = ReikaMathLibrary.py3d(ex-x, ey-y, ez-z);
				return DimensionManager.getWorld(dim) != null && this.getRange(is)+0.5 >= dd;
			}
		}
		return false;
	}

	private void setLinkedCPU(ItemStack is, TileEntity te) {
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("cx", te.xCoord);
		is.stackTagCompound.setInteger("cy", te.yCoord);
		is.stackTagCompound.setInteger("cz", te.zCoord);
		is.stackTagCompound.setInteger("id", te.worldObj.provider.dimensionId);
	}

	public boolean canWorkInterdimensionally(ItemStack is) {
		return is.getItemDamage() > 8192;
	}

	public int getRange(ItemStack is) {
		return 4*(int)ReikaMathLibrary.logbase(is.getItemDamage(), 2);
	}

	@Override
	public void getSubItems(Item id, CreativeTabs tab, List li) {
		li.add(ReactorItems.REMOTE.getStackOfMetadata(0));
		li.add(ReactorItems.REMOTE.getStackOfMetadata(32000));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		if (is.stackTagCompound != null) {
			int x = is.stackTagCompound.getInteger("cx");
			int y = is.stackTagCompound.getInteger("cy");
			int z = is.stackTagCompound.getInteger("cz");
			int dim = is.stackTagCompound.getInteger("id");
			li.add(String.format("Linked to CPU in world %d at %d, %d, %d", dim, x, y, z));
		}
		else {
			li.add("No linked CPU");
		}
		li.add("Charge: "+is.getItemDamage()+" kJ");
	}

	@Override
	public int setCharged(ItemStack is, int charge, boolean strongcoil) {
		int ret = is.getItemDamage();
		is.setItemDamage(charge);
		return ret;
	}
}
