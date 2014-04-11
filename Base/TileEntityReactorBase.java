/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.Auxiliary.ReactorRenderList;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityReactorBoiler;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.API.ThermalMachine;
import Reika.RotaryCraft.API.Transducerable;
import Reika.RotaryCraft.Auxiliary.Variables;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public abstract class TileEntityReactorBase extends TileEntityBase implements RenderFetcher, Transducerable, IPeripheral, SimpleComponent, ManagedPeripheral {

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
	public int getTileEntityBlockID() {
		return ReactorTiles.TEList[this.getIndex()].getBlockID();
	}

	public ReactorTiles getMachine() {
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

		NBT.setFloat("ang", phi);

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		temperature = NBT.getInteger("temp");

		phi = NBT.getFloat("ang");

	}

	public boolean isThisTE(int id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	public final String getName() {
		return this.getTEName();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		ReactorTiles r = ReactorTiles.TEList[this.getIndex()];
		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
	}

	protected void updateTemperature(World world, int x, int y, int z) {
		//ReikaJavaLibrary.pConsole(temperature, Side.SERVER);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		int dT = Tamb-temperature;
		if (dT != 0 && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, 0) != null) {
			int diff = (1+dT/32);
			if (diff <= 1)
				diff = dT/Math.abs(dT);
			temperature += diff;
		}
		if (this instanceof TileEntityReactorBoiler && temperature >= 300 && Tamb > 100) {
			if (!((TileEntityReactorBoiler)this).tank.isEmpty()) {
				world.setBlock(x, y, z, 0);
				world.createExplosion(null, x+0.5, y+0.5, z+0.5, 3F, true);
			}
		}

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			ReactorTiles src = this.getMachine();
			if (r != null) {
				TileEntityReactorBase te = (TileEntityReactorBase)world.getBlockTileEntity(dx, dy, dz);
				if (te instanceof Temperatured) {
					Temperatured tr = (Temperatured)te;
					boolean flag = true;
					if (src == ReactorTiles.COOLANT) {
						TileEntityWaterCell wc = (TileEntityWaterCell)this;
						flag = tr.canDumpHeatInto(wc.getLiquidState());
					}
					if (tr instanceof TileEntityNuclearCore)
						flag = true;
					if (flag) {
						int T = tr.getTemperature();
						dT = T-temperature;
						if (dT > 0) {
							int newT = T-dT/4;
							//ReikaJavaLibrary.pConsole(temperature+":"+T+" "+this.getTEName()+":"+te.getTEName()+"->"+(temperature+dT/4D)+":"+newT, this instanceof TileEntityWaterCell && FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER);
							temperature += dT/4;
							tr.setTemperature(newT);
						}
					}
				}
				if ((r == ReactorTiles.BREEDER || r == ReactorTiles.CO2HEATER || r == ReactorTiles.PEBBLEBED) && src == ReactorTiles.BOILER) {
					if (temperature >= 100) {
						world.setBlock(x, y, z, 0);
						world.createExplosion(null, x+0.5, y+0.5, z+0.5, 3F, true);
					}
				}
			}
		}
	}

	public ForgeDirection getRandomDirection() {
		int r = 2+rand.nextInt(4);
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
				String s = String.format("%s contains %d mB of %s", this.getTEName(), rp.getLevel(), rp.getLiquidType().getLocalizedName());
				li.add(s);
			}
		}
		if (this instanceof TileEntitySolenoidMagnet) {
			ShaftPowerReceiver sp = (ShaftPowerReceiver)this;
			String pre = ReikaEngLibrary.getSIPrefix(sp.getPower());
			double base = ReikaMathLibrary.getThousandBase(sp.getPower());
			li.add(String.format("%s receiving %.3f %sW @ %d rad/s.", sp.getName(), base, pre, sp.getOmega()));
		}
		return li;
	}

	@Override
	public int getPacketDelay() {
		return DragonAPICore.isSinglePlayer() ? 1 : Math.min(20, ConfigRegistry.PACKETDELAY.getValue());
	}


	/** ComputerCraft */
	@Override
	public final String[] getMethodNames() {
		ArrayList<LuaMethod> li = new ArrayList();
		List<LuaMethod> all = LuaMethod.getMethods();
		for (int i = 0; i < all.size(); i++) {
			LuaMethod l = all.get(i);
			if (l.isValidFor(this))
				li.add(l);
		}
		String[] s = new String[li.size()];
		for (int i = 0; i < s.length; i++) {
			LuaMethod l = li.get(i);
			s[i] = l.displayName;
			luaMethods.put(i, l);
			methodNames.put(l.displayName, l);
		}
		return s;
	}

	@Override
	public final Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		return luaMethods.containsKey(method) ? luaMethods.get(method).invoke(this, arguments) : null;
	}

	@Override
	public final boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public final void attach(IComputerAccess computer) {

	}

	@Override
	public final void detach(IComputerAccess computer) {

	}

	@Override
	public final String getType() {
		return this.getName().replaceAll(" ", "");
	}

	/** OpenComputers */
	@Override
	public final String getComponentName() {
		return this.getName().replaceAll(" ", "");
	}

	@Override
	public final String[] methods() {
		return this.getMethodNames();
	}

	@Override
	public final Object[] invoke(String method, Context context, Arguments args) throws Exception {
		Object[] objs = new Object[args.count()];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = args.checkAny(i);
		}
		return methodNames.containsKey(method) ? methodNames.get(method).invoke(this, objs) : null;
	}
}
