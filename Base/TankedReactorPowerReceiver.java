package Reika.ReactorCraft.Base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ReactorCraft.Auxiliary.ReactorPowerReceiver;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;


public abstract class TankedReactorPowerReceiver extends TileEntityTankedReactorMachine implements ReactorPowerReceiver {

	private long power;
	private int omega;
	private int torque;

	private int iotick;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!PowerTransferHelper.checkPowerFrom(this, ForgeDirection.DOWN)) {
			this.noInputMachine();
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (iotick > 0)
			iotick -= 8;
	}

	@Override
	public final int getOmega() {
		return omega;
	}

	@Override
	public final int getTorque() {
		return torque;
	}

	@Override
	public final long getPower() {
		return power;
	}

	@Override
	public final int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public final void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public final void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public final void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public final void setPower(long power) {
		this.power = power;
	}

	@Override
	public final boolean isReceiving() {
		return true;
	}

	@Override
	public final void noInputMachine() {
		torque = omega = 0;
		power = 0;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);

		omega = NBT.getInteger("speed");
		torque = NBT.getInteger("trq");
		power = NBT.getLong("pwr");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);

		NBT.setInteger("speed", omega);
		NBT.setInteger("trq", torque);
		NBT.setLong("pwr", power);
	}

	public final boolean sufficientPower() {
		return power >= this.getMinPower() && omega >= this.getMinSpeed() && torque >= this.getMinTorque();
	}
}
