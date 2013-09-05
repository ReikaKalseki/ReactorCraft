package Reika.ReactorCraft.TileEntities;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ReactorCraft.Base.TileEntityInventoriedReactorBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class TileEntityWasteContainer extends TileEntityInventoriedReactorBase {

	private ItemStack[] inv = new ItemStack[15];

	@Override
	public int getIndex() {
		return ReactorTiles.WASTECONTAINER.ordinal();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		thermalTicker.update();

		if (thermalTicker.checkCap()) {
			int waste = this.countWaste();
		}
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean hasWaste() {
		return this.countWaste() > 0;
	}

	public int countWaste() {
		int count = 0;
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == ReactorItems.WASTE.getShiftedItemID()) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return itemstack.itemID == ReactorItems.DEPLETED.getShiftedItemID();
	}

	@Override
	public int getSizeInventory() {
		return 15;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return itemstack.itemID == ReactorItems.WASTE.getShiftedItemID();
	}

}
