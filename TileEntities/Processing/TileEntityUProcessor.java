/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.ParallelTicker;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2Handler;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityUProcessor extends TileEntityInventoriedReactorBase implements IFluidHandler, PipeConnector {

	private final HybridTank output = new HybridTank("uprocout", 3000);
	private final HybridTank intermediate = new HybridTank("uprocmid", 3000);
	private final HybridTank input = new HybridTank("uprocin", 3000);

	public int intermediate_timer;
	public int output_timer;

	private ForgeDirection facing;

	private ParallelTicker timer = new ParallelTicker().addTicker("intermediate", 0).addTicker("output", 0);

	public static enum Processes {
		UF6("water", "rc hydrofluoric acid", "rc uranium hexafluoride", 250, 1000, 250, 125, 80, 400, "ingotUranium"),
		LiFBe("rc lithium", "", "rc lifbe", 100, 500, 0, 0, 120, 600, ReactorStacks.emeralddust);

		public final int intermediateTime;
		public final int ouputTime;

		public final Fluid inputFluid;
		public final Fluid intermediateFluid;
		public final Fluid outputFluid;

		public final int inputFluidConsumed;
		public final int outputFluidProduced;

		public final int intermediateFluidProduced;
		public final int intermediateFluidConsumed;

		private final HashSet<KeyedItemStack> inputItem = new HashSet();

		private static final HashMap<String, Processes> processMap = new HashMap();
		private static final HashMap<String, Processes> processOutputMap = new HashMap();
		public static final Processes[] list = values();

		private Processes(String f, String f1, String f2, int incons, int outprod, int prod, int cons, int t1, int t2, String in) {
			this(f, f1, f2, incons, outprod, prod, cons, t1, t2, new ArrayList(OreDictionary.getOres(in)));
		}

		private Processes(String f, String f1, String f2, int incons, int outprod, int prod, int cons, int t1, int t2, ItemStack in) {
			this(f, f1, f2, incons, outprod, prod, cons, t1, t2, ReikaJavaLibrary.makeListFrom(in));
		}

		private Processes(String f, String f1, String f2, int incons, int outprod, int prod, int cons, int t1, int t2, Collection<ItemStack> in) {
			inputFluid = FluidRegistry.getFluid(f);
			intermediateFluid = FluidRegistry.getFluid(f1);
			outputFluid = FluidRegistry.getFluid(f2);

			intermediateTime = t1;
			ouputTime = t2;

			inputFluidConsumed = incons;
			outputFluidProduced = outprod;

			intermediateFluidProduced = prod;
			intermediateFluidConsumed = cons;

			if (f2.equals("rc uranium hexafluoride")) {
				if (ModList.IC2.isLoaded()) {
					ItemStack is = IC2Handler.getInstance().getPurifiedCrushedUranium();
					if (is != null)
						in.add(is);
				}
			}
			for (ItemStack is : in) {
				inputItem.add(new KeyedItemStack(is).setSimpleHash(true));
			}
		}

		public boolean hasIntermediate() {
			return intermediateFluid != null && intermediateFluidProduced > 0;
		}

		static {
			for (int i = 0; i < list.length; i++) {
				Processes p = list[i];
				processMap.put(p.inputFluid.getName(), p);
				processOutputMap.put(p.outputFluid.getName(), p);
			}
		}

		public boolean isValidItem(ItemStack is) {
			return inputItem.contains(new KeyedItemStack(is).setSimpleHash(true));
		}

		public List<ItemStack> getInputItemList() {
			ArrayList li = new ArrayList();
			for (KeyedItemStack ks : inputItem) {
				li.add(ks.getItemStack());
			}
			return li;
		}

	}

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
		this.getFluidContainers();
		Processes p = this.getProcess();
		if (p == null)
			return;
		timer.setCap("intermediate", p.intermediateTime);
		timer.setCap("output", p.ouputTime);
		if (p.hasIntermediate() && this.canRunIntermediate(p)) {
			timer.updateTicker("intermediate");
			if (timer.checkCap("intermediate"))
				this.runIntermediate(p);
		}
		else {
			timer.resetTicker("intermediate");
		}

		if (this.canRunOutput(p)) {
			timer.updateTicker("output");
			if (timer.checkCap("output"))
				this.runOutput(p);
		}
		else {
			timer.resetTicker("output");
		}

		if (!world.isRemote) {
			intermediate_timer = timer.getTickOf("intermediate");
			output_timer = timer.getTickOf("output");
		}
	}

	private Processes getProcess() {
		Fluid f = input.getActualFluid();
		if (f == null)
			return null;
		Processes p = Processes.processMap.get(f.getName());
		if (p == null)
			return null;
		if (p.hasIntermediate() && !this.hasFluorite())
			;//return null;
		if (!this.hasInputItem(p))
			;//return null;
		return p;
	}

	public boolean canRunOutput(Processes p) {
		return this.hasInputItem(p) && (!p.hasIntermediate() || this.getIntermediate() >= p.intermediateFluidConsumed) && this.canAcceptMoreOutput(p.outputFluidProduced);
	}

	private boolean hasInputItem(Processes p) {
		if (inv[2] == null)
			return false;
		return p.isValidItem(inv[2]);
	}

	private boolean hasFluorite() {
		if (inv[0] == null)
			return false;
		if (inv[0].getItem() == ReactorItems.FLUORITE.getItemInstance())
			return true;
		ArrayList<ItemStack> shards = OreDictionary.getOres("gemFluorite");
		return ReikaItemHelper.collectionContainsItemStack(shards, inv[0]);
	}

	public boolean canRunIntermediate(Processes p) {
		return this.getInput() > 0 && this.canAcceptMoreIntermediate(p.intermediateFluidProduced);
	}

	private void runIntermediate(Processes p) {
		ReikaInventoryHelper.decrStack(0, inv);
		this.addIntermediate(p.intermediateFluidProduced, p.intermediateFluid);
		input.drain(p.inputFluidConsumed, true);
	}

	private void runOutput(Processes p) {
		ReikaInventoryHelper.decrStack(2, inv);
		if (!p.hasIntermediate()) {
			ReikaInventoryHelper.decrStack(0, inv);
			input.drain(p.inputFluidConsumed, true);
		}
		output.fill(new FluidStack(p.outputFluid, p.outputFluidProduced), true);
		intermediate.drain(p.intermediateFluidConsumed, true);
		if (p == Processes.UF6) {
			ReactorAchievements.UF6.triggerAchievement(this.getPlacer());
		}
	}

	public int getIntermediateTimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("intermediate"));
	}

	public int getOutputTimerScaled(int p) {
		return (int)(p*timer.getPortionOfCap("output"));
	}

	public int getInputScaled(int p) {
		return p*this.getInput()/input.getCapacity();
	}

	public int getIntermediateScaled(int p) {
		return p*this.getIntermediate()/intermediate.getCapacity();
	}

	public int getOutputScaled(int p) {
		return p*this.getOutput()/output.getCapacity();
	}

	public int getCapacity() {
		return input.getCapacity();
	}

	public int getInput() {
		return input.getLevel();
	}

	public int getIntermediate() {
		return intermediate.getLevel();
	}

	public int getOutput() {
		return output.getLevel();
	}

	public Fluid getInputFluid() {
		return input.getActualFluid();
	}

	public Fluid getIntermediateFluid() {
		return intermediate.getActualFluid();
	}

	public Fluid getOutputFluid() {
		return output.getActualFluid();
	}

	private void getFluidContainers() {
		if (inv[1] != null) {
			FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(inv[1]);
			if (fs != null && Processes.processMap.get(fs.getFluid().getName()) != null && this.canAcceptMoreInput(fs.amount)) {
				input.fill(fs.copy(), true);
				inv[1] = FluidContainerRegistry.drainFluidContainer(inv[1]);
			}
		}
	}

	public boolean canAcceptMoreInput(int amt) {
		return input.getFluid() == null || input.getFluid().amount+amt <= input.getCapacity();
	}

	public boolean canAcceptMoreIntermediate(int amt) {
		return intermediate.getFluid() == null || intermediate.getFluid().amount+amt <= intermediate.getCapacity();
	}

	public boolean canAcceptMoreOutput(int amt) {
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
		switch (i) {
			case 0:
				return is.getItem() == ReactorItems.FLUORITE.getItemInstance();
			case 1:
				return this.getProcessByFluidItem(is) != null;
			case 2:
				return this.getProcessByMainItem(is) != null;
		}
		return false;
	}

	public static Processes getProcessByMainItem(ItemStack is) {
		for (int i = 0; i < Processes.list.length; i++) {
			Processes p = Processes.list[i];
			if (p.isValidItem(is)) {
				return p;
			}
		}
		return null;
	}

	public static Processes getProcessByFluidItem(ItemStack is) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(is);
		if (fs == null)
			return null;
		return Processes.processMap.get(fs.getFluid().getName());
	}

	public static Processes getProcessByFluidOutputItem(ItemStack is) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(is);
		if (fs == null)
			return null;
		return Processes.processOutputMap.get(fs.getFluid().getName());
	}

	public static Processes getProcessByInput(Fluid f) {
		return Processes.processMap.get(f.getName());
	}

	public static Processes getProcessByOutput(Fluid f) {
		return Processes.processOutputMap.get(f.getName());
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return input.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? output.drain(resource.amount, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		for (int i = 0; i < Processes.list.length; i++) {
			if (Processes.list[i].inputFluid.equals(fluid))
				return true;
			if (Processes.list[i].intermediateFluid != null && Processes.list[i].intermediateFluid.equals(fluid))
				return true;
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		for (int i = 0; i < Processes.list.length; i++) {
			if (Processes.list[i].outputFluid.equals(fluid))
				return true;
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{input.getInfo(), intermediate.getInfo(), output.getInfo()};
	}

	public void addIntermediate(int amt, Fluid f) {
		int a = intermediate.fill(new FluidStack(f, amt), true);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		output_timer = NBT.getInteger("uf6");
		intermediate_timer = NBT.getInteger("hf");

		input.readFromNBT(NBT);
		intermediate.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("uf6", output_timer);
		NBT.setInteger("hf", intermediate_timer);

		input.writeToNBT(NBT);
		intermediate.writeToNBT(NBT);
		output.writeToNBT(NBT);
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

}
