/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.HTGR;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.PebbleBedArrangement;
import Reika.ReactorCraft.Auxiliary.ReactorBlock;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;

public class TileEntityPebbleBed extends TileEntityInventoriedReactorBase implements Temperatured, Feedable, ReactorBlock, BreakAction {

	protected StepTimer tempTimer = new StepTimer(20);

	private PebbleBedArrangement reactor;

	public static final int MINTEMP = 800;
	public static final int OVERTEMP = 1200;
	public static final int FAILTEMP = 4400;

	private int damage = 0;

	@Override
	public int getSizeInventory() {
		return 47;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && ReikaRandomHelper.doWithChance(this.getFissionChance()/100D))
			this.runDecayCycle();

		if (DragonAPICore.debugtest) {
			ReikaInventoryHelper.clearInventory(this);
			ReikaInventoryHelper.addToIInv(ReactorItems.PELLET.getStackOf(), this);
		}

		//ReikaJavaLibrary.pConsole(temperature, Side.SERVER);
		if (!world.isRemote)
			this.feed();

		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, x, y, z);
		}

		if (damage > 0 && rand.nextInt(800) == 0) {
			damage--;
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.checkAndJoinArrangement(world, x, y, z);
	}

	private void checkAndJoinArrangement(World world, int x, int y, int z) {
		reactor = new PebbleBedArrangement(this);
		int r = 3;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = this.getTileEntity(dx, dy, dz);
					if (te instanceof TileEntityPebbleBed) {
						reactor.merge(((TileEntityPebbleBed)te).reactor);
					}
				}
			}
		}
	}

	private double getFissionChance() {
		int size = this.getReactorSize();
		if (size >= 128)
			return 1; //20
		else if (size >= 72)
			return 2; //12
		else if (size >= 48)
			return 3; //8
		else if (size >= 36)
			return 4; //8
		else if (size >= 24)
			return 6;
		else if (size >= 12)
			return 4;
		else if (size >= 6)
			return 2;
		else
			return 1;
	}

	private int getReactorSize() {
		return reactor.getSize();
	}

	public void setReactorObject(PebbleBedArrangement pba) {
		reactor = pba;
	}

	@Override
	public void breakBlock() {
		reactor.remove(this);
	}

	private void runDecayCycle() {
		if (!worldObj.isRemote) {
			int slot = -1;
			for (int i = inv.length-1; i >= 0; i--) {
				ItemStack is = inv[i];
				if (is != null && is.getItem() == ReactorItems.PELLET.getItemInstance()) {
					slot = i;
					i = -1;
				}
			}
			if (slot != -1) {
				if (ReikaRandomHelper.doWithChance(3)) {
					ItemStack is = inv[slot];
					inv[slot] = this.getFissionProduct(is);
				}
				temperature += 20;
			}
		}
	}

	private ItemStack getFissionProduct(ItemStack is) {
		if (is.getItemDamage() == ReactorItems.PELLET.getNumberMetadatas()-1)
			return ReactorItems.OLDPELLET.getStackOf();
		return ReactorItems.PELLET.getStackOfMetadata(is.getItemDamage()+1);
	}

	@Override
	protected void updateTemperature(World world, int x, int y, int z) {
		super.updateTemperature(world, x, y, z);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		int dT = temperature-Tamb;

		if (dT != 0) {
			int f = ReikaWorldHelper.isExposedToAir(world, x, y, z) ? 32 : 96;
			temperature -= (1+dT/f);
		}

		if (dT > 0) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ReactorTiles r = ReactorTiles.getTE(world, dx, dy, dz);

				if (r == this.getMachine()) {
					TileEntityPebbleBed te = (TileEntityPebbleBed)world.getTileEntity(dx, dy, dz);
					int dTemp = temperature-te.temperature;
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.temperature += dTemp/16;
					}
				}
			}
		}

		if (temperature >= this.getMaxTemperature()) {
			world.setBlock(x, y, z, Blocks.flowing_lava);
		}
		else if (temperature >= OVERTEMP) {
			int chance = 5+(FAILTEMP-temperature)/10/this.getReactorSize();
			if (rand.nextInt(chance) == 0) {
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz", 1, 0.5F);
				ReikaParticleHelper.SMOKE.spawnAroundBlockWithOutset(world, x, y, z, 9, 0.0625);
				damage++;
				if (damage >= 100) {
					this.melt(world, x, y, z);
				}
			}
		}
	}

	private void melt(World world, int x, int y, int z) {
		ReactorAchievements.PEBBLEFAIL.triggerAchievement(this.getPlacer());
		this.delete();
		world.setBlock(x, y, z, Blocks.flowing_lava);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz", 2, 0.1F);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 1, 0.2F);
		ReikaParticleHelper.LAVA.spawnAroundBlockWithOutset(world, x, y, z, 12, 0.0625);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return is.getItem() == ReactorItems.PELLET.getItemInstance() || is.getItem() == ReactorItems.OLDPELLET.getItemInstance();
	}

	@Override
	public boolean canEnterFromSide(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public boolean canExitToSide(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public boolean canRemoveItem(int slot, ItemStack is) {
		return is.getItem() == ReactorItems.OLDPELLET.getItemInstance();
	}

	@Override
	public int getIndex() {
		return ReactorTiles.PEBBLEBED.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isFissile() {
		return ReikaInventoryHelper.checkForItem(ReactorItems.PELLET.getItemInstance(), inv);
	}

	public boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		Block id = world.getBlock(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (tile instanceof TileEntityPebbleBed) {
			if (((Feedable)tile).feedIn(inv[inv.length-1])) {
				for (int i = inv.length-1; i > 0; i--)
					inv[i] = inv[i-1];

				id = world.getBlock(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = this.getAdjacentTileEntity(ForgeDirection.UP);
				if (tile instanceof TileEntityPebbleBed) {
					inv[0] = ((Feedable) tile).feedOut();
				}
				else
					inv[0] = null;
			}
		}
		this.collapseInventory();
		return false;
	}

	private void collapseInventory() {
		for (int i = 0; i < inv.length; i++) {
			for (int k = inv.length-1; k > 0; k--) {
				if (inv[k] == null) {
					inv[k] = inv[k-1];
					inv[k-1] = null;
				}
			}
		}
	}

	@Override
	public boolean feedIn(ItemStack is) {
		if (is == null)
			return true;
		if (!this.isItemValidForSlot(0, is))
			return false;
		if (inv[0] == null) {
			inv[0] = is.copy();
			return true;
		}
		return false;
	}

	@Override
	public ItemStack feedOut() {
		if (inv[inv.length-1] == null)
			return null;
		else {
			ItemStack is = inv[inv.length-1].copy();
			inv[inv.length-1] = null;
			return is;
		}
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	@Override
	public int getMaxTemperature() {
		return FAILTEMP;
	}

	@Override
	public boolean canDumpHeatInto(LiquidStates liq) {
		return false;
	}

	@Override
	public final int getTextureState(ForgeDirection side) {
		if (side.offsetY != 0)
			return 4;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		ReactorTiles src = this.getMachine();
		ReactorTiles r = ReactorTiles.getTE(world, x, y-1, z);
		ReactorTiles r2 = ReactorTiles.getTE(world, x, y+1, z);
		if (r2 == src && r == src)
			return 2;
		else if (r2 == src)
			return 1;
		else if (r == src)
			return 3;
		return 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		damage = NBT.getInteger("dmg");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("dmg", damage);
	}

}
