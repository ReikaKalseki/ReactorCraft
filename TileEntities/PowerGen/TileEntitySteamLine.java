/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.ReactorCraft.Auxiliary.SteamTile;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.ReactorCraft.TileEntities.TileEntitySteamDiffuser;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeRenderConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.PumpablePipe;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Auxiliary.TileEntityPipePump;

public class TileEntitySteamLine extends TileEntityReactorBase implements PumpablePipe, SteamTile {

	//private double storedEnergy;
	private int steam;

	private boolean[] connections = new boolean[6];

	private WorkingFluid fluid = WorkingFluid.EMPTY;
	private Proportionality<ReactorType> source = new Proportionality();

	@Override
	public int getIndex() {
		return ReactorTiles.STEAMLINE.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.drawFromBoiler(world, x, y, z);
		this.getPipeSteam(world, x, y, z);

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
		}
		//steam += 1000;
		//fluid = WorkingFluid.AMMONIA;
		//steam = 0;
		//ReikaJavaLibrary.pConsole(steam+":"+fluid, Side.SERVER);
	}

	private void drawFromBoiler(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		if (r == ReactorTiles.BOILER) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getTileEntity(x, y-1, z);
			if (this.canTakeInWorkingFluid(te.getWorkingFluid())) {
				fluid = te.getWorkingFluid();
				int s = te.removeSteam();
				steam += s;
				source.addValue(te.getReactorType(), s);
			}
		}
	}

	private boolean canTakeInWorkingFluid(WorkingFluid f) {
		if (f == WorkingFluid.EMPTY)
			return false;
		if (fluid == WorkingFluid.EMPTY)
			return true;
		if (fluid == f)
			return true;
		return false;
	}

	private void getPipeSteam(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);
			if (te instanceof TileEntitySteamLine) {
				TileEntitySteamLine tile = (TileEntitySteamLine)te;
				if (this.canTakeInWorkingFluid(tile.fluid))
					this.readPipe(tile);
			}
			else if (te instanceof WorldRift) {
				WorldRift wr = (WorldRift)te;
				TileEntity tile = wr.getTileEntityFrom(dir);
				if (tile instanceof TileEntitySteamLine) {
					TileEntitySteamLine ts = (TileEntitySteamLine)tile;
					if (this.canTakeInWorkingFluid(ts.fluid))
						this.readPipe(ts);
				}
			}
		}
	}

	private void readPipe(TileEntitySteamLine te) {
		int dS = te.steam-steam;
		if (dS > 0) {
			//ReikaJavaLibrary.pConsole(steam+":"+te.steam);
			steam += dS/2+1;
			te.steam -= dS/2+1;
			fluid = te.fluid;
			source = te.source;
		}
	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block id = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == this.getTileEntityBlockID() && meta == ReactorTiles.STEAMLINE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.BOILER.getBlock() && meta == ReactorTiles.BOILER.getBlockMetadata() && dir == ForgeDirection.DOWN)
			return true;
		if (id == ReactorTiles.GRATE.getBlock() && meta == ReactorTiles.GRATE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.BIGTURBINE.getBlock() && meta == ReactorTiles.BIGTURBINE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.DIFFUSER.getBlock() && meta == ReactorTiles.DIFFUSER.getBlockMetadata()) {
			boolean flag = ((TileEntitySteamDiffuser)this.getAdjacentTileEntity(dir)).getFacing().getOpposite() == dir;
			return flag;
		}
		if (id == MachineRegistry.PIPEPUMP.getBlock() && meta == MachineRegistry.PIPEPUMP.getBlockMetadata()) {
			boolean flag = ((TileEntityPipePump)this.getAdjacentTileEntity(dir)).canConnectToPipeOnSide(dir);
			return flag;
		}
		if (world.getTileEntity(dx, dy, dz) instanceof WorldRift)
			return true;
		return false;
	}

	public int getSteam() {
		return steam;
	}

	public void removeSteam(int amt) {
		steam -= amt;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		steam = NBT.getInteger("energy");

		fluid = WorkingFluid.getFromNBT(NBT);

		for (int i = 0; i < 6; i++) {
			connections[i] = NBT.getBoolean("conn"+i);
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("energy", steam);

		fluid.saveToNBT(NBT);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("conn"+i, connections[i]);
		}
	}

	public WorkingFluid getWorkingFluid() {
		return fluid;
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
			world.func_147479_m(x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
		}
		world.func_147479_m(x, y, z);
		//ReikaJavaLibrary.pConsole(Arrays.toString(connections), Side.SERVER);
	}

	public void deleteFromAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ReactorTiles m = ReactorTiles.getTE(world, dx, dy, dz);
			if (m == this.getMachine()) {
				TileEntitySteamLine te = (TileEntitySteamLine)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = false;
				world.func_147479_m(dx, dy, dz);
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
			if (m == this.getMachine()) {
				TileEntitySteamLine te = (TileEntitySteamLine)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;
				world.func_147479_m(dx, dy, dz);
			}
		}
	}

	private boolean isConnected(ForgeDirection dir) {
		int x = xCoord+dir.offsetX;
		int y = yCoord+dir.offsetY;
		int z = zCoord+dir.offsetZ;
		ReactorTiles m = this.getMachine();
		ReactorTiles m2 = ReactorTiles.getTE(worldObj, x, y, z);
		if (m == m2)
			return true;
		TileEntity te = worldObj.getTileEntity(x, y, z);
		if (te instanceof PipeRenderConnector)
			return ((PipeRenderConnector)te).canConnectToPipeOnSide(dir);
		else if (te instanceof WorldRift)
			return true;
		return false;
	}

	@Override
	public int getPacketDelay() {
		return 4*super.getPacketDelay();
	}

	@Override
	public boolean canTransferTo(PumpablePipe p, ForgeDirection dir) {
		if (p instanceof TileEntitySteamLine) {
			WorkingFluid f = ((TileEntitySteamLine)p).fluid;
			return f != WorkingFluid.EMPTY ? f == fluid : true;
		}
		return false;
	}

	@Override
	public int getFluidLevel() {
		return this.getSteam();
	}

	@Override
	public void transferFrom(PumpablePipe from, int amt) {
		((TileEntitySteamLine)from).steam -= amt;
		fluid = ((TileEntitySteamLine)from).fluid;
		steam += amt;
		source = ((TileEntitySteamLine)from).source;
	}

	public Proportionality<ReactorType> getSourceReactorType() {
		return source;
	}
}
