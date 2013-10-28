/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Instantiable.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Piping.TileEntityPipe;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityReactorBoiler extends TileEntityTankedReactorMachine implements ReactorCoreTE, IPipeConnection {

	private int steam;

	public static final int WATER_PER_STEAM = 1;

	private WorkingFluid fluid = WorkingFluid.EMPTY;

	@Override
	public int getIndex() {
		return ReactorTiles.BOILER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();

		this.getWater(world, x, y, z);

		if (thermalTicker.checkCap() && !world.isRemote) {
			this.updateTemperature(world, x, y, z);
		}
		if (temperature >= 650 && fluid == WorkingFluid.AMMONIA)
			this.detonateAmmonia(world, x, y, z);
		if (tank.getLevel() >= WATER_PER_STEAM && temperature > 100 && this.canBoilTankLiquid()) {
			steam++;
			if (tank.getActualFluid().equals(FluidRegistry.WATER))
				fluid = WorkingFluid.WATER;
			else if (tank.getActualFluid().equals(FluidRegistry.getFluid("ammonia")))
				fluid = WorkingFluid.AMMONIA;
			tank.removeLiquid(WATER_PER_STEAM);
			temperature -= 5;
		}

		//temperature = 200;

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}

		//ReikaJavaLibrary.pConsole(y+":"+steam+":"+temperature+":"+fluid.name()+":"+tank, Side.SERVER);

		//ReikaJavaLibrary.pConsole("T: "+temperature+"    W: "+tank.getLevel()+"    S: "+steam, Side.SERVER);

		this.balanceFluid(world, x, y, z);
		this.transferSteam(world, x, y, z);
	}

	private void transferSteam(World world, int x, int y, int z) {
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

	private void balanceFluid(World world, int x, int y, int z) {
		for (int i = 0; i < 2; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r == ReactorTiles.BOILER) {
				TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getBlockTileEntity(dx, dy, dz);
				if (te.tank.getLevel() < tank.getLevel() && (te.tank.isEmpty() || te.tank.getActualFluid() == tank.getActualFluid())) {
					int dl = tank.getLevel()-te.tank.getLevel();
					te.tank.addLiquid(dl/4+1, tank.getActualFluid());
					tank.removeLiquid(dl/4+1);
				}
			}
		}
	}

	private void detonateAmmonia(World world, int x, int y, int z) {
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

	private boolean canBoilTankLiquid() {
		if (!WorkingFluid.isWorkingFluid(tank.getActualFluid()))
			return false;
		return fluid == WorkingFluid.EMPTY || tank.getActualFluid().equals(fluid.getFluid());
	}

	private void getWater(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			MachineRegistry m = MachineRegistry.getMachine(world, dx, dy, dz);
			if (m == MachineRegistry.PIPE) {
				TileEntityPipe te = (TileEntityPipe)world.getBlockTileEntity(dx, dy, dz);
				if (te != null && te.liquidID == 9 && te.liquidLevel > 0) {
					int dl = te.liquidLevel/4+1;
					tank.addLiquid(dl, FluidRegistry.WATER);
					te.liquidLevel -= dl;
				}
			}
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
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
	public boolean canReceiveFrom(ForgeDirection from) {
		return from == ForgeDirection.DOWN;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		return WorkingFluid.isWorkingFluid(f);
	}

	@Override
	public double getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return 1000;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");
		tank.readFromNBT(NBT);

		fluid = WorkingFluid.getFromNBT(NBT);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);

		tank.writeToNBT(NBT);

		fluid.saveToNBT(NBT);
	}

	public int removeSteam() {
		int s = steam;
		steam = 0;
		return s;
	}

	public WorkingFluid getWorkingFluid() {
		return fluid;
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return with == ForgeDirection.DOWN ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
	}

	public void addLiquid(int amt, Fluid fluid) {
		tank.addLiquid(amt, fluid);
	}

}
