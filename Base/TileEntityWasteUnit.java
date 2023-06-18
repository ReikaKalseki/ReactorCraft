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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.ChromatiAPI;
import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.Isotopes;
import Reika.DragonAPI.Libraries.MathSci.ReikaNuclearHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaTimeHelper;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Registry.ReactorItems;

public abstract class TileEntityWasteUnit extends TileEntityInventoriedReactorBase {

	private long lastTickTime = -1;

	protected void fill() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (this.getStackInSlot(i) == null) {
				ItemStack is = WasteManager.getFullyRandomWasteItem();
				this.setInventorySlotContents(i, is);
			}
		}
	}

	public abstract boolean leaksRadiation();

	public abstract boolean isValidIsotope(Isotopes i);

	protected abstract boolean canBeAccelerated();

	protected abstract double getBaseDecayRate();

	private final double getAccelerationFactor() {
		double base = this.getBaseDecayRate();
		if (this.canBeAccelerated())
			base = Math.pow(base, this.getAcceleratorBoost());
		return base*192;
	}

	private final double getAcceleratorBoost() {
		if (!ModList.CHROMATICRAFT.isLoaded()) {
			return 1;
		}
		CrystalElementProxy e = CrystalElementAccessor.getByEnum("LIGHTBLUE");
		int tier = ChromatiAPI.getAPI().adjacency().getAdjacentUpgradeTier(worldObj, xCoord, yCoord, zCoord, e);
		if (tier <= 0)
			return 1;
		return Math.sqrt(ChromatiAPI.getAPI().adjacency().getFactor(e, tier));
	}

	protected final void decayWaste() {
		double mult = this.getAccelerationFactor();
		if (this.accountForOutGameTime())
			mult *= (1+this.getSkippedTicks());
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null && inv[i].getItem() == ReactorItems.WASTE.getItemInstance()) {
				Isotopes atom = Isotopes.getIsotope(inv[i].getItemDamage());
				if (ReikaRandomHelper.doWithChance(mult/this.getBaseDecayRate()*0.5*ReikaNuclearHelper.getDecayChanceFromHalflife(Math.log(atom.getMCHalfLife())))) {
					//ReikaJavaLibrary.pConsole("Radiating from "+atom);
					if (this.leaksRadiation() && rand.nextBoolean())
						this.leakRadiation(worldObj, xCoord, yCoord, zCoord);
				}
				//ReikaJavaLibrary.pConsole(ReikaNuclearHelper.getDecayChanceFromHalflife(atom.getMCHalfLife()));
				if (ReikaNuclearHelper.shouldDecay(atom, mult)) {
					ReikaInventoryHelper.decrStack(i, this, Math.max(1, inv[i].stackSize/2));
					this.onDecayWaste(i);
				}
			}
		}
	}

	protected abstract boolean accountForOutGameTime();

	private long getSkippedTicks() { //compensate for lag + make decay effectively run even with MC closed
		long time = System.currentTimeMillis();
		long dur = time-lastTickTime;
		long ticks = 0;
		if (dur > 50) {
			ticks = (dur/50)-1;
		}
		lastTickTime = time;
		return ticks;
	}

	protected void onDecayWaste(int i) {

	}

	protected void leakRadiation(World world, int x, int y, int z) {
		ForgeDirection dir = dirs[rand.nextInt(dirs.length)];
		if (!world.isRemote)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, dir, NeutronType.WASTE));
	}

	@Override
	public final boolean isItemValidForSlot(int i, ItemStack is) {
		return is.getItem() == ReactorItems.WASTE.getItemInstance() && is.getItemDamage() < 1000 && this.isValidIsotope(Isotopes.getIsotope(is.getItemDamage())) && this.isValidSlot(i, is);
	}

	protected boolean isValidSlot(int i, ItemStack is) {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public final boolean canItemEnterFromSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public final boolean canItemExitToSide(ForgeDirection dir) {
		return true;
	}

	public final int countWaste() {
		int count = 0;
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null) {
				if (inv[i].getItem() == ReactorItems.WASTE.getItemInstance()) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	public final boolean hasWaste() {
		return this.countWaste() > 0;
	}

	public static double getHalfLife(ItemStack is) {
		if (is.getItem() != ReactorItems.WASTE.getItemInstance())
			return 0;
		return Isotopes.getIsotope(is.getItemDamage()).getMCHalfLife();//WasteManager.getWasteList().get(is.getItemDamage()).getMCHalfLife();
	}

	public static boolean isLongLivedWaste(ItemStack is) {
		return is.getItem() == ReactorItems.WASTE.getItemInstance() && getHalfLife(is) > 6*ReikaTimeHelper.YEAR.getMinecraftDuration();
	}

	public static boolean isLongLivedWaste(Isotopes i) {
		return i.getMCHalfLife() > 6*ReikaTimeHelper.YEAR.getMinecraftDuration();
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		lastTickTime = NBT.getLong("lasttime");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setLong("lasttime", lastTickTime);
	}
}
