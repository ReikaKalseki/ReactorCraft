/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.HydrogenExplosion;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityNeutron.NeutronType;
import Reika.ReactorCraft.Event.ReactorMeltdownEvent;
import Reika.ReactorCraft.Registry.ReactorAchievements;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public abstract class TileEntityNuclearCore extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Temperatured, Feedable,
ChunkLoadingTile {

	protected StepTimer tempTimer = new StepTimer(20);

	protected int hydrogen = 0;
	private int activeTimer = 0;

	public static final int CLADDING = 800;
	public static final int HYDROGEN = 1400;
	public static final int EXPLOSION = 1800;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && rand.nextInt(20) == 0)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection(), NeutronType.DECAY));
		if (DragonAPICore.debugtest) {
			ReikaInventoryHelper.clearInventory(this);
			ReikaInventoryHelper.addToIInv(ReactorItems.FUEL.getStackOf(), this);
		}

		this.feed();
		this.feedWaste(world, x, y, z);

		if (activeTimer > 0) {
			activeTimer--;
			this.onActivityChange(false);
		}

		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, x, y, z);
		}
		//ReikaJavaLibrary.pConsole(temperature);
		if (temperature > CLADDING) {
			if (rand.nextInt(20) == 0)
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
			ReikaParticleHelper.SMOKE.spawnAroundBlockWithOutset(world, x, y, z, 9, 0.0625);
		}
		else if (temperature > 500 && ReikaRandomHelper.doWithChance(20)) {
			if (rand.nextInt(20) == 0)
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
			ReikaParticleHelper.SMOKE.spawnAroundBlockWithOutset(world, x, y, z, 4, 0.0625);
		}
	}

	private void onActivityChange(boolean active) {
		//if (!worldObj.isRemote && ReactorOptions.CHUNKLOADING.getState()) {
		//	if (active) {
		//		ChunkManager.instance.loadChunks(worldObj, xCoord, yCoord, zCoord, this);
		//	}
		//	else {
		//		ChunkManager.instance.unloadChunks(worldObj, xCoord, yCoord, zCoord);
		//	}
		//}
	}

	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, 1);
	}

	private void feedWaste(World world, int x, int y, int z) {
		TileEntity te = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (te instanceof TileEntityNuclearCore) {
			for (int i = 4; i < 12; i++) {
				if (inv[i] != null && inv[i].getItem() == ReactorItems.WASTE.getItemInstance()) {
					for (int k = 4; k < 12; k++) {
						if (((TileEntityNuclearCore) te).inv[k] == null) {
							((TileEntityNuclearCore) te).inv[k] = inv[i];
							inv[i] = null;
						}
					}
				}
			}
		}
	}

	@Override
	public final int getInventoryStackLimit() {
		return 1;
	}

	public final int getSizeInventory() {
		return 12;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(int T) {
		temperature = T;
	}

	public boolean feed() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		Block id = world.getBlock(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = this.getAdjacentTileEntity(ForgeDirection.DOWN);
		if (tile instanceof Feedable) {
			if (((Feedable)tile).feedIn(inv[3])) {
				inv[3] = inv[2];
				inv[2] = inv[1];
				inv[1] = inv[0];

				id = world.getBlock(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = this.getAdjacentTileEntity(ForgeDirection.UP);
				if (tile instanceof Feedable) {
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
		for (int i = 0; i < 4; i++) {
			for (int k = 3; k > 0; k--) {
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
		if (inv[3] == null)
			return null;
		else {
			ItemStack is = inv[3].copy();
			inv[3] = null;
			return is;
		}
	}

	public abstract boolean isFissile();

	@Override
	public final boolean canEnterFromSide(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public final boolean canExitToSide(ForgeDirection dir) {
		return dir == ForgeDirection.DOWN;
	}

	protected final boolean isPoisoned() {
		int count = 0;
		for (int i = 4; i < 12; i++) {
			ItemStack is = inv[i];
			if (is != null && is.getItem() == ReactorItems.WASTE.getItemInstance())
				count++;
		}
		return rand.nextInt(9-count) == 0;
	}

	protected void addWaste() {
		boolean flag = false;
		ItemStack waste = WasteManager.getRandomWasteItem();
		for (int i = 4; i < 12 && !flag; i++) {
			ItemStack inslot = inv[i];
			if (inslot == null) {
				inv[i] = waste;
				flag = true;
			}
			else if (ItemStack.areItemStackTagsEqual(waste, inslot) && waste.isItemEqual(inslot) && inv[i].stackSize+waste.stackSize <= waste.getMaxStackSize()) {
				inv[i].stackSize += waste.stackSize;
				flag = true;
			}
		}
	}

	public boolean onNeutron(EntityNeutron e, World world, int x, int y, int z) {
		boolean inactive = activeTimer <= 0;
		activeTimer = 2400; //2 min
		if (inactive)
			this.onActivityChange(true);
		return false;
	}

	protected final void spawnNeutronBurst(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		for (int i = 0; i < 3; i++)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection(), this.getNeutronType()));
	}

	protected final NeutronType getNeutronType() {
		switch(this.getMachine().getReactorType()) {
		case BREEDER:
			return NeutronType.BREEDER;
		case FISSION:
			return NeutronType.FISSION;
		case FUSION:
			return NeutronType.FUSION;
		default:
			return null;
		}
	}

	@Override
	public int getMaxTemperature() {
		return EXPLOSION;
	}

	protected void onMeltdown(World world, int x, int y, int z) {
		MinecraftForge.EVENT_BUS.post(new ReactorMeltdownEvent(world, x, y, z));
		if (world.isRemote)
			return;
		int r = 2;
		for (int i = x-r; i <= x+r; i++) {
			for (int j = y-r; j <= y+r; j++) {
				for (int k = z-r; k <= z+r; k++) {
					ReactorTiles src = ReactorTiles.TEList[this.getIndex()];
					ReactorTiles other = ReactorTiles.getTE(world, i, j, k);
					if (src == other)
						world.setBlock(i, j, k, ReactorBlocks.CORIUMFLOWING.getBlockInstance());
				}
			}
		}
		world.createExplosion(null, x+0.5, y+0.5, z+0.5, 8, false);
		HydrogenExplosion ex = new HydrogenExplosion(world, null, x+0.5, y+0.5, z+0.5, 7);
		ex.doExplosionA();
		ex.doExplosionB(false);
	}

	@Override
	protected void updateTemperature(World world, int x, int y, int z) {
		super.updateTemperature(world, x, y, z);
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		int dT = temperature-Tamb;

		if (dT != 0 && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.air) != null)
			temperature -= (1+dT/32);

		if (dT > 0) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				Block id = world.getBlock(dx, dy, dz);
				int meta = world.getBlockMetadata(dx, dy, dz);/*
				if (id == ReactorTiles.COOLANT.getBlock() && meta == ReactorTiles.COOLANT.getBlockMetadata()) {
					TileEntityWaterCell te = (TileEntityWaterCell)world.getTileEntity(dx, dy, dz);
					if (te.getLiquidState().isWater() && temperature >= 100 && ReikaRandomHelper.doWithChance(40)) {
						te.setLiquidState(LiquidStates.EMPTY);
						temperature -= 20;
					}
				}
				 */
				if (id == this.getTileEntityBlockID() && meta == ReactorTiles.TEList[this.getIndex()].getBlockMetadata()) {
					TileEntityNuclearCore te = (TileEntityNuclearCore)world.getTileEntity(dx, dy, dz);
					int dTemp = temperature-te.temperature;
					if (dTemp > 0) {
						temperature -= dTemp/16;
						te.temperature += dTemp/16;
					}
				}
			}
		}

		if (hydrogen > 0)
			hydrogen--;

		if (temperature >= 500) {
			ReactorAchievements.HOTCORE.triggerAchievement(this.getPlacer());
		}

		if (temperature > this.getMaxTemperature()) {
			this.onMeltdown(world, x, y, z);
			ReactorAchievements.MELTDOWN.triggerAchievement(this.getPlacer());
		}

		if (temperature > HYDROGEN) {
			hydrogen += 1;
			if (hydrogen > 200) {
				this.onMeltdown(world, x, y, z);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		activeTimer = NBT.getInteger("activetick");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("activetick", activeTimer);

	}

}
