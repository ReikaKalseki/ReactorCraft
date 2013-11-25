package Reika.ReactorCraft.Base;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Auxiliary.Feedable;
import Reika.ReactorCraft.Auxiliary.HydrogenExplosion;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Auxiliary.WasteManager;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.TileEntityWaterCell.LiquidStates;

public abstract class TileEntityNuclearCore extends TileEntityInventoriedReactorBase implements ReactorCoreTE, Temperatured, Feedable {

	protected ItemStack[] inv = new ItemStack[12];

	protected StepTimer tempTimer = new StepTimer(20);

	protected int hydrogen = 0;

	public static final int CLADDING = 800;
	public static final int HYDROGEN = 1400;
	public static final int EXPLOSION = 1800;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.isFissile() && rand.nextInt(20) == 0)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
		//ReikaInventoryHelper.addToIInv(ReactorItems.FUEL.getStackOf(), this);
		this.feed();

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
		int id = world.getBlockId(x, y-1, z);
		int meta = world.getBlockMetadata(x, y-1, z);
		TileEntity tile = world.getBlockTileEntity(x, y-1, z);
		if (tile instanceof Feedable) {
			if (((Feedable)tile).feedIn(inv[3])) {
				inv[3] = inv[2];
				inv[2] = inv[1];
				inv[1] = inv[0];

				id = world.getBlockId(x, y+1, z);
				meta = world.getBlockMetadata(x, y+1, z);
				tile = world.getBlockTileEntity(x, y+1, z);
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
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		NBTTagList nbttaglist = NBT.getTagList("Items");
		inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length)
			{
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

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
			if (is != null && is.itemID == ReactorItems.WASTE.getShiftedItemID())
				count++;
		}
		return rand.nextInt(9-count) == 0;
	}

	@Override
	public final ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public final void setInventorySlotContents(int i, ItemStack is) {
		inv[i] = is;
	}

	protected void addWaste() {
		boolean flag = false;
		ItemStack waste = WasteManager.getRandomWasteItem();
		ReikaJavaLibrary.pConsole(waste.getDisplayName());
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

	protected void spawnNeutronBurst(World world, int x, int y, int z) {
		for (int i = 0; i < 3; i++)
			world.spawnEntityInWorld(new EntityNeutron(world, x, y, z, this.getRandomDirection()));
	}

	@Override
	public int getMaxTemperature() {
		return EXPLOSION;
	}

	protected void onMeltdown(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		int r = 2;
		for (int i = x-r; i <= x+r; i++) {
			for (int j = y-r; j <= y+r; j++) {
				for (int k = z-r; k <= z+r; k++) {
					int id = world.getBlockId(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					if (id == this.getTileEntityBlockID() && meta == ReactorTiles.FUEL.getBlockMetadata())
						world.setBlock(i, j, k, ReactorBlocks.CORIUMFLOWING.getBlockID());
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
		int Tamb = ReikaWorldHelper.getBiomeTemp(world, x, z);
		int dT = temperature-Tamb;

		if (dT != 0 && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, 0) != null)
			temperature -= (1+dT/32);

		if (dT > 0) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = dir.offsetX;
				int dy = dir.offsetY;
				int dz = dir.offsetZ;
				int id = world.getBlockId(dx, dy, dz);
				int meta = world.getBlockMetadata(dx, dy, dz);
				if (id == ReactorTiles.COOLANT.getBlockID() && meta == ReactorTiles.COOLANT.getBlockMetadata()) {
					TileEntityWaterCell te = (TileEntityWaterCell)world.getBlockTileEntity(dx, dy, dz);
					if (te.getLiquidState().isWater() && temperature >= 100 && ReikaRandomHelper.doWithChance(40)) {
						te.setLiquidState(LiquidStates.EMPTY);
						temperature -= 20;
					}
				}
			}
		}

		if (hydrogen > 0)
			hydrogen--;

		if (temperature > EXPLOSION) {
			this.onMeltdown(world, x, y, z);
		}
		if (temperature > HYDROGEN) {
			hydrogen += 1;
			if (hydrogen > 200) {
				this.onMeltdown(world, x, y, z);
			}
		}

		/*
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(3, 3, 3);
		List<EntityNeutron> inbox = world.getEntitiesWithinAABB(EntityNeutron.class, box);
		if (inbox.size() > 175) {
			this.onMeltdown(world, x, y, z);
		}*/
	}

}
