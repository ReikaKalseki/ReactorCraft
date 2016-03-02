/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Thorium;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

//Liquid Fueled
//Secondary Loop
//Does not spontaneously emit neutrons
//Cannot overheat (negative void coefficient)
//If gets over some temp, dumps fuel on ground
//Liquid waste
@Strippable(value={"buildcraft.api.transport.IPipeConnection"})
public class TileEntityThoriumCore extends TileEntityNuclearCore implements InertIInv, IFluidHandler, PipeConnector, IPipeConnection {

	private static final int CYCLE_AMOUNT = 100;

	private final HybridTank fuelTank = new HybridTank("thoriumfuel", 4000);
	private final HybridTank fuelTankOut = new HybridTank("thoriumfuelout", 4000);
	private final HybridTank wasteTank = new HybridTank("thoriumwaste", 1000);

	private StepTimer timer2 = new StepTimer(20);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(temperature+":"+this, temperature > 700);

		if (DragonAPICore.debugtest) {
			ReikaInventoryHelper.clearInventory(this);
			ReikaInventoryHelper.addToIInv(ReactorItems.THORIUM.getStackOf(), this);
		}

		timer2.update();

		if (timer2.checkCap()) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);
				/*
				if (r == ReactorTiles.SODIUMBOILER) {
					TileEntitySodiumHeater te = (TileEntitySodiumHeater)world.getTileEntity(dx, dy, dz);
					int dTemp = temperature-te.getTemperature();
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.setTemperature(te.getTemperature()+dTemp/16);
					}
				}
				 */
			}
		}
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return liq == LiquidStates.LITHIUM;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return false;
	}

	@Override
	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		super.onNeutron(e, world, x, y, z);
		if (!world.isRemote) {
			if (e.getType().canTriggerFission() && ReikaRandomHelper.doWithChance(this.getNeutronInteractionChance())) {
				if (this.checkPoisonedChance())
					return true;
				if (ReikaRandomHelper.doWithChance(this.getNeutronChance()) && this.hasFuel()) {
					fuelTank.removeLiquid(CYCLE_AMOUNT);
					fuelTankOut.addLiquid(CYCLE_AMOUNT, FluidRegistry.getFluid("rc hot lifbe"));
					temperature += 20;
					this.spawnNeutronBurst(world, x, y, z);

					if (ReikaRandomHelper.doWithChance(5)) {
						this.addWaste();
					}
				}

				return true;
			}
		}
		return false;
	}

	private double getNeutronInteractionChance() {
		return ReikaMathLibrary.cosInterpolation(this.getMinTemperature(), this.getMaxTemperature(), temperature);
	}

	private double getNeutronChance() {
		return 25-20*Math.sqrt((temperature-this.getMinTemperature())/(double)(this.getMaxTemperature()-this.getMinTemperature()));
	}

	private boolean hasFuel() {
		return fuelTank.getLevel() >= CYCLE_AMOUNT;
	}

	@Override
	protected boolean checkPoisonedChance() {
		return ReikaRandomHelper.doWithChance(0.75*Math.pow(wasteTank.getLevel()/(double)wasteTank.getCapacity(), 1.6));
	}

	@Override
	protected void addWaste() {
		wasteTank.addLiquid(CYCLE_AMOUNT/2, FluidRegistry.getFluid("rc nuclear waste"));
	}

	@Override
	public boolean isFissile() {
		return false;
	}

	private int getMinTemperature() {
		return 400;
	}

	@Override
	public int getMaxTemperature() {
		return 1200;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		return false;
	}

	@Override
	public int getIndex() {
		return ReactorTiles.THORIUM.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return fuelTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (this.canDrain(from, resource.getFluid())) {
			return from.offsetY == 0 ? wasteTank.drain(resource.amount, doDrain) : fuelTankOut.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.canDrain(from, null)) {
			return from.offsetY == 0 ? wasteTank.drain(maxDrain, doDrain) : fuelTankOut.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && fluid == FluidRegistry.getFluid("rc lifbe fuel");
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from != ForgeDirection.UP && (ReikaFluidHelper.isFluidDrainableFromTank(fluid, wasteTank) || ReikaFluidHelper.isFluidDrainableFromTank(fluid, fuelTankOut));
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{fuelTank.getInfo(), fuelTankOut.getInfo(), wasteTank.getInfo()};
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return type == PipeType.FLUID ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry m, ForgeDirection side) {
		return side.offsetY == 0 ? false : m.isStandardPipe();
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return side == ForgeDirection.UP ? Flow.INPUT : Flow.OUTPUT;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		fuelTank.readFromNBT(NBT);
		fuelTankOut.readFromNBT(NBT);
		wasteTank.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		fuelTank.writeToNBT(NBT);
		fuelTankOut.writeToNBT(NBT);
		wasteTank.writeToNBT(NBT);
	}

}
