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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.ReactorCraft.Auxiliary.ReactorRenderList;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;
import Reika.RotaryCraft.API.ShaftMachine;

public abstract class TileEntityReactorBase extends TileEntityBase implements RenderFetcher {

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
}
