/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.ReactorCraft;

public abstract class BlockMultiBlock extends Block {

	private final Icon[] icons = new Icon[16];
	protected static final ForgeDirection[] dirs = ForgeDirection.values();

	public BlockMultiBlock(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	public abstract boolean checkForFullMultiBlock(World world, int x, int y, int z);

	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, int idn) {

	}

	protected abstract void breakMultiBlock(World world, int x, int y, int z);

	@Override
	public final void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote && this.canTriggerMultiBlockCheck(world, x, y, z, world.getBlockMetadata(x, y, z))) {
			if (this.checkForFullMultiBlock(world, x, y, z))
				this.onCreateFullMultiBlock(world, x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int oldid, int oldmeta) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (!world.isRemote && !this.checkForFullMultiBlock(world, dx, dy, dz)) {
				this.breakMultiBlock(world, dx, dy, dz);
			}
		}

		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	protected abstract void onCreateFullMultiBlock(World world, int x, int y, int z);

	public abstract int getNumberVariants();

	@Override
	public final Icon getIcon(int s, int meta) {
		return icons[this.getItemTextureIndex(meta)];
	}

	protected abstract String getIconBaseName();

	@Override
	public final void registerIcons(IconRegister ico) {
		for (int i = 0; i < 16; i++) {
			String name = this.getIconBaseName()+"_"+i;
			icons[i] = ico.registerIcon("reactorcraft:multi/"+name);
		}
	}

	@Override
	public final Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int index = this.getTextureIndex(world, x, y, z, side, world.getBlockMetadata(x, y, z));
		index = Math.min(16, index); //safety net
		return icons[index];
	}

	public abstract int getTextureIndex(IBlockAccess world, int x, int y, int z, int side, int meta);

	@Override
	public final int damageDropped(int meta) {
		return 0;
	}

	public final String getName(int meta) {
		return StatCollector.translateToLocal("multiblock."+this.getIconBaseName().toLowerCase()+"."+(meta&7));
	}

	public abstract int getItemTextureIndex(int meta);

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition mov, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z)&7;
		return new ItemStack(blockID, 1, meta);
	}

	public abstract boolean canTriggerMultiBlockCheck(World world, int x, int y, int z, int meta);

}
