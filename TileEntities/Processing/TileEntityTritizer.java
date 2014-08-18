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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityTritizer extends TileEntityReactorBase implements ReactorCoreTE, PipeConnector, IFluidHandler {

	public static final int CAPACITY = 1000;

	private HybridTank input = new HybridTank("tritizerin", CAPACITY);
	private HybridTank output = new HybridTank("tritizerout", CAPACITY);

	@Override
	public int getIndex() {
		return ReactorTiles.TRITIZER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (DragonAPICore.debugtest) {
			input.addLiquid(100, ReactorCraft.H2);
			if (output.getLevel() > CAPACITY/2)
				output.empty();
		}
		//this.onNeutron(null, world, x, y, z);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		input.writeToNBT(NBT);
		output.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		input.readFromNBT(NBT);
		output.readFromNBT(NBT);
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		if (input.isEmpty())
			return false;
		NeutronType type = e.getType();
		if (type.canIrradiateLiquids()) {
			Reactions r = Reactions.getReactionFrom(input.getActualFluid());
			if (!world.isRemote && this.canMake(r) && ReikaRandomHelper.doWithChance(r.chance)) {
				this.make(r);
				return true;
			}
		}
		return false;
	}

	private void make(Reactions r) {
		int amt = r.amount;
		input.removeLiquid(amt);
		output.addLiquid(amt, r.output);
	}

	private boolean canMake(Reactions r) {
		int amt = r.amount;
		return input.getLevel() >= amt && output.canTakeIn(amt) && input.getActualFluid().equals(r.input);
	}

	private static enum Reactions {
		TRITIUM("rc deuterium", "rc tritium", 75, 25),
		D20("water", "heavy water", 25, 100);

		public final Fluid input;
		public final Fluid output;
		public final int chance;
		public final int amount;

		private static final Reactions[] reactionList = values();

		private Reactions(String in, String out, int chance, int amt) {
			this.chance = chance;
			amount = amt;
			input = FluidRegistry.getFluid(in);
			output = FluidRegistry.getFluid(out);
		}

		public static Reactions getReactionFrom(Fluid in) {
			for (int i = 0; i < reactionList.length; i++) {
				Reactions r = reactionList[i];
				if (r.input.equals(in))
					return r;
			}
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!this.canDrain(from, resource.getFluid()))
			return null;
		return output.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!this.canDrain(from, null))
			return null;
		return output.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && Reactions.getReactionFrom(fluid) != null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.DOWN;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{input.getInfo(), output.getInfo()};
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m == MachineRegistry.PIPE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return this.canConnectToPipe(p) && side.offsetY != 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!this.canFill(from, resource.getFluid()))
			return 0;
		return input.fill(resource, doFill);
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		if (side == ForgeDirection.UP)
			return Flow.INPUT;
		if (side == ForgeDirection.DOWN)
			return Flow.OUTPUT;
		return Flow.NONE;
	}

}
