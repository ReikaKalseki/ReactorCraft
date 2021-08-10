/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.BlockMultiBlock;
import Reika.ReactorCraft.ReactorCraft;

public abstract class BlockReCMultiBlock extends BlockMultiBlock<Boolean> {

	public BlockReCMultiBlock(Material par2Material) {
		super(par2Material);
		this.setResistance(10);
		this.setHardness(2);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctrMultis);
	}

	@Override
	protected final String getFullIconPath(int i) {
		return "reactorcraft:multi/"+this.getIconBaseName()+"_"+i;
	}

	@Override
	public final ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		TileEntity te = this.getTileEntityForPosition(world, x, y, z);
		return te instanceof TileEntityReactorBase ? ((TileEntityReactorBase)te).getMessages(world, x, y, z, side) : new ArrayList();
	}

	public final String getName(int meta) {
		return StatCollector.translateToLocal("multiblock."+this.getIconBaseName().toLowerCase(Locale.ENGLISH)+"."+(meta&7));
	}

	protected abstract String getIconBaseName();

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

}
