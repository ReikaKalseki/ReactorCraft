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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.RenderableDuct;
import Reika.RotaryCraft.Auxiliary.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.TransferAmount;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityGasDuct extends TileEntityReactorBase implements RenderableDuct {

	private Fluid fluid;
	private int level;

	private boolean[] connections = new boolean[6];

	@Override
	public int getIndex() {
		return ReactorTiles.GASPIPE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.intakeFluid(world, x, y, z);
		this.dumpContents(world, x, y, z);
		if (this.getLevel() <= 0) {
			this.setLevel(0);
			this.setFluid(null);
		}
	}

	protected final boolean canInteractWith(World world, int x, int y, int z, ForgeDirection side) {
		if (!connections[side.ordinal()])
			return false;
		int dx = x+side.offsetX;
		int dy = y+side.offsetY;
		int dz = z+side.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == 0)
			return false;
		ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
		if (m == this.getMachine())
			return true;
		return Block.blocksList[id].hasTileEntity(meta);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

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

	@Override
	public Icon getBlockIcon() {
		return Block.hardenedClay.getIcon(1, 0);
	}

	@Override
	public Fluid getLiquidType() {
		return fluid;
	}

	public int getLevel() {
		return level;
	}

	/** Direction is relative to the piping block (so DOWN means the block is below the pipe) */
	public boolean isConnectionValidForSide(ForgeDirection dir) {
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
	}

	public void recomputeConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			connections[i] = this.isConnected(dirs[i]);
			world.markBlockForRenderUpdate(x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
		}
		world.markBlockForRenderUpdate(x, y, z);
	}

	public void deleteFromAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
			if (m == ReactorTiles.TEList[this.getIndex()]) {
				TileEntityGasDuct te = (TileEntityGasDuct)world.getBlockTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = false;
				world.markBlockForRenderUpdate(dx, dy, dz);
			}
		}
	}

	public void addToAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
			if (m == ReactorTiles.TEList[this.getIndex()]) {
				TileEntityGasDuct te = (TileEntityGasDuct)world.getBlockTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;
				world.markBlockForRenderUpdate(dx, dy, dz);
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
		TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
		if (tile instanceof PipeConnector)
			return ((PipeConnector)tile).canConnectToPipeOnSide(MachineRegistry.PIPE, dir.getOpposite());
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("conn"+i, connections[i]);
		}

		ReikaNBTHelper.writeFluidToNBT(NBT, this.getLiquidType());
		NBT.setInteger("level", this.getLevel());
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		for (int i = 0; i < 6; i++) {
			connections[i] = NBT.getBoolean("conn"+i);
		}

		this.setFluid(ReikaNBTHelper.getFluidFromNBT(NBT));
		this.setLevel(NBT.getInteger("level"));
	}

	private void setFluid(Fluid f) {
		fluid = f;
	}

	private void setLevel(int amt) {
		level = amt;
	}

	public boolean isConnectedToNonSelf(ForgeDirection dir) {
		if (!this.isConnectionValidForSide(dir))
			return false;
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		int dx = xCoord+dir.offsetX;
		int dy = yCoord+dir.offsetY;
		int dz = zCoord+dir.offsetZ;
		World world = worldObj;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		return id != this.getMachine().getBlockID() || meta != this.getMachine().getBlockMetadata();
	}

	public boolean isValidFluid(Fluid f) {
		return f.isGaseous();
	}

	public void dumpContents(World world, int x, int y, int z) {
		Fluid f = this.getLiquidType();
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
				TileEntity te = world.getBlockTileEntity(dx, dy, dz);
				if (te instanceof TileEntityGasDuct) {
					TileEntityGasDuct tp = (TileEntityGasDuct)te;
					if (tp.canIntakeFluid(f)) {
						int otherlevel = tp.getLevel();
						int dL = level-otherlevel;
						int toadd = this.getPipeOutput(dL);
						if (toadd > 0) {
							this.addFluid(toadd);
							tp.removeLiquid(toadd);
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

	public final void removeLiquid(int toremove) {
		this.setLevel(this.getLevel()-toremove);
	}

	public final void addFluid(int toadd) {
		this.setLevel(this.getLevel()+toadd);
	}

	public void intakeFluid(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (this.canInteractWith(world, x, y, z, dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				TileEntity te = world.getBlockTileEntity(dx, dy, dz);
				if (te instanceof TileEntityGasDuct) {
					TileEntityGasDuct tp = (TileEntityGasDuct)te;
					Fluid f = tp.getLiquidType();
					int amt = tp.getLevel();
					int dL = amt-this.getLevel();
					int todrain = this.getPipeIntake(dL);
					if (todrain > 0 && this.canIntakeFluid(f)) {
						this.setFluid(f);
						this.addFluid(todrain);
						tp.removeLiquid(todrain);
						this.onIntake(te);
					}
				}
				else if (te instanceof IFluidHandler && dir.offsetY != 0) {
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

	public final int getPipeIntake(int otherlevel) {
		return TransferAmount.FORCEDQUARTER.getTransferred(otherlevel);
	}

	public final int getPipeOutput(int max) {
		return Math.min(TransferAmount.FORCEDQUARTER.getTransferred(max), this.getLevel()-5);
	}

	private boolean canIntakeFluid(Fluid f) {
		return this.isValidFluid(f) && (fluid == null || f.equals(fluid));
	}

	private void onIntake(TileEntity te) {

	}

	@Override
	public Block getPipeBlockType() {
		return Block.hardenedClay;
	}

}
