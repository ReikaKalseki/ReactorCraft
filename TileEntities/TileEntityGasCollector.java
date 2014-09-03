/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Auxiliary.ItemMaterialController;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.ItemMaterial;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityGasCollector extends TileEntityReactorBase implements IFluidHandler {

	private HybridTank tank = new HybridTank("co2collector", 1000);

	private int readx;
	private int ready;
	private int readz;

	public int ticks = 512;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (ticks > 0)
			ticks -= 8;

		this.getIOSides(world, x, y, z, meta);
		Block id = world.getBlock(readx, ready, readz);
		if (id == Blocks.lit_furnace) {
			TileEntityFurnace te = (TileEntityFurnace)world.getTileEntity(readx, ready, readz);
			ItemStack fuel = te.getStackInSlot(1);
			if (fuel != null) {
				ItemMaterial mat = ItemMaterialController.instance.getMaterial(fuel);
				if (mat == ItemMaterial.COAL || mat == ItemMaterial.WOOD)
					tank.addLiquid(10, FluidRegistry.getFluid("rc co2"));
			}
		}
		//ReikaJavaLibrary.pConsole(id+":"+tank, Side.SERVER);
	}

	public int[] getTarget() {
		return new int[]{readx, ready, readz};
	}

	public boolean hasFurnace() {
		Block id = worldObj.getBlock(readx, ready, readz);
		return id == Blocks.furnace || id == Blocks.lit_furnace;
	}

	private void getIOSides(World world, int x, int y, int z, int meta) {
		switch(meta) {
		case 5:
			readx = x+1;
			readz = z;
			ready = y;
			break;
		case 3:
			readx = x-1;
			readz = z;
			ready = y;
			break;
		case 2:
			readz = z-1;
			readx = x;
			ready = y;
			break;
		case 4:
			readz = z+1;
			readx = x;
			ready = y;
			break;
		case 0:
			readx = x;
			readz = z;
			ready = y-1;
			break;
		case 1:
			readx = x;
			readz = z;
			ready = y+1;
			break;
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getIndex() {
		return ReactorTiles.COLLECTOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
	}

}
