/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fission.Thorium;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.TemperaturedReactorTyped;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.ReactorCraft.TileEntities.Waste.TileEntityWastePipe;
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
	public static final int FUEL_DUMP_TEMPERATURE = 1100;

	private final HybridTank fuelTank = new HybridTank("thoriumfuel", 4000);
	private final HybridTank fuelTankOut = new HybridTank("thoriumfuelout", 4000);
	private final HybridTank wasteTank = new HybridTank("thoriumwaste", 1000);

	private StepTimer timer2 = new StepTimer(5);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(temperature+":"+this, temperature > 700);

		if (DragonAPICore.debugtest) {
			ReikaInventoryHelper.clearInventory(this);
			fuelTank.addLiquid(100, ReactorCraft.LIFBe_fuel);
			if (fuelTankOut.getLevel() >= fuelTankOut.getCapacity()/2)
				fuelTankOut.empty();
			wasteTank.empty();

			//temperature = 400;
		}

		if (!world.isRemote) {

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
					if (r == this.getTile()) {
						this.balanceLiquidsWith((TileEntityThoriumCore)this.getAdjacentTileEntity(dir));
					}
				}
			}
		}

		if (!world.isRemote)
			this.feedFluid();
	}

	@Override
	protected int getRestingTemperature(World world, int x, int y, int z) {
		return fuelTank.getActualFluid() == ReactorCraft.LIFBe_fuel_preheat ? 250 : super.getRestingTemperature(world, x, y, z);
	}

	private void balanceLiquidsWith(TileEntityThoriumCore te) {
		this.balanceTanks(wasteTank, te.wasteTank);
		this.balanceTanks(fuelTank, te.fuelTank);
		this.balanceTanks(fuelTankOut, te.fuelTankOut);
	}

	private void balanceTanks(HybridTank from, HybridTank to) {
		if (to.getActualFluid() != null && to.getActualFluid() != from.getActualFluid())
			return;
		int dl = from.getLevel()-to.getLevel();
		if (dl > 1) {
			int amt = Math.min(from.getLevel()/4, Math.max(1, dl/8+1));
			if (amt > 0) {
				to.addLiquid(amt, from.getActualFluid());
				from.removeLiquid(amt);
			}
		}
	}

	@Override
	protected int getDecayNeutronChance() {
		return 30;
	}

	@Override
	protected int getWarningTemperature() {
		return 900;
	}

	@Override
	protected int getAmbientHeatLossFactor(World world, int x, int y, int z, int base, int Tamb) {
		return Tamb < temperature ? base*4 : base/2;
	}

	@Override
	protected float getHeatConductionThroughput(TemperaturedReactorTyped other) {
		//if (this.getRestingTemperature(worldObj, xCoord, yCoord, zCoord) > 100) {
		if (other.getReactorType() != ReactorType.THORIUM)
			return 0.25F;
		//}
		return super.getHeatConductionThroughput(other);
	}

	private int getSameCoreHeatConductionFraction() {
		return 4;
	}

	@Override
	protected int getHeatConductionFraction(TemperaturedReactorTyped other) {
		return other.getReactorType() == ReactorType.FISSION ? 2 : super.getHeatConductionFraction(other);
	}

	@Override
	protected float getHeatConductionEfficiency(TemperaturedReactorTyped other) {
		boolean rest = temperature-this.getRestingTemperature(worldObj, xCoord, yCoord, zCoord) < 50;
		switch(other.getTile()) {
			case BOILER:
				return rest ? 0.125F : 0.75F;
			case FUEL:
				return rest ? 0.2F : 1F;
			default:
				return super.getHeatConductionEfficiency(other);
		}
	}

	private void feedFluid() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		TileEntity tile = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (tile instanceof TileEntityThoriumCore) {
			int amt = ((TileEntityThoriumCore)tile).feedFluidIn(fuelTank.getFluid(), 0);
			if (amt > 0) {
				fuelTank.removeLiquid(amt);
			}

			amt = ((TileEntityThoriumCore)tile).feedFluidIn(fuelTankOut.getFluid(), 1);
			if (amt > 0) {
				fuelTankOut.removeLiquid(amt);
			}

			amt = ((TileEntityThoriumCore)tile).feedFluidIn(wasteTank.getFluid(), 2);
			if (amt > 0) {
				wasteTank.removeLiquid(amt);
			}
		}
	}

	private int feedFluidIn(FluidStack is, int tankType) {
		if (is == null)
			return 0;
		HybridTank tank = null;
		switch(tankType) {
			case 0:
				tank = fuelTank;
				break;
			case 1:
				tank = fuelTankOut;
				break;
			case 2:
				tank = wasteTank;
				break;
		}
		if (tank == null)
			return 0;
		Fluid f = is.getFluid();
		if (tank.getActualFluid() != null && tank.getActualFluid() != f)
			return 0;
		else {
			int add = Math.min(tank.getRemainingSpace(), is.amount);
			tank.addLiquid(add, f);
			return add;
		}
	}

	int dumpFuel(TileEntityFuelDump te, int max) {
		/*
		int n = MathHelper.ceiling_double_int(fuelTank.getLevel()/1000D);
		for (int i = 0; i < n; i++) {
			world.setBlock(x, y-1-i, z, ReactorBlocks.THORIUM.getBlockInstance());
		}
		fuelTank.empty();
		 */
		int amt = Math.min(max, fuelTank.getLevel());
		if (amt > 0) {
			fuelTank.removeLiquid(amt);
			ReactorAchievements.THORIUMDUMP.triggerAchievement(this.getPlacer());
		}
		return amt;
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
			if (e.getType().canTriggerFission() && ReikaRandomHelper.doWithChance(e.getNeutronSpeed().getInteractionMultiplier()) && e.getType() != NeutronType.BREEDER && ReikaRandomHelper.doWithChance(this.getNeutronInteractionChance())) {
				if (this.checkPoisonedChance())
					return true;
				if (ReikaRandomHelper.doWithChance(this.getNeutronChance()) && this.hasFuel()) {
					fuelTank.removeLiquid(CYCLE_AMOUNT);
					fuelTankOut.addLiquid(CYCLE_AMOUNT, FluidRegistry.getFluid("rc hot lifbe"));
					temperature += 50;
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
		int midT = (this.getMinTemperature()+this.getMaxTemperature())/2;
		double f = temperature <= midT ? 0.5 : 0.75;
		return (1-f)+f*ReikaMathLibrary.cosInterpolation(this.getMinTemperature(), this.getMaxTemperature(), temperature);
	}

	private double getNeutronChance() {
		return 50-40*Math.sqrt((temperature-this.getMinTemperature())/(double)(this.getMaxTemperature()-this.getMinTemperature()));
	}

	public boolean hasFuel() {
		return fuelTank.getLevel() >= CYCLE_AMOUNT;
	}

	@Override
	protected boolean checkPoisonedChance() {
		return ReikaRandomHelper.doWithChance(0.875*Math.pow(wasteTank.getLevel()/(double)wasteTank.getCapacity(), 1.6));
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
	public ReactorTiles getTile() {
		return ReactorTiles.THORIUM;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) ? fuelTank.fill(resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (this.canDrain(from, resource.getFluid())) {
			return from.offsetY == 0 ? this.isWastePipe(from) ? wasteTank.drain(resource.amount, doDrain) : null : fuelTankOut.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.canDrain(from, null)) {
			return from.offsetY == 0 ? this.isWastePipe(from) ? wasteTank.drain(maxDrain, doDrain) : null : fuelTankOut.drain(maxDrain, doDrain);
		}
		return null;
	}

	private boolean isWastePipe(ForgeDirection from) {
		return this.getAdjacentTileEntity(from) instanceof TileEntityWastePipe;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && (fluid == ReactorCraft.LIFBe_fuel || fluid == ReactorCraft.LIFBe_fuel_preheat);
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
		return m.isStandardPipe() || m == MachineRegistry.FUELLINE;
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry m, ForgeDirection side) {
		return side == ForgeDirection.UP ? m == MachineRegistry.FUELLINE : m.isStandardPipe();
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

	@Override
	public ReactorType getReactorType() {
		return ReactorType.THORIUM;
	}

}
