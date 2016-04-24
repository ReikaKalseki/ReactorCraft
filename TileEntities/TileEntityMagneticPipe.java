/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Blocks.BlockDuct;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityFusionInjector;
import Reika.RotaryCraft.API.Interfaces.Shockable;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.BlockRegistry;

public class TileEntityMagneticPipe extends TileEntityReactorPiping implements Shockable {

	private int charge;

	private StepTimer chargeTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNETPIPE.ordinal();
	}

	@Override
	public IIcon getBlockIcon() {
		return charge > 0 ? BlockDuct.getGlow() : Blocks.gold_block.getIcon(0, 0);
	}

	@Override
	public Block getPipeBlockType() {
		return Blocks.gold_block;
	}

	@Override
	public boolean isConnectedToNonSelf(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return f != null && f.equals(FluidRegistry.getFluid("rc fusion plasma"));
	}

	@Override
	protected void onIntake(TileEntity te) {

	}

	@Override
	public IIcon getGlassIcon() {
		return BlockRegistry.BLASTPANE.getBlockInstance().getIcon(0, 0);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		this.distributeCharge(world, x, y, z);
		this.updateCharge(world, x, y, z);

		if (charge <= 0 && !world.isRemote) {
			ReactorCraft.logger.debug("Melting magnetic pipe "+this+" with charge "+charge);
			charge = 0;
			if (fluid != null && fluid.getTemperature(world, x, y, z) > 5000) {
				world.setBlock(x, y, z, Blocks.flowing_lava);
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
				ReikaParticleHelper.LAVA.spawnAroundBlock(world, x, y, z, 5);
				ReactorAchievements.MELTPIPE.triggerAchievement(this.getPlacer());
			}
		}
	}

	private void distributeCharge(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);

			if (te instanceof WorldRift) {
				WorldLocation loc = ((WorldRift)te).getLinkTarget();
				if (loc != null) {
					te = ((WorldRift)te).getTileEntityFrom(dir);
					if (te == null)
						continue;
					dx = te.xCoord;
					dy = te.yCoord;
					dz = te.zCoord;
					world = te.worldObj;
				}
			}

			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.MAGNETPIPE) {
				TileEntityMagneticPipe tile = (TileEntityMagneticPipe)world.getTileEntity(dx, dy, dz);
				int dq = charge - tile.charge;
				if (dq > 0) {
					tile.charge += dq/4;
					charge -= dq/4;
				}
			}
		}
	}

	private void updateCharge(World world, int x, int y, int z) {
		ForgeDirection dir = ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water);
		if (dir != null) {
			EntityDischarge e = new EntityDischarge(world, x+0.5, y+0.5, z+0.5, charge, x+0.5+dir.offsetX, y+0.5+dir.offsetY, z+0.5+dir.offsetZ);
			if (!world.isRemote)
				world.spawnEntityInWorld(e);
			charge = 0;
		}
		else {
			if (charge > 1)
				charge *= 0.99;
			else
				charge = 0;
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("chg", charge);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		charge = NBT.getInteger("chg");
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += Math.pow(charge, 1.1);
	}

	@Override
	public int getMinDischarge() {
		return 256;
	}

	@Override
	public float getAimX() {
		return 0.5F;
	}

	@Override
	public float getAimY() {
		return 0.5F;
	}

	@Override
	public float getAimZ() {
		return 0.5F;
	}

	@Override
	public IIcon getOverlayIcon() {
		return null;
	}

	public int getCharge() {
		return charge;
	}

	private boolean isPlasmaAcceptingBlock(TileEntity te) {
		return te instanceof TileEntityMagneticPipe || te instanceof TileEntityFusionHeater || te instanceof TileEntityFusionInjector;
	}

	@Override
	protected boolean isInteractableTile(TileEntity te) {
		return te instanceof WorldRift || this.isPlasmaAcceptingBlock(te);
	}

	@Override
	public boolean canDischargeLongRange() {
		return true;
	}

}
