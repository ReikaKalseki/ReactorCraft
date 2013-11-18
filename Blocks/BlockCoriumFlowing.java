/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowing;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Registry.MatBlocks;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCoriumFlowing extends BlockFlowing implements ILiquid {

	int numAdjacentSources = 0;
	boolean isOptimalFlowDirection[] = new boolean[4];
	int flowCost[] = new int[4];

	public BlockCoriumFlowing(int i, Material material) {
		super(i, material);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	@Override
	public int getRenderType() {
		return 4;
	}

	private void updateFlow(World world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, ReactorBlocks.CORIUMSTILL.getBlockID(), l, 2);
	}

	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate(World world) {
		return 40;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		int oldDecay = this.getFlowDecay(world, x, y, z);
		byte viscosity = 1;
		int flowDecay;

		if (oldDecay > 0) {
			numAdjacentSources = 0;
			int minFlowDecay = this.getSmallestFlowDecay(world, x - 1, y, z, -100);
			minFlowDecay = this.getSmallestFlowDecay(world, x + 1, y, z, minFlowDecay);
			minFlowDecay = this.getSmallestFlowDecay(world, x, y, z - 1, minFlowDecay);
			minFlowDecay = this.getSmallestFlowDecay(world, x, y, z + 1, minFlowDecay);
			flowDecay = minFlowDecay + viscosity;

			if (flowDecay >= 8 || minFlowDecay < 0) {
				flowDecay = -1;
			}

			int decayAbove = this.getFlowDecay(world, x, y + 1, z);
			if (decayAbove >= 0) {
				if (decayAbove >= 8) {
					flowDecay = decayAbove;
				}
				else {
					flowDecay = decayAbove + 8;
				}
			}

			boolean update = true;
			if (oldDecay < 8 && flowDecay < 8 && flowDecay > oldDecay && random.nextDouble() < 0.2) {
				flowDecay = oldDecay;
				update = false;
			}

			if (flowDecay == oldDecay) {
				if (update) {
					this.updateFlow(world, x, y, z);
				}
			}
			else {
				oldDecay = flowDecay;

				if (flowDecay < 0) {
					world.setBlockToAir(x, y, z);
				}
				else {
					world.setBlockMetadataWithNotify(x, y, z, flowDecay, 2);
					world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
					world.notifyBlocksOfNeighborChange(x, y, z, blockID);
				}
			}
		}
		else {
			this.updateFlow(world, x, y, z);
		}

		if (this.liquidCanDisplaceBlock(world, x, y - 1, z)) {
			if (oldDecay >= 8) {
				this.flowIntoBlock(world, x, y - 1, z, oldDecay);
			}
			else {
				this.flowIntoBlock(world, x, y - 1, z, oldDecay + 8);
			}
		}
		else if (oldDecay >= 0 && (oldDecay == 0 || this.blockBlocksFlow(world, x, y - 1, z))) {
			boolean[] flowDirection = this.getOptimalFlowDirections(world, x, y, z);
			flowDecay = oldDecay + viscosity;

			if (oldDecay >= 8) {
				flowDecay = 1;
			}

			if (flowDecay >= 8) {
				return;
			}

			if (flowDirection[0]) {
				this.flowIntoBlock(world, x - 1, y, z, flowDecay);
			}

			if (flowDirection[1]) {
				this.flowIntoBlock(world, x + 1, y, z, flowDecay);
			}

			if (flowDirection[2]) {
				this.flowIntoBlock(world, x, y, z - 1, flowDecay);
			}

			if (flowDirection[3]) {
				this.flowIntoBlock(world, x, y, z + 1, flowDecay);
			}
		}

		this.freezeWithChance(world, x, y, z);
	}

	private void freezeWithChance(World world, int x, int y, int z) {
		if (ReikaMathLibrary.doWithChance(5) && !world.provider.isHellWorld) {
			world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockID(), MatBlocks.SLAG.ordinal(), 3);
		}
	}

	private void flowIntoBlock(World world, int i, int j, int k, int l) {
		if (this.liquidCanDisplaceBlock(world, i, j, k)) {
			int blockId = world.getBlockId(i, j, k);
			if (blockId > 0) {
				Block.blocksList[blockId].dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
			}
			world.setBlock(i, j, k, blockID, l, 3);
			if (ReikaMathLibrary.doWithChance(12.5))
				RadiationEffects.contaminateArea(world, i, j+ReikaRandomHelper.getSafeRandomInt(3), k, 1);
		}

		ForgeDirection iceside = ReikaWorldHelper.checkForAdjBlock(world, i, j, k, Block.ice.blockID);
		ForgeDirection waterside = ReikaWorldHelper.checkForAdjMaterial(world, i, j, k, Material.water);
		if (iceside != null || waterside != null) {
			if (ReikaMathLibrary.doWithChance(15))
				world.setBlock(i, j, k, ReactorBlocks.MATS.getBlockID(), MatBlocks.SLAG.ordinal(), 3);
			if (iceside != null) {
				ReikaWorldHelper.changeAdjBlock(world, i, j, k, iceside, Block.waterMoving.blockID, 0);
			}
			if (waterside != null) {
				ReikaWorldHelper.changeAdjBlock(world, i, j, k, waterside, 0, 0);
				ReikaSoundHelper.playSoundAtBlock(world, i, j, k, "random.fizz");
				ReikaParticleHelper.SMOKE.spawnAroundBlock(world, i, j, k, 8);
			}
		}
	}

	private int calculateFlowCost(World world, int i, int j, int k, int l, int i1) {
		int j1 = 1000;
		for (int k1 = 0; k1 < 4; k1++) {
			if (k1 == 0 && i1 == 1 || k1 == 1 && i1 == 0 || k1 == 2 && i1 == 3 || k1 == 3 && i1 == 2) {
				continue;
			}
			int l1 = i;
			int i2 = j;
			int j2 = k;
			if (k1 == 0) {
				l1--;
			}
			if (k1 == 1) {
				l1++;
			}
			if (k1 == 2) {
				j2--;
			}
			if (k1 == 3) {
				j2++;
			}
			if (this.blockBlocksFlow(world, l1, i2, j2) || world.getBlockMaterial(l1, i2, j2) == blockMaterial && world.getBlockMetadata(l1, i2, j2) == 0) {
				continue;
			}
			if (!this.blockBlocksFlow(world, l1, i2 - 1, j2)) {
				return l;
			}
			if (l >= 4) {
				continue;
			}
			int k2 = this.calculateFlowCost(world, l1, i2, j2, l + 1, k1);
			if (k2 < j1) {
				j1 = k2;
			}
		}

		return j1;
	}

	private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) {
		for (int l = 0; l < 4; l++) {
			flowCost[l] = 1000;
			int j1 = i;
			int i2 = j;
			int j2 = k;
			if (l == 0) {
				j1--;
			}
			if (l == 1) {
				j1++;
			}
			if (l == 2) {
				j2--;
			}
			if (l == 3) {
				j2++;
			}
			if (this.blockBlocksFlow(world, j1, i2, j2) || world.getBlockMaterial(j1, i2, j2) == blockMaterial && world.getBlockMetadata(j1, i2, j2) == 0) {
				continue;
			}
			if (!this.blockBlocksFlow(world, j1, i2 - 1, j2)) {
				flowCost[l] = 0;
			}
			else {
				flowCost[l] = this.calculateFlowCost(world, j1, i2, j2, 1, l);
			}
		}

		int i1 = flowCost[0];
		for (int k1 = 1; k1 < 4; k1++) {
			if (flowCost[k1] < i1) {
				i1 = flowCost[k1];
			}
		}

		for (int l1 = 0; l1 < 4; l1++) {
			isOptimalFlowDirection[l1] = flowCost[l1] == i1;
		}

		return isOptimalFlowDirection;
	}

	private boolean blockBlocksFlow(World world, int x, int y, int z) {
		int l = world.getBlockId(x, y, z);

		if (l != Block.doorWood.blockID && l != Block.doorIron.blockID && l != Block.signPost.blockID && l != Block.ladder.blockID && l != Block.reed.blockID) {
			if (l == 0) {
				return false;
			}
			else {
				Material material = Block.blocksList[l].blockMaterial;
				return material == Material.portal ? true : material.blocksMovement();
			}
		}
		else {
			return true;
		}
	}

	@Override
	protected int getSmallestFlowDecay(World world, int x, int y, int z, int par5) {
		int i1 = this.getFlowDecay(world, x, y, z);

		if (i1 < 0) {
			return par5;
		}
		else {
			if (i1 == 0) {
				++numAdjacentSources;
			}

			if (i1 >= 8) {
				i1 = 0;
			}

			return par5 >= 0 && i1 >= par5 ? par5 : i1;
		}
	}

	private boolean liquidCanDisplaceBlock(World world, int i, int j, int k) {
		Material material = world.getBlockMaterial(i, j, k);
		if (material == blockMaterial) {
			return false;
		} else {
			return !this.blockBlocksFlow(world, i, j, k);
		}
	}

	@Override
	public int stillLiquidId() {
		return ReactorBlocks.CORIUMSTILL.getBlockID();
	}

	@Override
	public boolean isMetaSensitive() {
		return false;
	}

	@Override
	public int stillLiquidMeta() {
		return 0;
	}

	@Override
	public boolean isBlockReplaceable(World world, int i, int j, int k) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		theIcon = new Icon[]{iconRegister.registerIcon("ReactorCraft:slag"), iconRegister.registerIcon("ReactorCraft:slag_flow")};
	}

	private void checkForHarden(World world, int x, int y, int z)
	{
		if (world.getBlockId(x, y, z) == blockID) {
			boolean flag = false;

			if (flag || world.getBlockMaterial(x, y, z - 1) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x, y, z + 1) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x - 1, y, z) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x + 1, y, z) == Material.water)
				flag = true;
			if (flag || world.getBlockMaterial(x, y + 1, z) == Material.water)
				flag = true;

			if (flag) {
				world.setBlock(x, y, z, ReactorBlocks.MATS.getBlockID(), MatBlocks.SLAG.ordinal(), 3);
				this.triggerLavaMixEffects(world, x, y, z);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4)
	{
		this.checkForHarden(world, par2, par3, par4);
		world.scheduleBlockUpdate(par2, par3, par4, blockID, this.tickRate(world));
	}

}
