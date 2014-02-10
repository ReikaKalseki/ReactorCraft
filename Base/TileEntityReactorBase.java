/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.ArrayList;

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
import Reika.ReactorCraft.Auxiliary.ReactorRenderList;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
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

public abstract class TileEntityReactorBase extends TileEntityBase implements RenderFetcher, Transducerable, IPeripheral {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	protected StepTimer thermalTicker = new StepTimer(20);

	protected int temperature;
	public float phi;

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
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("temp", temperature);

		NBT.setFloat("ang", phi);

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

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
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
			ReactorTiles src = ReactorTiles.TEList[this.getIndex()];
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
						int dT = T-temperature;
						if (dT > 0) {
							int newT = T-dT/4;
							//ReikaJavaLibrary.pConsole(temperature+":"+T+" "+this.getTEName()+":"+te.getTEName()+"->"+(temperature+dT/4D)+":"+newT, this instanceof TileEntityWaterCell && FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER);
							temperature += dT/4;
							tr.setTemperature(newT);
						}
					}
				}
			}
		}
	}

	public ForgeDirection getRandomDirection() {
		int r = 2+rand.nextInt(4);
		return dirs[r];
	}

	public ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		if (this instanceof Temperatured) {
			String s = String.format("%s %s: %dC", this.getTEName(), Variables.TEMPERATURE, ((Temperatured)this).getTemperature());
			li.add(s);
		}
		else if (this instanceof ThermalMachine) {
			String s = String.format("%s %s: %dC", this.getTEName(), Variables.TEMPERATURE, ((TemperatureTE)this).getTemperature());
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
	public final int getPacketDelay() {
		return DragonAPICore.isSinglePlayer() ? 1 : Math.min(20, ConfigRegistry.PACKETDELAY.getValue());
	}

	@Override
	public String[] getMethodNames() {
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return false;
	}

	@Override
	public void attach(IComputerAccess computer) {

	}

	@Override
	public void detach(IComputerAccess computer) {

	}

	@Override
	public String getType() {
		return this.getName().replaceAll(" ", "");
	}
}
