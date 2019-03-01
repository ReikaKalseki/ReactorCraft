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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.ReactorBlock;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityLine;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;


public class TileEntityHeatPipe extends TileEntityLine {

	private int temperature;

	@Override
	public IIcon getTexture() {
		return Blocks.snow.getIcon(0, 0);
	}

	@Override
	public int getIndex() {
		return ReactorTiles.HEATPIPE.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		this.balanceHeat(world, x, y, z);

		if (this.getTicksExisted()%32 == 0) {
			int tdiff = (int)Math.signum(temperature-ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z));
			temperature -= tdiff;
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (temperature == 0)
			temperature = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
	}

	private void balanceHeat(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			TileEntity te = this.getAdjacentTileEntity(dirs[i]);
			if (te instanceof TileEntityHeatPipe) {
				TileEntityHeatPipe tile = (TileEntityHeatPipe)te;
				this.balanceWith(tile);
			}
			else if (te instanceof WorldRift) {
				WorldRift wr = (WorldRift)te;
				TileEntity tile = wr.getTileEntityFrom(dirs[i]);
				if (tile instanceof TileEntityHeatPipe) {
					TileEntityHeatPipe ts = (TileEntityHeatPipe)tile;
					this.balanceWith(ts);
				}
			}
			else if (te != null && this.canConnectToMachine(te.getBlockType(), te.getBlockMetadata(), dirs[i], te)) {
				if (te instanceof Temperatured) {
					Temperatured ts = (Temperatured)te;
					int diff = temperature-ts.getTemperature();
					if (diff <= 0 && !(te instanceof ReactorBlock))
						return;
					//ReikaJavaLibrary.pConsole(ts+" > "+diff+" @ "+temperature);
					diff = diff/4;
					int diff2 = diff;
					/*
					if (diff2 > 0 && ts instanceof TileEntityNuclearBoiler) {
						diff2 = Math.min(diff2/4, 95-ts.getTemperature());
						diff *= 2;
					}*/
					ts.setTemperature(ts.getTemperature()+diff2);
					temperature -= diff;
					/*
					if (ts instanceof TileEntityReactorBoiler && ts.getTemperature() > 300) {
						ReikaSoundHelper.playSoundAtBlock(world, te.xCoord, te.yCoord, te.zCoord, "random.fizz", 1, 1);
						world.setBlock(te.xCoord, te.yCoord, te.zCoord, Blocks.flowing_lava);
					}*/
				}
				else if (te instanceof TemperatureTE) {
					TemperatureTE ts = (TemperatureTE)te;
					int diff = temperature-ts.getTemperature();
					if (diff <= 0)
						return;
					diff = Math.max(1, diff/4);
					ts.addTemperature(diff);
					temperature -= diff;
				}
			}
		}
	}

	private void balanceWith(TileEntityHeatPipe ts) {
		int diff = ts.temperature-temperature;
		if (diff <= 0)
			return;
		diff = Math.max(1, diff/2); //no loss over distance
		ts.temperature -= diff;
		temperature += diff;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		temperature = NBT.getInteger("temp");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("temp", temperature);
	}

	@Override
	protected boolean canConnectToMachine(Block id, int meta, ForgeDirection dir, TileEntity te) {
		return (te instanceof TemperatureTE || te instanceof Temperatured) && !(te instanceof TileEntityReactorBase && ((TileEntityReactorBase)te).getMachine().getReactorType() == ReactorType.HTGR);
	}

	@Override
	public void onEntityCollided(Entity e) {
		if (temperature >= 100) {
			e.attackEntityFrom(DamageSource.inFire, temperature/100);
		}
	}

}
