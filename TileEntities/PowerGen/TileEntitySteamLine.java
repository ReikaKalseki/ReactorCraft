/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.PowerGen;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.SteamTile;
import Reika.ReactorCraft.Base.TileEntityLine;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.Registry.WorkingFluid;
import Reika.ReactorCraft.TileEntities.TileEntitySteamDiffuser;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.RotaryCraft.API.Interfaces.PressureTile;
import Reika.RotaryCraft.Auxiliary.Interfaces.PumpablePipe;
import Reika.RotaryCraft.Registry.MachineRegistry;
import Reika.RotaryCraft.TileEntities.Auxiliary.TileEntityPipePump;

public class TileEntitySteamLine extends TileEntityLine implements PumpablePipe, SteamTile, PressureTile {

	private int steam;

	private WorkingFluid fluid = WorkingFluid.EMPTY;
	private Proportionality<ReactorType> source = new Proportionality();

	@Override
	public int getIndex() {
		return ReactorTiles.STEAMLINE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		this.drawFromBoiler(world, x, y, z);
		this.getPipeSteam(world, x, y, z);

		if (steam <= 0) {
			fluid = WorkingFluid.EMPTY;
			source.clear();
		}
		else if (this.getPressure() > this.getMaxPressure()) {
			this.delete();
			world.createExplosion(null, x+0.5, y+0.5, z+0.5, 2, true);
		}
	}

	@Override
	protected boolean canConnectToMachine(Block id, int meta, ForgeDirection dir, TileEntity te) {
		if (id == ReactorTiles.BOILER.getBlock() && meta == ReactorTiles.BOILER.getBlockMetadata() && dir == ForgeDirection.DOWN)
			return true;
		if (id == ReactorTiles.GRATE.getBlock() && meta == ReactorTiles.GRATE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.BIGTURBINE.getBlock() && meta == ReactorTiles.BIGTURBINE.getBlockMetadata())
			return true;
		if (id == ReactorTiles.DIFFUSER.getBlock() && meta == ReactorTiles.DIFFUSER.getBlockMetadata()) {
			return ((TileEntitySteamDiffuser)this.getAdjacentTileEntity(dir)).getFacing().getOpposite() == dir;
		}
		if (id == MachineRegistry.PIPEPUMP.getBlock() && meta == MachineRegistry.PIPEPUMP.getBlockMetadata()) {
			return ((TileEntityPipePump)this.getAdjacentTileEntity(dir)).canConnectToPipeOnSide(dir);
		}
		return false;
	}

	private void drawFromBoiler(World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		if (r == ReactorTiles.BOILER) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getTileEntity(x, y-1, z);
			if (te.getTileEntityAge() > 5 && this.canTakeInWorkingFluid(te.getWorkingFluid())) {
				fluid = te.getWorkingFluid();
				int s = te.removeSteam();
				steam += s;
				for (ReactorType rt : te.getReactorTypeSet()) {
					double f = te.getReactorTypeFraction(rt);
					if (rt == null || rt == ReactorType.NONE)
						rt = te.getReactorType();
					if (rt == null || rt == ReactorType.NONE)
						rt = te.getDefaultReactorType();
					source.addValue(rt, s*f);
				}
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
			TileEntity te = this.getAdjacentTileEntity(dirs[i]);
			if (te instanceof TileEntitySteamLine) {
				TileEntitySteamLine tile = (TileEntitySteamLine)te;
				if (this.canTakeInWorkingFluid(tile.fluid))
					this.readPipe(tile);
			}
			else if (te instanceof WorldRift && !world.isRemote) {
				WorldRift wr = (WorldRift)te;
				TileEntity tile = wr.getTileEntityFrom(dirs[i]);
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
			int amt = dS/2+1;
			float frac = amt/(float)te.steam;
			steam += amt;
			te.steam -= amt;
			fluid = te.fluid;
			this.addSources(te, frac);
		}
	}

	@Override
	public int getSteam() {
		return steam;
	}

	public void removeSteam(int amt) {
		steam -= amt;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		steam = NBT.getInteger("energy");

		fluid = WorkingFluid.getFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("energy", steam);

		fluid.saveToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		source.readFromNBT(NBT.getCompoundTag("sources"), (NBTIO<ReactorType>)ReikaNBTHelper.getEnumConverter(ReactorType.class));
		if (source.removeValue(null) > 0) {
			ReactorCraft.logger.logError(this+" loaded null-containing steam type map from NBT: "+NBT);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound tag = new NBTTagCompound();
		source.writeToNBT(tag, (NBTIO<ReactorType>)ReikaNBTHelper.getEnumConverter(ReactorType.class));
		NBT.setTag("sources", tag);
	}

	public WorkingFluid getWorkingFluid() {
		return fluid;
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
		float frac = (float)amt/((TileEntitySteamLine)from).steam;
		((TileEntitySteamLine)from).steam -= amt;
		fluid = ((TileEntitySteamLine)from).fluid;
		steam += amt;
		this.addSources((TileEntitySteamLine)from, frac);
	}

	private void addSources(TileEntitySteamLine from, float frac) {
		for (ReactorType r : new ArrayList<ReactorType>(from.source.getElements())) {
			if (r == null)
				continue;
			double val = from.source.getValue(r)*frac;
			if (Double.isNaN(val) || Double.isInfinite(val))
				continue;
			source.addValue(r, val);
			from.source.addValue(r, -val);
		}
	}

	public Proportionality<ReactorType> getSourceReactorType() {
		return source.copy();
	}

	@Override
	public IIcon getTexture() {
		return Blocks.wool.getIcon(0, this.isInWorld() ? 15 : 7);
	}

	@Override
	public int getPressure() {
		return steam;
	}

	@Override
	public int getMaxPressure() {
		return ReactorOptions.STEAMLINECAP.getValue();
	}
}
