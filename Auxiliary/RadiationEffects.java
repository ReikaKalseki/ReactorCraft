/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashSet;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.CreeperExplodeEvent;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ItemInSystemEffect;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MESystemEffect;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.API.RadiationHandler.RadiationLevel;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Registry.RadiationShield;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IReadOnlyCollection;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

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
			this.contaminateArea(world, x, y, z, 4, 3, 1.5, true, RadiationIntensity.MODERATE);
		}
	}

	public boolean applyEffects(EntityLivingBase e, RadiationIntensity ri) {
		if (ri.causesHarm()) {
			if (!e.isPotionActive(ReactorCraft.radiation)) {
				if (!this.isEntityImmuneToAll(e) && (!ri.isShieldable() || !this.hasHazmatSuit(e))) {
					e.addPotionEffect(this.getRadiationEffect(ri));
					return true;
				}
			}
			if (ReikaEntityHelper.isEntityWearingPoweredArmor(e)) {
				for (int i = 1; i <= 4; i++) {
					ReikaItemHelper.dechargeItem(e.getEquipmentInSlot(i));
				}
			}
			if (e instanceof EntityCreeper) {
				EntityCreeper ec = (EntityCreeper)e;
				ec.getEntityData().setBoolean("radioactive", true);
			}
		}
		return false;
	}

	public void applyPulseEffects(EntityLivingBase e, RadiationIntensity ri) {
		if (!e.isPotionActive(ReactorCraft.radiation) && !this.isEntityImmuneToAll(e) && !this.hasHazmatSuit(e))
			e.addPotionEffect(this.getRadiationEffect(20, ri));
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

	public double contaminateArea(World world, int x, int y, int z, int range, float density, double force, boolean los, RadiationIntensity ri) {
		double frac = 1;
		int num = Math.max(1, (int)(Math.sqrt(range)*density));
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		for (int i = 0; i < num; i++) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, range);
			int dy = ReikaRandomHelper.getRandomPlusMinus(y, range);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, range);
			while(los && !this.isValidRadiationPosition(world, x, y, z, dx, dy, dz, 2)) {
				dx = ReikaRandomHelper.getRandomPlusMinus(x, range);
				dy = ReikaRandomHelper.getRandomPlusMinus(y, range);
				dz = ReikaRandomHelper.getRandomPlusMinus(z, range);
			}
			if (ReikaMathLibrary.py3d(dx-x, dy-y, dz-z) <= force) {
				frac -= 1D/num;
			}
			EntityRadiation rad = new EntityRadiation(world, range, ri);
			rad.setLocationAndAngles(dx+0.5, dy+0.5, dz+0.5, 0, 0);
			if (!world.isRemote)
				world.spawnEntityInWorld(rad);
		}
		return frac;
	}

	private boolean isValidRadiationPosition(World world, int x, int y, int z, int dx, int dy, int dz, double forceDist) {
		if (ReikaMathLibrary.py3d(dx-x, dy-y, dz-z) <= forceDist)
			return true;
		ArrayList<BlockKey> li = ReikaWorldHelper.getBlocksAlongVector(world, x+0.5, y+0.5, z+0.5, dx+0.5, dy+0.5, dz+0.5);
		double chance = 1;
		for (BlockKey bk : li) {
			RadiationShield rs = RadiationShield.getFrom(bk.blockID, bk.metadata);
			if (rs != null) {
				chance *= 1-rs.radiationDeflectChance/100D;
			}
		}
		boolean flag = chance > 0 && ReikaRandomHelper.doWithChance(chance);
		//if (flag) {
		//String vec = String.format("%d->%d,%d->%d,%d-%d", x, dx, y, dy, z, dz);
		//ReikaJavaLibrary.pConsole(String.format("Got %.10f%s chance for %s from %s", chance, "%", vec, li.toString()));
		//ReikaJavaLibrary.pConsole("Success");
		//}
		return flag;
	}

	public void transformBlock(World world, int x, int y, int z, RadiationIntensity ri) {
		if (world.isRemote)
			return;
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return;
		if (id == Blocks.deadbush)
			return;

		if (ri.isAtLeast(RadiationIntensity.HIGHLEVEL)) {
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
		}

		if (id == ReactorBlocks.FLUORITE.getBlockInstance() || id == ReactorBlocks.FLUORITEORE.getBlockInstance()) {
			world.setBlock(x, y, z, id, meta+8, 3);
			world.func_147479_m(x, y, z);
		}

		TileEntity te = world.getTileEntity(x, y, z);

		if (ri.isAtLeast(RadiationIntensity.MODERATE) && ModList.THAUMCRAFT.isLoaded()) {
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

	public PotionEffect getRadiationEffect(RadiationIntensity ri) {
		return this.getRadiationEffect(ri.potionDuration, ri);
	}

	private PotionEffect getRadiationEffect(int duration, RadiationIntensity ri) {
		PotionEffect pot = new PotionEffect(ReactorCraft.radiation.id, duration, ri.ordinal());
		pot.setCurativeItems(new ArrayList());
		return pot;
	}

	public static enum RadiationIntensity implements RadiationLevel {
		BACKGROUND(0), //always
		LOWLEVEL(100), //neutrons, waste containers
		MODERATE(1200), //plutonium, creepers
		HIGHLEVEL(6000), //waste
		LETHAL(36000); //Meltdowns; Hazmat does not protect

		public static final RadiationIntensity[] radiationList = values();

		private final int potionDuration;

		private RadiationIntensity(int t) {
			potionDuration = t;
		}

		public boolean isShieldable() {
			return this.ordinal() <= HIGHLEVEL.ordinal();
		}

		public boolean causesHarm() {
			return this != BACKGROUND;
		}

		public boolean isAtLeast(RadiationIntensity ri) {
			return this.ordinal() >= ri.ordinal();
		}
	}

	public MESystemEffect createMESystemEffect() {
		return new ItemInSystemEffect(ReactorItems.WASTE.getStackOfMetadata(OreDictionary.WILDCARD_VALUE)) {

			@Override
			public int getTickFrequency() {
				return 2400;
			}

			@Override
			protected void doEffect(IGrid grid, long amt) {
				IReadOnlyCollection<IGridNode> c = grid.getNodes();
				HashSet<WorldLocation> locations = new HashSet();
				for (IGridNode ign : c) {
					IGridBlock igb = ign.getGridBlock();
					if (igb != null && igb.isWorldAccessible()) {
						DimensionalCoord loc = igb.getLocation();
						locations.add(new WorldLocation(loc.getWorld(), loc.x, loc.y, loc.z));
					}
				}
				WorldLocation loc = ReikaJavaLibrary.getRandomCollectionEntry(rand, locations);
				this.leakRadiation(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
			}

			protected void leakRadiation(World world, int x, int y, int z) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
				if (!world.isRemote)
					world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, dir, NeutronType.WASTE));
			}};
	}

}
