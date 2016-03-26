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

import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidFinite;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.RotaryCraft.Registry.BlockRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockThoriumFuel extends BlockFluidFinite {

	private IIcon[] icon;

	public BlockThoriumFuel(Material material) {
		super(ReactorCraft.LIFBe_fuel, material);

		this.setHardness(100F);
		this.setLightOpacity(100);
		this.setResistance(500);
		this.setCreativeTab(ReactorCraft.instance.isLocked() ? null : ReactorCraft.tabRctr);

		this.setQuantaPerBlock(8);
	}
	/*
	@Override
	public int getRenderType() {
		return 4;
	}*/

	@Override
	public void getSubBlocks(Item i, CreativeTabs c, List li) {
		li.add(new ItemStack(this, 1, 7));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		super.updateTick(world, x, y, z, rand);
		/*
		if (world.getBlockMetadata(x, y, z) == 0 && canOverwrite(world, x, y-1, z)) {
			world.setBlock(x, y-1, z, this, 0, 3);
			world.setBlock(x, y, z, Blocks.air);
			return;
		}

		for (int i = 2; i < 6; i++) {

		}*/

		if (rand.nextInt(4) == 0)
			;//this.tryAggressiveSpread(world, x, y, z, rand);

		if (ReikaRandomHelper.doWithChance(0.005))
			RadiationEffects.instance.contaminateArea(world, x, y+ReikaRandomHelper.getSafeRandomInt(2), z, 2, 0.25F, 0, false, RadiationIntensity.MODERATE);
	}

	private void tryAggressiveSpread(World world, int x, int y, int z, Random rand) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (dir != ForgeDirection.UP) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (world.getBlock(dx, dy, dz) == this || this.canOverwrite(world, dx, dy, dz)) {
					int f = this.getFillDifference(world, x, y, z, dx, dy, dz);
					if (f > 0) {
						this.spreadTo(world, x, y, z, dx, dy, dz);
						return;
					}
					else if (f == 0) {
						if (rand.nextInt(4) == 0) {
							world.setBlock(dx, dy, dz, this, world.getBlockMetadata(x, y, z), 3);
							world.setBlock(x, y, z, Blocks.air);
							return;
						}
					}
				}
			}
		}
	}

	private void spreadTo(World world, int x, int y, int z, int x2, int y2, int z2) {
		int f1 = this.getFill(world, x, y, z)-1;
		int f2 = this.getFill(world, x2, y2, z2)-1;
		int avg = (f1+f2);
		int fb1 = avg;
		int fb2 = avg%2 == 0 ? avg : avg+1;
		world.setBlock(x, y, z, this, fb1, 3);
		world.setBlock(x2, y2, z2, this, fb2, 3);
	}

	private int getFillDifference(World world, int x, int y, int z, int x2, int y2, int z2) {
		int f1 = this.getFill(world, x, y, z);
		int f2 = this.getFill(world, x2, y2, z2);
		return f1-f2;
	}

	private int getFill(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == this ? world.getBlockMetadata(x, y, z)+1 : 0;
	}

	/*
	@Override
	protected void flowIntoBlock(World world, int i, int j, int k, int l) {
		super.flowIntoBlock(world, i, j, k, l);
		//if (this.liquidCanDisplaceBlock(world, i, j, k)) {
		Block blockId = world.getBlock(i, j, k);
		if (blockId != Blocks.air) {
			blockId.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
		}
		//world.setBlock(i, j, k, this, l, 3);
		if (ReikaRandomHelper.doWithChance(0.005))
			RadiationEffects.instance.contaminateArea(world, i, j+ReikaRandomHelper.getSafeRandomInt(2), k, 2, 0.25F, 0, false, RadiationIntensity.MODERATE);
		//}

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
	 */

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		return this.canOverwrite(world, x, y, z);
	}

	/*
	private boolean blockBlocksFlow(World world, int x, int y, int z) {
		Block l = world.getBlock(x, y, z);
		ReikaJavaLibrary.pConsole(l);
		if (l == Blocks.air)
			return false;

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
	 */
	@Override
	public boolean isReplaceable(IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("ReactorCraft:fluid/lifbe_fuel");//icon = new IIcon[]{this.getFluid().getStillIcon(), this.getFluid().getFlowingIcon()};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta)
	{
		return blockIcon;//s != 0 && s != 1 ? icon[1] : icon[0];
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

	public static boolean canOverwrite(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b == ReactorBlocks.THORIUM.getBlockInstance())
			return false;//meta != 0;
		if (b == BlockRegistry.PIPING.getBlockInstance())
			return true;
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return true;
		String n = b.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
		if (n.contains("duct") || n.contains("conduit") || n.contains("cable") || n.contains("pipe"))
			return true;
		return false;
	}

}
