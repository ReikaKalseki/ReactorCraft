package Reika.ReactorCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityTankedReactorMachine;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Piping.TileEntityPipe;

public class TileEntityReactorBoiler extends TileEntityTankedReactorMachine implements ReactorCoreTE {

	private int steam;

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
		if (tank.getLevel() >= 1 && temperature > 100) {
			steam++;
			tank.removeLiquid(1);

		}
		//ReikaJavaLibrary.pConsole("T: "+temperature+"    W: "+tank.getLevel()+"    S: "+steam, Side.SERVER);
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
		return FluidRegistry.WATER;
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
	}

	public int removeSteam() {
		int s = steam;
		steam = 0;
		return s;
	}

}
