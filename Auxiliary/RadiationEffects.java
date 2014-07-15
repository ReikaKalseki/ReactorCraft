/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;

public class RadiationEffects {

	private static final Random rand = new Random();

	public static void applyEffects(EntityLivingBase e) {
		if (!e.isPotionActive(ReactorCraft.radiation) && !isEntityImmuneToAll(e))
			e.addPotionEffect(RadiationEffects.getRadiationEffect(36000));
	}

	public static void applyPulseEffects(EntityLivingBase e) {
		if (!e.isPotionActive(ReactorCraft.radiation) && !isEntityImmuneToAll(e) && !hasHazmatSuit(e))
			e.addPotionEffect(RadiationEffects.getRadiationEffect(20));
	}

	public static boolean isEntityImmuneToAll(EntityLivingBase e) {
		return e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode;
	}

	public static boolean hasHazmatSuit(EntityLivingBase e) {
		for (int i = 1; i < 5; i++) {
			ItemStack is = e.getCurrentItemOrArmor(i);
			if (is == null)
				return false;
			ReactorItems ri = ReactorItems.getEntry(is);
			if (ri == null)
				return false;
			if (!ri.isHazmat())
				return false;
		}
		return true;
	}

	public static void contaminateArea(World world, int x, int y, int z, int range, float density) {
		Random r = new Random();
		int num = (int)(Math.sqrt(range)*density);
		for (int i = 0; i < num; i++) {
			int dx = x-range+r.nextInt(range*2+1);
			int dy = y-range+r.nextInt(range*2+1);
			int dz = z-range+r.nextInt(range*2+1);
			EntityRadiation rad = new EntityRadiation(world, range);
			rad.setLocationAndAngles(dx+0.5, dy+0.5, dz+0.5, 0, 0);
			if (!world.isRemote)
				world.spawnEntityInWorld(rad);
		}
	}

	public static void transformBlock(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == 0)
			return;
		if (world.isRemote)
			return;
		if (id == Block.deadBush.blockID)
			return;
		Block b = Block.blocksList[id];
		if (id == Block.leaves.blockID || b.blockMaterial == Material.leaves || ModWoodList.isModLeaf(new ItemStack(id, 1, meta)))
			world.setBlock(x, y, z, 0);
		if (id == Block.reed.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.tallGrass.blockID)
			world.setBlock(x, y, z, Block.deadBush.blockID);
		if (id == Block.vine.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.waterlily.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.plantRed.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.plantYellow.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.crops.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.carrot.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.potato.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.cactus.blockID || b.blockMaterial == Material.cactus) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.pumpkin.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.pumpkinStem.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.melon.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.melonStem.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.sapling.blockID || b.blockMaterial == Material.plants)
			world.setBlock(x, y, z, Block.deadBush.blockID);
		if (id == Block.cocoaPlant.blockID) {
			b.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, 0);
		}
		if (id == Block.cobblestoneMossy.blockID)
			world.setBlock(x, y, z, Block.cobblestone.blockID);
		if (id == Block.grass.blockID || b.blockMaterial == Material.grass)
			world.setBlock(x, y, z, Block.dirt.blockID);
		if (id == Block.silverfish.blockID)
			world.setBlock(x, y, z, ReikaBlockHelper.getSilverfishImitatedBlock(meta), 0, 3);
		if (id == ReactorBlocks.FLUORITE.getBlockID() || id == ReactorBlocks.FLUORITEORE.getBlockID()) {
			world.setBlock(x, y, z, id, meta+8, 3);
			world.markBlockForRenderUpdate(x, y, z);
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (ModList.THAUMCRAFT.isLoaded()) {
			if (te instanceof INode) {
				INode n = (INode)te;
				n.addToContainer(Aspect.POISON, 10);
				n.addToContainer(Aspect.DEATH, 5);
				n.addToContainer(Aspect.ENTROPY, 10);
				n.addToContainer(Aspect.AURA, 5);
				n.addToContainer(Aspect.TAINT, 5);
				n.addToContainer(Aspect.ENERGY, 2);
				if (rand.nextInt(4) == 0) {
					if (n.getNodeType() == NodeType.NORMAL) {
						n.setNodeType(NodeType.UNSTABLE);
					}
					else if (n.getNodeType() == NodeType.UNSTABLE) {
						n.setNodeType(NodeType.TAINTED);
					}
				}
				if (rand.nextInt(8) == 0) {
					n.setNodeModifier(NodeModifier.BRIGHT);
				}
			}
		}
	}

	public static PotionEffect getRadiationEffect(int duration) {
		PotionEffect pot = new PotionEffect(ReactorCraft.radiation.id, duration, 0);
		pot.setCurativeItems(new ArrayList());
		return pot;
	}

}
