/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Event.CreeperExplodeEvent;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RadiationEffects {

	private static final Random rand = new Random();

	public static final RadiationEffects instance = new RadiationEffects();

	private RadiationEffects() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void dirtyBombs(CreeperExplodeEvent evt) {
		if (evt.creeper.getEntityData().getBoolean("radioactive")) {
			World world = evt.creeper.worldObj;
			int x = MathHelper.floor_double(evt.creeper.posX);
			int y = MathHelper.floor_double(evt.creeper.posY);
			int z = MathHelper.floor_double(evt.creeper.posZ);
			this.contaminateArea(world, x, y, z, 4, 3);
		}
	}

	public void applyEffects(EntityLivingBase e) {
		if (!e.isPotionActive(ReactorCraft.radiation) && !this.isEntityImmuneToAll(e))
			e.addPotionEffect(this.getRadiationEffect(36000));
		if (e instanceof EntityCreeper) {
			EntityCreeper ec = (EntityCreeper)e;
			ec.getEntityData().setBoolean("radioactive", true);
		}
	}

	public void applyPulseEffects(EntityLivingBase e) {
		if (!e.isPotionActive(ReactorCraft.radiation) && !this.isEntityImmuneToAll(e) && !this.hasHazmatSuit(e))
			e.addPotionEffect(this.getRadiationEffect(20));
	}

	public boolean isEntityImmuneToAll(EntityLivingBase e) {
		return e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode;
	}

	public boolean hasHazmatSuit(EntityLivingBase e) {
		for (int i = 1; i < 5; i++) {
			ItemStack is = e.getEquipmentInSlot(i);
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

	public void contaminateArea(World world, int x, int y, int z, int range, float density) {
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

	public void transformBlock(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return;
		if (world.isRemote)
			return;
		if (id == Blocks.deadbush)
			return;
		if (id == Blocks.leaves || id == Blocks.leaves2 || id.getMaterial() == Material.leaves || ModWoodList.isModLeaf(id, meta))
			world.setBlockToAir(x, y, z);
		if (id == Blocks.reeds) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.tallgrass)
			world.setBlock(x, y, z, Blocks.deadbush);
		if (id == Blocks.vine) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.waterlily) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.red_flower) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.yellow_flower) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.wheat) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.carrots) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.potatoes) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.cactus || id.getMaterial() == Material.cactus) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.pumpkin) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.pumpkin_stem) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.melon_block) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.melon_stem) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.sapling || id.getMaterial() == Material.plants)
			world.setBlock(x, y, z, Blocks.deadbush);
		if (id == Blocks.cocoa) {
			id.dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		if (id == Blocks.mossy_cobblestone)
			world.setBlock(x, y, z, Blocks.cobblestone);
		if (id == Blocks.grass || id.getMaterial() == Material.grass)
			world.setBlock(x, y, z, Blocks.dirt);
		if (id == Blocks.monster_egg)
			world.setBlock(x, y, z, ReikaBlockHelper.getSilverfishImitatedBlock(meta), 0, 3);
		if (id == ReactorBlocks.FLUORITE.getBlockInstance() || id == ReactorBlocks.FLUORITEORE.getBlockInstance()) {
			world.setBlock(x, y, z, id, meta+8, 3);
			world.func_147479_m(x, y, z);
		}

		TileEntity te = world.getTileEntity(x, y, z);

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

	public PotionEffect getRadiationEffect(int duration) {
		PotionEffect pot = new PotionEffect(ReactorCraft.radiation.id, duration, 0);
		pot.setCurativeItems(new ArrayList());
		return pot;
	}

}
