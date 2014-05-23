/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityReactorBoiler extends TileEntityNuclearBoiler {

	public static final int WATER_PER_STEAM = 200;
	public static final int DETTEMP = 650;

	private WorkingFluid fluid = WorkingFluid.EMPTY;

	@Override
	public int getIndex() {
		return ReactorTiles.BOILER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (temperature >= DETTEMP && fluid == WorkingFluid.AMMONIA)
			this.detonateAmmonia(world, x, y, z);

		if (tank.getLevel() >= WATER_PER_STEAM && temperature > 100 && this.canBoilTankLiquid()) {
			steam++;
			if (tank.getActualFluid().equals(FluidRegistry.WATER))
				fluid = WorkingFluid.WATER;
			else if (tank.getActualFluid().equals(FluidRegistry.getFluid("rc ammonia"))) {
				fluid = WorkingFluid.AMMONIA;
				ReactorAchievements.AMMONIA.triggerAchievement(this.getPlacer());
			}
			tank.removeLiquid(WATER_PER_STEAM);
			temperature -= 5;
		}

		if (DragonAPICore.debugtest) {
			tank.addLiquid(500, FluidRegistry.WATER);
			if (temperature < 100) {
				temperature = 120;
			}
		}

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}

		//ReikaJavaLibrary.pConsole(y+":"+steam+":"+temperature+":"+fluid.name()+":"+tank, Side.SERVER);

		//ReikaJavaLibrary.pConsole("T: "+temperature+"    W: "+tank.getLevel()+"    S: "+steam, Side.SERVER);

		this.transferSteam(world, x, y, z);
	}

	private void detonateAmmonia(World world, int x, int y, int z) {
		ReactorAchievements.NH3EXPLODE.triggerAchievement(this.getPlacer());
		BlockArray pipes = new BlockArray();
		int id = ReactorTiles.STEAMLINE.getBlockID();
		int meta = ReactorTiles.STEAMLINE.getBlockMetadata();
		pipes.recursiveAddWithMetadata(world, x, y+1, z, id, meta);
		for (int i = 0; i < pipes.getSize(); i++) {
			int[] xyz = pipes.getNthBlock(i);
			world.setBlock(xyz[0], xyz[1], xyz[2], 0);
			ReikaParticleHelper.EXPLODE.spawnAt(world, xyz[0], xyz[1], xyz[2]);
			ReikaItemHelper.dropItem(world, xyz[0], xyz[1], xyz[2], new ItemStack(Item.netherrackBrick));
		}
		world.setBlock(x, y, z, 0);
		ReikaItemHelper.dropItem(world, x, y, z, ReikaItemHelper.getSizedItemStack(ItemStacks.scrap, 8+rand.nextInt(18)));
		ReikaParticleHelper.EXPLODE.spawnAt(world, x, y, z);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 1.2F, 1);
		boolean flag = false;
		int r = 8;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int id2 = world.getBlockId(x+i, y+j, z+k);
					int meta2 = world.getBlockMetadata(x+i, y+j, z+k);
					Block b = Block.blocksList[id2];
					if (id2 != 0 && b.blockMaterial == Material.glass) {
						b.dropBlockAsItem(world, x+i, y+j, z+k, meta2, 0);
						world.setBlock(x+i, y+j, z+k, 0);
						ReikaRenderHelper.spawnDropParticles(world, x, y, z, b, meta2);
						flag = true;
					}
				}
			}
		}
		if (flag)
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.glass");
	}

	protected void transferSteam(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y+1, z);
		if (r == ReactorTiles.BOILER) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(x, y+1, z);
			if (steam > 0 && fluid != WorkingFluid.EMPTY) {
				if (te.fluid == WorkingFluid.EMPTY || te.fluid == fluid) {
					te.fluid = fluid;
					te.steam += steam;
					steam = 0;
				}
			}
		}
	}

	private boolean canBoilTankLiquid() {
		if (!WorkingFluid.isWorkingFluid(tank.getActualFluid()))
			return false;
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(worldObj, xCoord, yCoord, zCoord);
		if (temperature < Tamb+50)
			return false;
		return fluid == WorkingFluid.EMPTY || tank.getActualFluid().equals(fluid.getFluid());
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return WorkingFluid.isWorkingFluid(f);
	}

	@Override
	public int getMaxTemperature() {
		return 2000;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		fluid = WorkingFluid.getFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		fluid.saveToNBT(NBT);
	}

	public WorkingFluid getWorkingFluid() {
		return fluid;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	protected void overheat(World world, int x, int y, int z) {
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 8, true);
		for (int i = 0; i < 4; i++)
			ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), ItemStacks.scrap);
	}

}
