/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Power.ReikaEUHelper;
import Reika.DragonAPI.ModInteract.Power.ReikaRFHelper;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.ElectriCraft.API.WrappableWireSource;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = {"cofh.api.energy.IEnergyHandler", "ic2.api.energy.tile.IEnergySource", "Reika.ElectriCraft.API.WrappableWireSource"})
public class TileEntityReactorGenerator extends TileEntityReactorBase implements IEnergyHandler, IEnergySource, Screwdriverable, MultiBlockTile, WrappableWireSource {

	private ForgeDirection facingDir;

	private long power;
	private int torquein;
	private int omegain;

	private int lasttorquein;
	private int lastomegain;

	private Modes mode = Modes.RF;

	private boolean hasMultiblock;

	public boolean hasMultiBlock() {
		return hasMultiblock || DragonAPICore.debugtest;
	}

	public void setHasMultiBlock(boolean has) {
		if (hasMultiblock && !has)
			this.testBreakageFailure();
		hasMultiblock = has;
	}

	private void testBreakageFailure() {
		if (omegain > 1024) {
			this.fail(worldObj, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.getWorldTime()%128 == 0)
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);

		lastomegain = omegain;
		lasttorquein = torquein;

		if (hasMultiblock || DragonAPICore.debugtest)
			this.getPower(world, x, y, z, meta);
		else {
			omegain = torquein = 0;
		}

		power = (long)omegain*(long)torquein;

		//ReikaJavaLibrary.pConsole(power, Side.SERVER);

		if (power > 0) {
			ForgeDirection write = this.getFacing().getOpposite();
			TileEntity tile = this.getAdjacentTileEntity(write);
			switch(mode) {
				case RF:
					if (tile instanceof IEnergyReceiver) {
						IEnergyReceiver rc = (IEnergyReceiver)tile;
						//if (rc.canConnectEnergy(this.getFacing())) {
						int used = rc.receiveEnergy(this.getFacing(), (int)this.getGenUnits(), false);
						//}
					}
					else if (tile instanceof IEnergyHandler) {
						IEnergyHandler rc = (IEnergyHandler)tile;
						//if (rc.canConnectEnergy(this.getFacing())) {
						int used = rc.receiveEnergy(this.getFacing(), (int)this.getGenUnits(), false);
						//}
					}
					break;
				case EU:
					if (tile instanceof IEnergySink) {
						IEnergySink rc = (IEnergySink)tile;
						if (rc.acceptsEnergyFrom(this, this.getFacing())) {
							double leftover = rc.injectEnergy(this.getFacing(), (int)this.getGenUnits(), this.getSourceTier());
						}
					}
					break;
				case ELC: //handled by ELC logic
					break;
			}
		}
	}

	private void fail(World world, int x, int y, int z) {
		int l = this.getGeneratorLength()/2;
		world.setBlockToAir(x, y, z);
		double dx = x+0.5+this.getFacing().offsetX*l;
		double dz = z+0.5+this.getFacing().offsetZ*l;
		new FlyingBlocksExplosion(world, dx, y+0.5, dz, 12).doExplosion();
	}

	public static int getGeneratorLength() {
		return 10;
	}

	private void getPower(World world, int x, int y, int z, int meta) {
		int len = this.getGeneratorLength();
		int dx = x+this.getFacing().offsetX*len;
		int dy = y+this.getFacing().offsetY*len;
		int dz = z+this.getFacing().offsetZ*len;

		ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);

		if (r != null && r.isTurbine()) {
			TileEntityTurbineCore te = (TileEntityTurbineCore)this.getTileEntity(dx, dy, dz);
			if (te.getSteamMovement() == this.getFacing().getOpposite()) {
				power = te.getPower();
				omegain = te.getOmega();
				torquein = te.getTorque();
			}
			else {
				omegain = torquein = 0;
				power = 0;
			}
		}
		else {
			omegain = torquein = 0;
			power = 0;
		}
	}

	public ForgeDirection getFacing() {
		return facingDir != null ? facingDir : ForgeDirection.EAST;
	}

	public void setFacing(ForgeDirection dir) {
		facingDir = dir;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.GENERATOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += 0.5*ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omegain+1, 2), 1.05);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facingDir = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");

		power = NBT.getLong("pwr");

		if (NBT.hasKey("mode"))
			mode = Modes.list[NBT.getInteger("mode")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());
		NBT.setBoolean("multi", hasMultiblock);

		NBT.setLong("pwr", power);

		NBT.setInteger("mode", mode.ordinal());
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return this.getMode() == Modes.RF ? (int)this.getGenUnits() : 0;
	}

	public double getGenUnits() {
		return power*this.getMode().ratio;
	}

	public String getGeneratedOutputForDisplay() {
		return this.getName()+" generating "+mode.getDisplay(this);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from == this.getFacing().getOpposite();
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		int l = this.getGeneratorLength();
		int x1 = xCoord+1+this.getFacing().offsetX*l;
		int z1 = zCoord+1+this.getFacing().offsetZ*l;
		int mx = Math.min(x1, xCoord);
		int mz = Math.min(z1, zCoord);
		int mx2 = Math.max(x1, xCoord);
		int mz2 = Math.max(z1, zCoord);
		return AxisAlignedBB.getBoundingBox(mx, yCoord-2, mz, mx2, yCoord+3, mz2).expand(6, 6, 6);
	}

	public static enum Modes {
		RF("Redstone Flux", 1D/ReikaRFHelper.getWattsPerRF(), PowerTypes.RF),
		EU("EU", 1D/ReikaEUHelper.getWattsPerEU(), PowerTypes.EU),
		ELC("ElectriCraft", 1, PowerTypes.ELECTRICRAFT);

		public final String name;
		private final double ratio;
		public final PowerTypes type;

		private static final Modes[] list = values();

		private Modes(String s, double r, PowerTypes p) {
			name = s;
			ratio = r;
			type = p;
		}

		public String getDisplay(TileEntityReactorGenerator te) {
			if (this == ELC) {
				return this.getELCDisplay(te);
			}
			return String.format("%.3f %s/t.", te.power*ratio, this.name());
		}

		@ModDependent(ModList.ELECTRICRAFT)
		private String getELCDisplay(TileEntityReactorGenerator te) {
			return te.getTorque()/WireNetwork.TORQUE_PER_AMP+"A @ "+te.getOmega()*WireNetwork.TORQUE_PER_AMP+"V";
		}

		public boolean exists() {
			return type.isLoaded();
		}
	}

	public Modes stepType() {
		int o = mode.ordinal();
		Modes m = Modes.list[o];
		do {
			if (o < Modes.list.length-1) {
				o++;
			}
			else {
				o = 0;
			}
			m = Modes.list[o];
		} while (!m.exists());
		mode = m;
		return mode;
	}

	public Modes getMode() {
		return mode;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection dir) {
		return mode == Modes.EU;// && dir == this.getFacing().getOpposite();
	}

	@Override
	public double getOfferedEnergy() {
		return mode == Modes.EU ? this.getGenUnits() : 0;
	}

	@Override
	public void drawEnergy(double amount) {

	}

	@Override
	public int getSourceTier() {
		return 5;
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.addTileToNet();
	}

	@ModDependent(ModList.IC2)
	private void addTileToNet() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalidate) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.removeTileFromNet();
	}

	@ModDependent(ModList.IC2)
	private void removeTileFromNet() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		this.stepType();
		return true;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		if (side.offsetY == 0) {
			this.setFacing(side);
			return true;
		}
		return false;
	}

	@Override
	public void breakBlock() {
		if (!worldObj.isRemote) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = xCoord+dir.offsetX;
				int dy = yCoord+dir.offsetY;
				int dz = zCoord+dir.offsetZ;
				Block b = worldObj.getBlock(dx, dy, dz);
				if (b instanceof BlockMultiBlock) {
					((BlockMultiBlock)b).breakMultiBlock(worldObj, dx, dy, dz);
				}
			}
		}
	}

	@Override
	public boolean canConnectToSide(ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public boolean isFunctional() {
		return hasMultiblock && this.getMode() == Modes.ELC;
	}

	@Override
	public int getOmega() {
		return omegain;
	}

	@Override
	public int getTorque() {
		return torquein;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return 0;
	}

	@Override
	public void setIORenderAlpha(int io) {

	}

	@Override
	public boolean hasPowerStatusChangedSinceLastTick() {
		return lastomegain != omegain || lasttorquein != torquein;
	}

}
