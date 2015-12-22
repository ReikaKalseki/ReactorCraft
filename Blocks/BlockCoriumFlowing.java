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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCoriumFlowing extends BlockFluidClassic {

	int numAdjacentSources = 0;
	boolean isOptimalFlowDirection[] = new boolean[4];
	int flowCost[] = new int[4];

	private IIcon[] icon;

	public BlockCoriumFlowing(Material material) {
		super(FluidRegistry.getFluid("rc corium"), material);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);
	}
	/*
	@Override
	public int getRenderType() {
		return 4;
	}*/

	@Override
	protected void flowIntoBlock(World world, int i, int j, int k, int l) {
		super.flowIntoBlock(world, i, j, k, l);
		if (this.liquidCanDisplaceBlock(world, i, j, k)) {
			Block blockId = world.getBlock(i, j, k);
			if (blockId != Blocks.air) {
				blockId.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
			}
			world.setBlock(i, j, k, this, l, 3);
			if (ReikaRandomHelper.doWithChance(0.02))
				RadiationEffects.instance.contaminateArea(world, i, j+ReikaRandomHelper.getSafeRandomInt(3), k, 8, 1, 0, false, RadiationIntensity.LETHAL);
			if (ReikaRandomHelper.doWithChance(0.1))
				RadiationEffects.instance.contaminateArea(world, i, j+ReikaRandomHelper.getSafeRandomInt(3), k, 1, 1, 0, false, RadiationIntensity.LETHAL);
		}

		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, i, j, k, Blocks.ice);
		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, i, j, k, Material.water);
		if (iceside != null || waterside != null) {
			if (ReikaRandomHelper.doWithChance(15))
				;//world.setBlock(i, j, k, ReactorBlocks.MATS.getBlock(), MatBlocks.SLAG.ordinal(), 3);
			if (iceside != null) {
				ReikaWorldHelper.changeAdjBlock(world, i, j, k, iceside, Blocks.flowing_water, 0);
			}
			if (waterside != null) {
				ReikaWorldHelper.changeAdjBlock(world, i, j, k, waterside, Blocks.air, 0);
				ReikaSoundHelper.playSoundAtBlock(world, i, j, k, "random.fizz");
				ReikaParticleHelper.SMOKE.spawnAroundBlock(world, i, j, k, 8);
			}
		}
	}

	private boolean blockBlocksFlow(World world, int x, int y, int z) {
		Block l = world.getBlock(x, y, z);

		if (l != Blocks.wooden_door && l != Blocks.iron_door && l != Blocks.standing_sign && l != Blocks.ladder && l != Blocks.reeds) {
			if (l == Blocks.air) {
				return false;
			}
			else {
				Material material = l.getMaterial();
				return material == Material.portal ? true : material.blocksMovement();
			}
		}
		else {
			return true;
		}
	}

	private boolean liquidCanDisplaceBlock(World world, int i, int j, int k) {
		Material material = ReikaWorldHelper.getMaterial(world, i, j, k);
		if (material == blockMaterial) {
			return false;
		}
		else {
			return !this.blockBlocksFlow(world, i, j, k);
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		icon = new IIcon[]{iconRegister.registerIcon("ReactorCraft:mat_slag"), iconRegister.registerIcon("ReactorCraft:slag_flow")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta)
	{
		return s != 0 && s != 1 ? icon[1] : icon[0];
	}

	private void checkForHarden(World world, int x, int y, int z)
	{
		if (world.getBlock(x, y, z) == this) {
			boolean flag = false;

			if (flag || ReikaWorldHelper.getMaterial(world, x, y, z - 1) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x, y, z + 1) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x - 1, y, z) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x + 1, y, z) == Material.water)
				flag = true;
			if (flag || ReikaWorldHelper.getMaterial(world, x, y + 1, z) == Material.water)
				flag = true;

			if (flag) {
				;//world.setBlock(x, y, z, ReactorBlocks.MATS.getBlock(), MatBlocks.SLAG.ordinal(), 3);
				this.onNeighborBlockChange(world, x, y, z, this);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4)
	{
		this.checkForHarden(world, par2, par3, par4);
		world.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(world));
	}

}
