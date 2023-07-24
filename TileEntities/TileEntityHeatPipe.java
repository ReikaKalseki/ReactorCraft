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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.ReactorTyped;
import Reika.ReactorCraft.Base.TileEntityLine;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Auxiliary.Interfaces.HeatConduction;


public class TileEntityHeatPipe extends TileEntityLine {

	private static final double HEAT_CAPACITY = ReikaThermoHelper.COPPER_HEAT*ReikaEngLibrary.rhoiron;

	private double heatEnergy;

	private Proportionality<ReactorType> reactorTypes = new Proportionality();

	private float renderBrightness;
	private float lastBrightness;
	private float brightnessDeltaSinceUpdate;
	private int lastUpdateTime;

	@Override
	public IIcon getTexture() {
		return Blocks.snow.getIcon(0, 0);
	}

	@Override
	public ReactorTiles getTile() {
		return ReactorTiles.HEATPIPE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote) {
			this.balanceHeat(world, x, y, z);

			if (this.getTicksExisted()%32 == 0) {
				//this.ventHeat(world, x, y, z); //TODO fix heat pipe heat loss
			}
		}
	}

	private void ventHeat(World world, int x, int y, int z) {
		double temp = getTemperatureForPipe(this, false);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		if (temp >= Tamb) {
			temp -= (temp-Tamb)/96D;
			heatEnergy = HEAT_CAPACITY*temp;
		}
		temperature = (int)temp;
	}

	public double getNetHeatEnergy() {
		return heatEnergy-ReikaWorldHelper.getAmbientTemperatureAt(worldObj, xCoord, yCoord, zCoord)*HEAT_CAPACITY;
	}

	public static double getNetTemperature(HeatConduction hc) {
		return hc.getTemperature()-hc.getAmbientTemperature();
	}

	public static double getNetHeat(HeatConduction hc) {
		return hc.heatEnergyPerDegree()*getNetTemperature(hc);
	}

	public static int getTemperatureForHeat(double heat, HeatConduction hc) {
		return (int)Math.max(1, heat/hc.heatEnergyPerDegree());
	}

	public static double getTemperatureForPipe(TileEntityHeatPipe tp, boolean net) {
		return net ? tp.getNetHeatEnergy()/HEAT_CAPACITY : tp.heatEnergy/HEAT_CAPACITY;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		heatEnergy = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)*HEAT_CAPACITY;
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
				HeatConduction hc = (HeatConduction)te;
				double theirheat = this.getNetHeat(hc);
				double ourheat = this.getNetHeatEnergy();
				double theirtemp = this.getNetTemperature(hc);
				double ourtemp = this.getTemperatureForPipe(this, true);
				boolean intake = theirheat > ourheat;
				boolean valid = intake ? hc.allowHeatExtraction() && ourtemp < theirtemp : hc.allowExternalHeating() && ourtemp > theirtemp;
				//ReikaJavaLibrary.pConsole(our+" vs "+heat+" > "+valid, Side.SERVER);
				//ReikaJavaLibrary.pConsole("our "+ourheat+", their "+theirheat+" (Ts = "+theirtemp+", "+ourtemp+")", Side.SERVER, valid && !intake);
				if (valid) {
					double diff = ourheat-theirheat; // >0 if applying heat
					diff /= 4;
					int put = this.getTemperatureForHeat(diff, hc);
					//ReikaJavaLibrary.pConsole("Adding "+put+" to "+hc, Side.SERVER, !intake);
					hc.setTemperature(put+hc.getAmbientTemperature());
					heatEnergy -= diff;
					if (diff < 0) {
						ReactorType type = null;
						if (te instanceof ReactorTyped) {
							ReactorTyped tb = (ReactorTyped)te;
							type = tb.getReactorType();
						}
						if (type != null)
							reactorTypes.addValue(type, diff);
					}
					else if (diff > 0) {
						if (te instanceof TileEntityNuclearBoiler) {
							TileEntityNuclearBoiler tb = (TileEntityNuclearBoiler)te;
							tb.setReactorTypes(reactorTypes);
						}
					}
					/*
					if (diff > 0)
						ReikaJavaLibrary.pConsole("Taking "+diff+" heat from pipe to put into "+te+" at new temp "+put+" ["+ourtemp+" -> "+theirtemp+"]");
					else
						ReikaJavaLibrary.pConsole("Taking "+(-diff)+" heat from "+te+" to put into pipe @ heat "+heatEnergy+" ["+theirtemp+" -> "+ourtemp+"]");
					 */
				}
			}
		}
	}

	private void balanceWith(TileEntityHeatPipe ts) {
		if (ts.getTicksExisted() < 2)
			return;
		double diff = ts.heatEnergy-heatEnergy;
		if (diff <= 0)
			return;
		diff = diff/2; //no loss over distance
		ts.heatEnergy -= diff;
		heatEnergy += diff;
		reactorTypes = ts.reactorTypes;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		heatEnergy = NBT.getDouble("heat");
		this.updateBrightness();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setDouble("heat", heatEnergy);
	}

	private void updateBrightness() {
		float f = this.computeBrightness();
		brightnessDeltaSinceUpdate += Math.abs(f-lastBrightness);
		renderBrightness = f;
		if (worldObj != null && (brightnessDeltaSinceUpdate >= 0.2 || this.getTicksExisted()-lastUpdateTime > 40)) {
			this.triggerBlockUpdate();
			brightnessDeltaSinceUpdate = 0;
			lastUpdateTime = this.getTicksExisted();
		}
	}

	@Override
	protected boolean canConnectToMachine(Block id, int meta, ForgeDirection dir, TileEntity te) {
		if (!(te instanceof HeatConduction))
			return false;
		HeatConduction h = (HeatConduction)te;
		return h.allowExternalHeating() || h.allowHeatExtraction();
	}

	@Override
	public void onEntityCollided(Entity e) {
		if (temperature >= 100) {
			RotaryCraft.heatDamage.lastMachine = this;
			e.attackEntityFrom(RotaryCraft.heatDamage, temperature/100);
		}
	}

	public int getRenderColor() {
		int base = 0xFFA64D;
		float f = this.getBrightness();
		return f <= 0 ? base : ReikaColorAPI.mixColors(0xff3030, base, f);
	}

	private float computeBrightness() {
		if (temperature < 250)
			return 0;
		return Math.min(1, (temperature-250)/1500F);
	}

	public float getBrightness() {
		return renderBrightness;
	}

}
