/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.ReactorCraft.Registry.MatBlocks;

public class BlockReactorMat extends Block {

	private IIcon[][] icons = new IIcon[16][6];

	public BlockReactorMat(Material par2Material) {
		super(par2Material);
		this.setHardness(1.5F);
		this.setResistance(10F);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
		this.setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) == MatBlocks.SLAG.ordinal()) {
			if (ReikaRandomHelper.doWithChance(7.5)) {
				RadiationEffects.instance.contaminateArea(world, x, y, z, 4, 0.5F, 0.05, false, RadiationIntensity.HIGHLEVEL);
			}
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < MatBlocks.matList.length; i++) {
			if (MatBlocks.matList[i].isMultiSidedTexture()){
				for (int j = 0; j < 6; j++) {
					icons[i][j] = ico.registerIcon("ReactorCraft:mat/"+MatBlocks.matList[i].name().toLowerCase()+"_"+j);
				}
			}
			else {
				for (int j = 0; j < 6; j++) {
					icons[i][j] = ico.registerIcon("ReactorCraft:mat/"+MatBlocks.matList[i].name().toLowerCase());
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != this) //because MC is retarded
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		MatBlocks m = MatBlocks.matList[world.getBlockMetadata(x, y, z)];
		if (m == MatBlocks.SCRUBBER)
			return null;
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		MatBlocks m = MatBlocks.matList[world.getBlockMetadata(x, y, z)];
		if (m == MatBlocks.SCRUBBER)
			return 0;
		return super.getLightOpacity(world, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}
