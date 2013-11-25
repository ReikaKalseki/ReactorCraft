package Reika.ReactorCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntitySodiumBoiler extends TileEntityNuclearBoiler {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public int getMaxTemperature() {
		return 0;
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
	public int getIndex() {
		return ReactorTiles.SODIUMBOILER.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}



	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		steam = NBT.getInteger("energy");
		tank.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("energy", steam);

		tank.writeToNBT(NBT);
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("sodium");
	}

}
