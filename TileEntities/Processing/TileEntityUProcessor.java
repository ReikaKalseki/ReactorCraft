/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.ParallelTicker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.IC2Handler;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityUProcessor extends TileEntityInventoriedReactorBase implements IFluidHandler, PipeConnector {

	public static final int ACID_PER_UNIT = 125;
	public static final int ACID_PER_FLUORITE = 250;

	private HybridTank output = new HybridTank("uprocout", 3000);
	private HybridTank acid = new HybridTank("uprochf", 3000);
	private HybridTank water = new HybridTank("uprocwater", 3000);

	public int HF_timer;
	public int UF6_timer;

	public static final int ACID_TIME = 80;
	public static final int UF6_TIME = 400;

	private ForgeDirection facing;

	private ParallelTicker timer = new ParallelTicker().addTicker("acid", ACID_TIME).addTicker("uf6", UF6_TIME);

	@Override
	public int getIndex() {
		return ReactorTiles.PROCESSOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.getFacing(meta);
		this.getWaterBuckets();
		if (this.canMakeAcid()) {
			timer.updateTicker("acid");
			if (timer.checkCap("acid"))
				this.makeAcid();
		}
		else {
			timer.resetTicker("acid");
		}

		if (this.canMakeUF6()) {
			timer.updateTicker("uf6");
			if (timer.checkCap("uf6"))
				this.makeUF6();
		}
		else {
			timer.resetTicker("uf6");
		}

		if (!world.isRemote) {
			HF_timer = timer.getTickOf("acid");
			UF6_timer = timer.getTickOf("uf6");
		}
	}

	public boolean canMakeUF6() {
		return this.hasUranium() && this.getHF() >= ACID_PER_UNIT && this.canAcceptMoreUF6(FluidContainerRegistry.BUCKET_VOLUME);
	}

	private boolean hasUranium() {
		if (inv[2] == null)
			return false;
		if (ReikaItemHelper.matchStacks(inv[2], ReactorOres.PITCHBLENDE.getProduct()))
			return true;
		ArrayList<ItemStack> ingots = OreDictionary.getOres("ingotUranium");
		return ReikaItemHelper.listContainsItemStack(ingots, inv[2]);
	}

	private boolean hasFluorite() {
		if (inv[0] == null)
			return false;
		if (inv[0].getItem() == ReactorItems.FLUORITE.getItemInstance())
			return true;
		ArrayList<ItemStack> shards = OreDictionary.getOres("gemFluorite");
		return ReikaItemHelper.listContainsItemStack(shards, inv[0]);
	}

	public boolean canMakeAcid() {
		return this.getWater() > 0 && this.hasFluorite() && this.canAcceptMoreHF(ACID_PER_FLUORITE);
	}

	private void makeAcid() {
		ReikaInventoryHelper.decrStack(0, inv);
		this.addHF(ACID_PER_FLUORITE);
		water.drain(ACID_PER_FLUORITE, true);
	}

	private void makeUF6() {
		ReikaInventoryHelper.decrStack(2, inv);
		output.fill(FluidRegistry.getFluidStack("uranium hexafluoride", FluidContainerRegistry.BUCKET_VOLUME), true);
		acid.drain(ACID_PER_UNIT, true);
	}

	public int getHFTimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("acid"));
	}

	public int getUF6TimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("uf6"));
	}

	public int getWaterScaled(int p) {
		return p*this.getWater()/water.getCapacity();
	}

	public int getHFScaled(int p) {
		return p*this.getHF()/acid.getCapacity();
	}

	public int getUF6Scaled(int p) {
		return p*this.getUF6()/output.getCapacity();
	}

	public int getCapacity() {
		return water.getCapacity();
	}

	public int getWater() {
		return water.getLevel();
	}

	public int getHF() {
		return acid.getLevel();
	}

	public int getUF6() {
		return output.getLevel();
	}

	private void getWaterBuckets() {
		if (inv[1] != null && inv[1].getItem() == Items.water_bucket && this.canAcceptMoreWater(FluidContainerRegistry.BUCKET_VOLUME)) {
			water.fill(FluidRegistry.getFluidStack("water", FluidContainerRegistry.BUCKET_VOLUME), true);
			inv[1] = new ItemStack(Items.bucket);
		}
	}

	public boolean canAcceptMoreWater(int amt) {
		return water.getFluid() == null || water.getFluid().amount+amt <= water.getCapacity();
	}

	public boolean canAcceptMoreHF(int amt) {
		return acid.getFluid() == null || acid.getFluid().amount+amt <= acid.getCapacity();
	}

	public boolean canAcceptMoreUF6(int amt) {
		return output.getFluid() == null || output.getFluid().amount+amt <= output.getCapacity();
	}

	@Override
	public boolean canRemoveItem(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (i == 2)
			return this.isValidUranium(is);
		if (is.getItem() == ReactorItems.FLUORITE.getItemInstance())
			return i == 0;
		if (is.getItem() == Items.water_bucket)
			return i == 1;
		return false;
	}

	public static boolean isValidUranium(ItemStack is) {
		if (ReikaItemHelper.matchStacks(is, ReactorOres.PITCHBLENDE.getProduct()))
			return true;
		if (ReikaItemHelper.matchStacks(is, IC2Handler.getInstance().getPurifiedCrushedUranium()))
			return true;
		if (ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotUranium"), is))
			return true;
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return output.drain(resource.amount, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.WATER) || fluid.equals(ReactorCraft.HF);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid.equals(ReactorCraft.UF6);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{water.getInfo(), acid.getInfo(), output.getInfo()};
	}

	public void addHF(int amt) {
		int a = acid.fill(FluidRegistry.getFluidStack("hydrofluoric acid", amt), true);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		UF6_timer = NBT.getInteger("uf6");
		HF_timer = NBT.getInteger("hf");

		water.readFromNBT(NBT);
		acid.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("uf6", UF6_timer);
		NBT.setInteger("hf", HF_timer);

		water.writeToNBT(NBT);
		acid.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	public int getFluid(FluidStack liquid) {
		if (liquid.getFluid().equals(FluidRegistry.WATER))
			return this.getWater();
		if (liquid.getFluid().equals(ReactorCraft.HF))
			return this.getHF();
		if (liquid.getFluid().equals(ReactorCraft.UF6))
			return this.getUF6();
		return 0;
	}

	public static boolean isUF6Ingredient(ItemStack is) {
		if (is.getItem() == ReactorItems.FLUORITE.getItemInstance())
			return true;
		if (isValidUranium(is))
			return true;
		return false;
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return false;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p);
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == facing ? Flow.OUTPUT : Flow.INPUT;
	}

	private void getFacing(int meta) {
		switch(meta) {
		case 0:
			facing = ForgeDirection.WEST;
			break;
		case 1:
			facing = ForgeDirection.EAST;
			break;
		case 2:
			facing = ForgeDirection.NORTH;
			break;
		case 3:
			facing = ForgeDirection.SOUTH;
			break;
		}
	}

	public void setWater(int level) {
		water.setContents(level, FluidRegistry.WATER);
	}

	public void setHF(int level) {
		acid.setContents(level, FluidRegistry.getFluid("hydrofluoric acid"));
	}

	public void setUF6(int level) {
		output.setContents(level, FluidRegistry.getFluid("uranium hexafluoride"));
	}
}
