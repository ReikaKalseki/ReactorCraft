/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityReactorPiping;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.Shockable;
import Reika.RotaryCraft.Entities.EntityDischarge;

public class TileEntityMagneticPipe extends TileEntityReactorPiping implements Shockable {

	private int charge;

	private StepTimer chargeTimer = new StepTimer(20);

	@Override
	public int getIndex() {
		return ReactorTiles.MAGNETPIPE.ordinal();
	}

	@Override
	public Icon getBlockIcon() {
		return Block.blockGold.getIcon(0, 0);
	}

	@Override
	public Block getPipeBlockType() {
		return Block.blockGold;
	}

	@Override
	public boolean isConnectedToNonSelf(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return f != null && f.equals(FluidRegistry.getFluid("fusion plasma"));
	}

	@Override
	protected void onIntake(TileEntity te) {

	}

	@Override
	public Icon getGlassIcon() {
		return RotaryCraft.blastglass.getIcon(0, 0);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		this.distributeCharge(world, x, y, z);
		this.updateCharge(world, x, y, z);

		if (charge <= 0) {
			charge = 0;
			if (fluid != null && fluid.getTemperature(world, x, y, z) > 5000) {
				world.setBlock(x, y, z, Block.lavaMoving.blockID);
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
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.MAGNETPIPE) {
				TileEntityMagneticPipe tile = (TileEntityMagneticPipe)world.getBlockTileEntity(dx, dy, dz);
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
			//charge *= 0.99;
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("chg", charge);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		charge = NBT.getInteger("chg");
	}

	@Override
	public void onDischarge(int charge, double range) {
		this.charge += charge;
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

}
