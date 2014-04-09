package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaBuildCraftHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.PowerTransferHelper;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import cofh.api.energy.IEnergyHandler;

public class TileEntityReactorGenerator extends TileEntityReactorBase implements ShaftPowerReceiver, IEnergyHandler {

	private ForgeDirection facingDir;

	private int omega;
	private int torque;
	private long power;
	private int iotick;

	public boolean hasMultiblock;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (iotick > 0)
			iotick -= 8;

		if ((world.getWorldTime()&31) == 0)
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);

		TileEntity te = this.getAdjacentTileEntity(this.getFacing());
		if (!PowerTransferHelper.checkPowerFrom(this, te)) {
			this.noInputMachine();
		}

		int writex = x+this.getFacing().getOpposite().offsetX;
		int writey = y+this.getFacing().getOpposite().offsetY;
		int writez = z+this.getFacing().getOpposite().offsetZ;
		if (power > 0) {
			int id = world.getBlockId(writex, writey, writez);
			if (id != 0) {
				Block b = Block.blocksList[id];
				int metadata = world.getBlockMetadata(writex, writey, writez);
				if (b.hasTileEntity(metadata)) {
					TileEntity tile = world.getBlockTileEntity(writex, writey, writez);
					if (tile instanceof IEnergyHandler) {
						IEnergyHandler rc = (IEnergyHandler)tile;
						if (rc.canInterface(facingDir)) {
							int rf = this.getGenRF();
							float used = rc.receiveEnergy(facingDir, rf, false);
						}
					}
				}
			}
		}
	}

	public int getGenRF() {
		return (int)(power*10/ReikaBuildCraftHelper.getWattsPerMJ());
	}

	public ForgeDirection getFacing() {
		return facingDir != null ? facingDir : ForgeDirection.EAST;
	}

	protected void setFacing(ForgeDirection dir) {
		facingDir = dir;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.GENERATOR.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facingDir = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());
		NBT.setBoolean("multi", hasMultiblock);

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);
	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		ForgeDirection dir = this.getFacing();
		return x == xCoord+dir.offsetX && y == yCoord && z == zCoord+dir.offsetZ;
	}

	@Override
	public boolean isReceiving() {
		return hasMultiblock;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return this.getGenRF();
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return from == facingDir.getOpposite();
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

}
