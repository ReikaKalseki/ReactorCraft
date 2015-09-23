/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.RenderableDuct;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.TransferAmount;

public abstract class TileEntityReactorPiping extends TileEntityReactorBase implements RenderableDuct {

	protected Fluid fluid;
	protected int level;

	private boolean[] connections = new boolean[6];

	public abstract boolean isValidFluid(Fluid f);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		Fluid f = this.getFluidType();
		this.intakeFluid(world, x, y, z);
		if (this.getLevel() <= 0) {
			this.setLevel(0);
			this.setFluid(null);
		}
		else {
			this.dumpContents(world, x, y, z);
		}
		Fluid f2 = this.getFluidType();
		if (f != f2) {
			this.syncAllData(true);
			world.markBlockForUpdate(x, y, z);
		}
	}

	public boolean isConnectedDirectly(ForgeDirection dir) {
		return connections[dir.ordinal()];
	}

	protected boolean isInteractableTile(TileEntity te) {
		if (te == null)
			return false;
		if (te instanceof IFluidHandler) {
			String name = te.getClass().getSimpleName().toLowerCase();
			return !name.contains("conduit") && !name.contains("pipe");
		}
		return false;
	}

	protected final boolean canInteractWith(World world, int x, int y, int z, ForgeDirection side) {
		if (!connections[side.ordinal()])
			return false;
		int dx = x+side.offsetX;
		int dy = y+side.offsetY;
		int dz = z+side.offsetZ;
		Block id = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == Blocks.air)
			return false;
		ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
		if (m == this.getMachine())
			return true;
		TileEntity te = this.getTileEntity(dx, dy, dz);
		return (te instanceof PipeConnector || te instanceof IFluidHandler) && this.isInteractableTile(te);
	}

	@Override
	public final void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public final Fluid getFluidType() {
		return fluid;
	}

	public final int getLevel() {
		return level;
	}

	public final int getX() {
		return xCoord;
	}

	public final int getY() {
		return yCoord;
	}

	public final int getZ() {
		return zCoord;
	}

	public final World getWorld() {
		return worldObj;
	}

	/** Direction is relative to the piping block (so DOWN means the block is below the pipe) */
	public final boolean isConnectionValidForSide(ForgeDirection dir) {
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
	}

	public final void recomputeConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			connections[i] = this.isConnected(dirs[i]);
			world.func_147479_m(x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
		}
		this.syncAllData(true);
		world.func_147479_m(x, y, z);
	}

	public final void deleteFromAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
			if (m == ReactorTiles.TEList[this.getIndex()]) {
				TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = false;
				world.func_147479_m(dx, dy, dz);
			}
		}
	}

	public final void addToAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
			if (m == ReactorTiles.TEList[this.getIndex()]) {
				TileEntityReactorPiping te = (TileEntityReactorPiping)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;
				world.func_147479_m(dx, dy, dz);
			}
		}
	}

	private boolean isConnected(ForgeDirection dir) {
		int x = xCoord+dir.offsetX;
		int y = yCoord+dir.offsetY;
		int z = zCoord+dir.offsetZ;
		ReactorTiles m = ReactorTiles.TEList[this.getIndex()];
		ReactorTiles m2 = ReactorTiles.getTE(worldObj, x, y, z);
		if (m == m2) {
			return true;
		}
		TileEntity tile = worldObj.getTileEntity(x, y, z);
		if (tile instanceof IFluidHandler && this.isInteractableTile(tile))
			return true;
		return false;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("conn"+i, connections[i]);
		}

		ReikaNBTHelper.writeFluidToNBT(NBT, this.getFluidType());
		NBT.setInteger("level", this.getLevel());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		boolean update = false;

		boolean[] old = new boolean[connections.length];
		System.arraycopy(connections, 0, old, 0, old.length);
		for (int i = 0; i < 6; i++) {
			connections[i] = NBT.getBoolean("conn"+i);
		}
		update = !Arrays.equals(old, connections);

		Fluid f = ReikaNBTHelper.getFluidFromNBT(NBT);
		update = update || f != this.getFluidType();
		this.setFluid(f);
		this.setLevel(NBT.getInteger("level"));

		if (worldObj != null && update)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public final void removeLiquid(int toremove) {
		this.setLevel(this.getLevel()-toremove);
	}

	public final void addFluid(int toadd) {
		this.setLevel(this.getLevel()+toadd);
	}

	private final void intakeFluid(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (this.canInteractWith(world, x, y, z, dir)) {
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

				if (te instanceof TileEntityReactorPiping) {
					TileEntityReactorPiping tp = (TileEntityReactorPiping)te;
					Fluid f = tp.getFluidType();
					if (f != null) {
						int amt = tp.getLevel();
						int dL = amt-this.getLevel();
						int todrain = this.getPipeIntake(dL);
						if (todrain > 0 && this.canIntakeFluid(f)) {
							//ReikaJavaLibrary.pConsole("took in "+todrain+", had "+this.getLevel()+" here and "+amt+" in other");
							this.setFluid(f);
							this.addFluid(todrain);
							tp.removeLiquid(todrain);
							this.onIntake(te);
						}
					}
				}
				else if (te instanceof PipeConnector) {
					PipeConnector pc = (PipeConnector)te;
					Flow flow = pc.getFlowForSide(dir.getOpposite());
					if (flow.canOutput) {
						FluidStack fs = pc.drain(dir.getOpposite(), Integer.MAX_VALUE, false);
						if (fs != null) {
							int level = this.getLevel();
							int todrain = this.getPipeIntake(fs.amount-level);
							if (todrain > 0) {
								if (this.canIntakeFluid(fs.getFluid())) {
									this.addFluid(todrain);
									this.setFluid(fs.getFluid());
									pc.drain(dir.getOpposite(), todrain, true);
									this.onIntake(te);
								}
							}
						}
					}
				}
				else if (te instanceof IFluidHandler) {
					IFluidHandler fl = (IFluidHandler)te;
					FluidStack fs = fl.drain(dir.getOpposite(), Integer.MAX_VALUE, false);
					if (fs != null) {
						int level = this.getLevel();
						int todrain = this.getPipeIntake(fs.amount-level);
						if (todrain > 0) {
							if (this.canIntakeFluid(fs.getFluid())) {
								fl.drain(dir.getOpposite(), todrain, true);
								this.addFluid(todrain);
								this.setFluid(fs.getFluid());
								this.onIntake(te);
							}
						}
					}
				}
			}
		}
	}

	private final void dumpContents(World world, int x, int y, int z) {
		Fluid f = this.getFluidType();
		if (this.getLevel() <= 0 || f == null)
			return;
		for (int i = 0; i < 6; i++) {
			int level = this.getLevel();
			if (level <= 0) {
				this.setFluid(null);
				return;
			}
			ForgeDirection dir = dirs[i];
			if (this.canInteractWith(world, x, y, z, dir)) {
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

				if (te instanceof TileEntityReactorPiping) {
					TileEntityReactorPiping tp = (TileEntityReactorPiping)te;
					if (tp.canIntakeFluid(f)) {
						int otherlevel = tp.getLevel();
						int dL = level-otherlevel;
						int toadd = this.getPipeOutput(dL);
						if (toadd > 0) {
							//ReikaJavaLibrary.pConsole("dumped "+toadd+", had "+this.getLevel()+" here and "+otherlevel+" in other");
							tp.addFluid(toadd);
							this.removeLiquid(toadd);
						}
					}
				}
				else if (te instanceof PipeConnector) {
					PipeConnector pc = (PipeConnector)te;
					Flow flow = pc.getFlowForSide(dir.getOpposite());
					if (flow.canIntake) {
						int toadd = this.getPipeOutput(this.getLevel());
						//int toadd = pc.getFluidRemoval().getTransferred(this.getLiquidLevel());
						if (toadd > 0) {
							FluidStack fs = new FluidStack(f, toadd);
							int added = pc.fill(dir.getOpposite(), fs, true);
							//ReikaJavaLibrary.pConsole(added, Side.SERVER);
							if (added > 0) {
								//ReikaJavaLibrary.pConsole(toadd+":"+added+":"+this.getLiquidLevel(), Side.SERVER);
								this.removeLiquid(added);
							}
						}
					}
				}
				else if (te instanceof IFluidHandler && dir.offsetY == 0) {
					IFluidHandler fl = (IFluidHandler)te;
					if (fl.canFill(dir.getOpposite(), f)) {
						int toadd = this.getPipeOutput(this.getLevel());
						if (toadd > 0) {
							int added = fl.fill(dir.getOpposite(), new FluidStack(f, toadd), true);
							if (added > 0) {
								this.removeLiquid(added);
							}
						}
					}
				}
			}
		}
	}

	private boolean canIntakeFluid(Fluid f) {
		return this.isValidFluid(f) && (fluid == null || f.equals(fluid));
	}

	public final int getPipeIntake(int otherlevel) {
		return TransferAmount.FORCEDQUARTER.getTransferred(otherlevel);
	}

	public final int getPipeOutput(int max) {
		return Math.min(TransferAmount.FORCEDQUARTER.getTransferred(max), this.getLevel()-5);
	}

	private void setFluid(Fluid f) {
		fluid = f;
	}

	private void setLevel(int amt) {
		level = amt;
	}

	protected abstract void onIntake(TileEntity te);

	@Override
	public final boolean isFluidPipe() {
		return true;
	}

	@Override
	public IIcon getOverlayIcon() {
		return null;
	}

}
