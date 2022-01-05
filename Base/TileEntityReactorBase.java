/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorRenderList;
import Reika.ReactorCraft.Auxiliary.ReactorTyped;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Auxiliary.TemperaturedReactorTyped;
import Reika.ReactorCraft.Auxiliary.TypedReactorCoreTE;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.TileEntityHeatPipe;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamLine;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.RotaryCraft.API.Interfaces.ThermalMachine;
import Reika.RotaryCraft.API.Interfaces.Transducerable;
import Reika.RotaryCraft.API.Power.ShaftMachine;
import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.Variables;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;

import li.cil.oc.api.network.Visibility;

public abstract class TileEntityReactorBase extends TileEntityBase implements RenderFetcher, Transducerable {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	protected StepTimer thermalTicker = new StepTimer(20);

	protected int temperature;
	public float phi;

	private final HashMap<Integer, LuaMethod> luaMethods = new HashMap();
	private final HashMap<String, LuaMethod> methodNames = new HashMap();

	public final TextureFetcher getRenderer() {
		if (ReactorTiles.TEList[this.getIndex()].hasRender())
			return ReactorRenderList.getRenderForMachine(ReactorTiles.TEList[this.getIndex()]);
		else
			return null;
	}

	@Override
	public final boolean allowTickAcceleration() {
		return ReactorTiles.TEList[this.getIndex()].allowTickAcceleration();
	}

	@Override
	public final boolean canUpdate() {
		return !ReactorCraft.instance.isLocked() && this.isTickingTE();
	}

	protected boolean isTickingTE() {
		return true;
	}

	@Override
	public final Block getTileEntityBlockID() {
		return ReactorTiles.TEList[this.getIndex()].getBlock();
	}

	public final ReactorTiles getMachine() {
		return ReactorTiles.TEList[this.getIndex()];
	}

	@Override
	protected String getTEName() {
		return ReactorTiles.TEList[this.getIndex()].getName();
	}

	public abstract int getIndex();

	public int getTextureState(ForgeDirection side) {
		return 0;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("temp", temperature);

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		temperature = NBT.getInteger("temp");

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

	}

	public boolean isThisTE(Block id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		ReactorTiles r = ReactorTiles.TEList[this.getIndex()];
		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
	}

	protected void updateTemperature(World world, int x, int y, int z) {
		//ReikaJavaLibrary.pConsole(temperature, Side.SERVER);
		float af = 1+1.5F*MathHelper.clamp_float((temperature-100)/500F, 0, 1);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z, af);

		if (world.provider.dimensionId != -1)
			Tamb = Math.min(Tamb, 95);

		int dT = Tamb-temperature;
		if (dT != 0) {
			int d = ReikaWorldHelper.isExposedToAir(world, x, y, z) ? 32 : 64;
			int diff = (1+dT/d);
			if (diff <= 1)
				diff = dT/Math.abs(dT);
			temperature += diff;
		}

		ReikaWorldHelper.temperatureEnvironment(world, x, y, z, Math.min(temperature, 1000));

		if (this instanceof TileEntityReactorBoiler && temperature >= 300 && Tamb > 100) {
			if (!((TileEntityReactorBoiler)this).tank.isEmpty()) {
				world.setBlockToAir(x, y, z);
				world.createExplosion(null, x+0.5, y+0.5, z+0.5, 3F, true);
			}
		}

		ReactorTiles src = this.getMachine();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			if (r != null) {
				TileEntityReactorBase te = (TileEntityReactorBase)world.getTileEntity(dx, dy, dz);
				if (te instanceof Temperatured) {
					int Tamb_loc = ReikaWorldHelper.getAmbientTemperatureAt(world, dx, dy, dz);

					Temperatured tr = (Temperatured)te;
					boolean flag = true;/*
					if (src == ReactorTiles.COOLANT) {
						TileEntityWaterCell wc = (TileEntityWaterCell)this;
						flag = tr.canDumpHeatInto(wc.getLiquidState());
					}*/
					if (tr instanceof TileEntityNuclearCore)
						flag = true;
					if (flag) {
						int T = tr.getTemperature();
						dT = (T-temperature)-Math.max(0, (Tamb-Tamb_loc)); //if Tamb here is > Tamb there, subtract that difference to avoid exploits
						float f = te.getHeatThroughput(this);
						//ReikaJavaLibrary.pConsole(te.getMachine()+" > "+this.getMachine()+" = "+f);
						dT *= f;
						if (dT > 0) {
							int d = this.getHeatFraction(te);
							//ReikaJavaLibrary.pConsole(te.getMachine()+" > "+this.getMachine()+" = "+d);
							int newT = T-dT/d;
							//ReikaJavaLibrary.pConsole(temperature+":"+T+" "+this.getTEName()+":"+te.getTEName()+"->"+(temperature+dT/4D)+":"+newT, this instanceof TileEntityWaterCell && FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER);
							float e = te.getHeatEfficiency(this);
							//ReikaJavaLibrary.pConsole(te.getMachine()+" > "+this.getMachine()+" = "+e);
							double add = dT/d*e;
							temperature += add;
							tr.setTemperature(newT);
							if (this instanceof TileEntityReactorBoiler && !(tr instanceof TileEntityReactorBoiler) && tr instanceof TypedReactorCoreTE) {
								((TileEntityReactorBoiler)this).setReactorType(((TypedReactorCoreTE)tr).getReactorType(), add);
							}
							else if (this instanceof TileEntityNuclearBoiler && tr.getClass() == this.getClass()) {
								((TileEntityNuclearBoiler)this).setReactorType(((TileEntityNuclearBoiler)tr).getReactorType(), add);
							}
						}
					}
				}
				/*
				if (r == ReactorTiles.CO2HEATER || r == ReactorTiles.PEBBLEBED) {
					if (src.getReactorType() != ReactorType.HTGR && temperature > Tamb) {
						temperature -= Math.max(1, (temperature-Tamb)/2);
					}
				}
				 */
			}
		}
	}

	private int getHeatFraction(TileEntityReactorBase other) {
		return other instanceof TemperaturedReactorTyped ? this.getHeatConductionFraction((TemperaturedReactorTyped)other) : 4;
	}

	private float getHeatThroughput(TileEntityReactorBase other) {
		return other instanceof TemperaturedReactorTyped ? this.getHeatConductionThroughput((TemperaturedReactorTyped)other) : 1;
	}

	private float getHeatEfficiency(TileEntityReactorBase other) {
		return other instanceof TemperaturedReactorTyped ? this.getHeatConductionEfficiency((TemperaturedReactorTyped)other) : 0;
	}

	/** For transferring heat FROM that reactor block. */
	protected int getHeatConductionFraction(TemperaturedReactorTyped other) {
		return 4;
	}

	/** For transferring heat TO that reactor block. */
	protected float getHeatConductionThroughput(TemperaturedReactorTyped other) {
		return 1;
	}

	/** For transferring heat TO that reactor block. */
	protected float getHeatConductionEfficiency(TemperaturedReactorTyped other) {
		ReactorTiles r0 = other.getMachine();
		if (r0 == ReactorTiles.CONTROL || r0 == ReactorTiles.CPU)
			return this.getControlCPUHeatEfficiency();
		ReactorType r1 = this instanceof ReactorTyped ? ((ReactorTyped)this).getReactorType() : null;
		ReactorType r2 = other.getReactorType();
		if (r1 == r2)
			return 1;
		if (r1 == null || r2 == null) //one tile is not even a reactor
			return 0;
		return r1.getTypeMismatchHeatEfficiency();
	}

	protected float getControlCPUHeatEfficiency() {
		return 1;
	}

	protected float getTypeMismatchEfficiency() {
		return 0.5F;
	}

	public ForgeDirection getRandomDirection(boolean allowVertical) {
		int r = allowVertical ? rand.nextInt(6) : 2+rand.nextInt(4);
		return dirs[r];
	}

	public final ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		if (this instanceof Temperatured) {
			String s = String.format("%s %s: %dC", this.getTEName(), Variables.TEMPERATURE, ((Temperatured)this).getTemperature());
			li.add(s);
		}
		else if (this instanceof ThermalMachine) {
			String s = String.format("%s %s: %dC", this.getTEName(), Variables.TEMPERATURE, ((ThermalMachine)this).getTemperature());
			//li.add(s);
		}
		else if (this instanceof TemperatureTE) {
			String s = String.format("%s %s: %dC", this.getTEName(), Variables.TEMPERATURE, ((TemperatureTE)this).getTemperature());
			li.add(s);
		}
		if (this instanceof TileEntityReactorPiping) {
			TileEntityReactorPiping rp = (TileEntityReactorPiping)this;
			if (rp.getLevel() <= 0) {
				String s = String.format("%s is empty.", this.getTEName());
				li.add(s);
			}
			else {
				String s = String.format("%s contains %d mB of %s", this.getTEName(), rp.getLevel(), rp.getFluidType().getLocalizedName());
				li.add(s);
			}
		}
		if (this instanceof TileEntitySolenoidMagnet) {
			ShaftPowerReceiver sp = (ShaftPowerReceiver)this;
			String pre = ReikaEngLibrary.getSIPrefix(sp.getPower());
			double base = ReikaMathLibrary.getThousandBase(sp.getPower());
			li.add(String.format("%s receiving %.3f %sW @ %d rad/s.", sp.getName(), base, pre, sp.getOmega()));
		}
		if (this instanceof TileEntityReactorGenerator) {
			TileEntityReactorGenerator sp = (TileEntityReactorGenerator)this;
			li.add(sp.getGeneratedOutputForDisplay());
		}
		if (this instanceof TileEntityTurbineCore) {
			TileEntityTurbineCore sp = (TileEntityTurbineCore)this;
			long power = sp.getPower();
			String pre = ReikaEngLibrary.getSIPrefix(power);
			double base = ReikaMathLibrary.getThousandBase(power);
			li.add(String.format("%s producing %.3f %sW @ %d rad/s.", sp.getName(), base, pre, sp.getOmega()));
			li.add(String.format("Lubricant level %d mB per block.", sp.getLubricant()));
		}
		if (this instanceof TileEntitySteamLine) {
			TileEntitySteamLine sl = (TileEntitySteamLine)this;
			String s = String.format("%s contains %d m^3 of steam.", this.getTEName(), sl.getSteam());
			li.add(s);
			Proportionality<ReactorType> types = sl.getSourceReactorType();
			if (!types.isEmpty()) {
				li.add( "Reactor source types: ");
				for (ReactorType r : types.getElements()) {
					double frac = types.getFraction(r);
					li.add("  "+r+": "+frac*100+"%%");
				}
			}
		}
		if (this instanceof TileEntityHeatPipe) {
			TileEntityHeatPipe hp = (TileEntityHeatPipe)this;
			double e = hp.getNetHeatEnergy();
			String s = String.format("%s contains %.3f%sJ of heat energy.", this.getTEName(), ReikaMathLibrary.getThousandBase(e), ReikaEngLibrary.getSIPrefix(e));
			li.add(s);
		}
		return li;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final Visibility getOCNetworkVisibility() {
		return this.getMachine().isPipe() || this.getMachine() == ReactorTiles.REFLECTOR ? Visibility.None : Visibility.Network;
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	public boolean allowExternalHeating() {
		if (this instanceof ReactorTyped) {
			ReactorType r = ((ReactorTyped)this).getReactorType();
			return r != ReactorType.HTGR && r != ReactorType.FUSION;
		}
		return true;
	}

	public boolean allowHeatExtraction() {
		return true;
	}

	public boolean canBeCooledWithFins() {
		return false;
	}

	public double heatEnergyPerDegree() {
		double base = ReikaThermoHelper.STEEL_HEAT*ReikaBlockHelper.getBlockVolume(worldObj, xCoord, yCoord, zCoord)*ReikaEngLibrary.rhoiron;
		if (this.getMachine().isReactorCore() || this.getMachine() == ReactorTiles.EXCHANGER)
			base *= 50;
		return base;
	}
}
