/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.ReactorCraft.Entities.EntityRadiation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCoriumFlowing extends BlockFluidClassic {

	private IIcon[] icon;

	public BlockCoriumFlowing(Material material) {
		super(ReactorCraft.CORIUM, material);

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
	protected void flowIntoBlock(World world, int x, int y, int z, int l) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		super.flowIntoBlock(world, x, y, z, l);
		//if (this.liquidCanDisplaceBlock(world, i, j, k)) {
		Block blockId = world.getBlock(x, y, z);
		if (blockId != Blocks.air) {
			blockId.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		}
		world.setBlock(x, y, z, this, l, 3);
		if (world.getBlock(x, y, z) != b || world.getBlockMetadata(x, y, z) != meta) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(8, 8, 8);
			if (world.getEntitiesWithinAABB(EntityRadiation.class, box).size() < 40) {
				if (ReikaRandomHelper.doWithChance(0.02))
					RadiationEffects.instance.contaminateArea(world, x, y+ReikaRandomHelper.getSafeRandomInt(3), z, 8, 1, 0, false, RadiationIntensity.LETHAL);
				if (ReikaRandomHelper.doWithChance(0.1))
					RadiationEffects.instance.contaminateArea(world, x, y+ReikaRandomHelper.getSafeRandomInt(3), z, 1, 1, 0, false, RadiationIntensity.LETHAL);
			}
			//}
		}

		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.ice);
		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water);
		if (iceside != null || waterside != null) {
			if (ReikaRandomHelper.doWithChance(15))
				;//world.setBlock(i, j, k, ReactorBlocks.MATS.getBlock(), MatBlocks.SLAG.ordinal(), 3);
			if (iceside != null) {
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, iceside, Blocks.flowing_water, 0);
			}
			if (waterside != null) {
				ReikaWorldHelper.changeAdjBlock(world, x, y, z, waterside, Blocks.air, 0);
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
				ReikaParticleHelper.SMOKE.spawnAroundBlock(world, x, y, z, 8);
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
		icon = new IIcon[]{iconRegister.registerIcon("ReactorCraft:mat/slag"), iconRegister.registerIcon("ReactorCraft:fluid/slag_flow")};
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
