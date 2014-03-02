package Reika.ReactorCraft.Auxiliary.Lua;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

public class LuaReactorCheckFuel extends LuaMethod {

	public LuaReactorCheckFuel() {
		super("checkFuel", TileEntityNuclearCore.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		TileEntityNuclearCore tile = (TileEntityNuclearCore)te;
		ReactorTiles r = tile.getMachine();
		int fuel = 0;
		int maxfuel = 1;
		if (r == ReactorTiles.BREEDER) {
			maxfuel = 4*ReactorItems.BREEDERFUEL.getNumberMetadatas();
			for (int i = 0; i < 4; i++) {
				ItemStack is = tile.getStackInSlot(i);
				if (is != null) {
					if (is.itemID == ReactorItems.BREEDERFUEL.getShiftedItemID()) {
						fuel += ReactorItems.BREEDERFUEL.getNumberMetadatas()-1-is.getItemDamage();
					}
				}
			}
		}
		else if (r == ReactorTiles.FUEL) {
			maxfuel = 4*ReactorItems.FUEL.getNumberMetadatas();
			for (int i = 0; i < 4; i++) {
				ItemStack is = tile.getStackInSlot(i);
				if (is != null) {
					if (is.itemID == ReactorItems.FUEL.getShiftedItemID()) {
						fuel += ReactorItems.FUEL.getNumberMetadatas()-1-is.getItemDamage();
					}
					else if (is.itemID == ReactorItems.PLUTONIUM.getShiftedItemID()) {
						fuel += ReactorItems.PLUTONIUM.getNumberMetadatas()-1-is.getItemDamage();
					}
				}
			}
		}
		return new Object[]{String.format("%.3f%s", fuel/(float)maxfuel, "%")};
	}

	@Override
	public String getDocumentation() {
		return "Returns the fuel level of a nuclear fuel core.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

}
