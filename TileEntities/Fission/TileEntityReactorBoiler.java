/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.SteamTile;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityReactorBoiler extends TileEntityNuclearBoiler implements SteamTile {

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
			tank.addLiquid(2500, FluidRegistry.WATER);
			temperature = 120;
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
		Block id = ReactorTiles.STEAMLINE.getBlock();
		int meta = ReactorTiles.STEAMLINE.getBlockMetadata();
		pipes.recursiveAddWithMetadata(world, x, y+1, z, id, meta);
		for (int i = 0; i < pipes.getSize(); i++) {
			Coordinate c = pipes.getNthBlock(i);
			c.setBlock(world, Blocks.air);
			ReikaParticleHelper.EXPLODE.spawnAt(world, c.xCoord, c.yCoord, c.zCoord);
			ReikaItemHelper.dropItem(world, c.xCoord, c.yCoord, c.zCoord, new ItemStack(Items.netherbrick));
		}
		world.setBlockToAir(x, y, z);
		ReikaItemHelper.dropItem(world, x, y, z, ReikaItemHelper.getSizedItemStack(ItemStacks.scrap, 8+rand.nextInt(18)));
		ReikaParticleHelper.EXPLODE.spawnAt(world, x, y, z);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 1.2F, 1);
		boolean flag = false;
		int r = 8;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					Block id2 = world.getBlock(x+i, y+j, z+k);
					int meta2 = world.getBlockMetadata(x+i, y+j, z+k);
					if (id2 != Blocks.air && id2.getMaterial() == Material.glass) {
						id2.dropBlockAsItem(world, x+i, y+j, z+k, meta2, 0);
						world.setBlockToAir(x+i, y+j, z+k);
						if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
							ReikaRenderHelper.spawnDropParticles(world, x, y, z, id2, meta2);
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
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getTileEntity(x, y+1, z);
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
		return m.isStandardPipe();
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

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		NeutronType type = e.getType();
		return !tank.isEmpty() && ReikaRandomHelper.doWithChance(type.getBoilerAbsorptionChance());
	}

	@Override
	public int getSteam() {
		return steam;
	}

}
