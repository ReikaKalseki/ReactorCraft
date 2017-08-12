/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPoisonGas extends BlockFluidClassic {

	public BlockPoisonGas(Fluid f, Material material) {
		super(f, material);

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
		if (this.liquidCanDisplaceBlock(world, x, y, z)) {
			if (b.getMaterial() == Material.plants || b.getMaterial() == Material.grass || b.getMaterial() == Material.vine || b.getMaterial() == Material.wood || b.getMaterial() == Material.leaves || b.getMaterial() == Material.cloth || b.getMaterial() == Material.gourd) {
				if (this.shouldReactHypergolically())
					world.setBlock(x, y, z, Blocks.fire, 0, 3);
				else
					world.setBlock(x, y, z, this, l, 3);
			}
			else {
				if (!b.isAir(world, x, y, z))
					b.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlock(x, y, z, this, l, 3);
			}
		}
	}

	private boolean shouldReactHypergolically() {
		return this.getFluid() == ReactorCraft.HF;
	}

	private boolean blockBlocksFlow(IBlockAccess world, int x, int y, int z) {
		Block l = world.getBlock(x, y, z);
		return l.getMaterial().blocksMovement() || l instanceof BlockFire;
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, int x, int y, int z) {
		return this.liquidCanDisplaceBlock(world, x, y, z);
	}

	private boolean liquidCanDisplaceBlock(IBlockAccess world, int i, int j, int k) {
		Material mat = ReikaWorldHelper.getMaterial(world, i, j, k);
		return mat != blockMaterial && !this.blockBlocksFlow(world, i, j, k);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("ReactorCraft:fluid/"+ReactorBlocks.getBlock(this).name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4) {
		world.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(world));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)e;
			if (!RadiationEffects.instance.hasHazmatSuit(elb)) {
				elb.addPotionEffect(new PotionEffect(Potion.poison.id, 20, 4));
				if (world.rand.nextInt(8) == 0)
					elb.attackEntityFrom(ReactorCraft.gasDamage, 1);
				if (this.shouldReactHypergolically())
					elb.setFire(5);
			}
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		switch(ReactorBlocks.getBlock(this)) {
			case CHLORINE: {
				if (rand.nextInt(1+Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
					int clr = Potion.poison.getLiquidColor();
					double r = ReikaColorAPI.getRed(clr)/255D;
					double g = ReikaColorAPI.getGreen(clr)/255D;
					double b = ReikaColorAPI.getBlue(clr)/255D;
					ReikaParticleHelper.MOBSPELL.spawnAroundBlock(world, x, y, z, r, g, b, 1);
				}
				break;
			}
			case HF: {
				int clr = Potion.poison.getLiquidColor();
				double r = ReikaColorAPI.getRed(clr)/255D;
				double g = ReikaColorAPI.getGreen(clr)/255D;
				double b = ReikaColorAPI.getBlue(clr)/255D;
				ReikaParticleHelper.AMBIENTMOBSPELL.spawnAroundBlock(world, x, y, z, r, g, b, 1);
				if (rand.nextInt(64) == 0)
					ReikaParticleHelper.CLOUD.spawnAroundBlock(world, x, y, z, 1);
				if (rand.nextInt(16) == 0)
					ReikaParticleHelper.FLAME.spawnAroundBlock(world, x, y, z, 1);
				break;
			}
			default:
				break;
		}

	}

}
