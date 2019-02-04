package Reika.ReactorCraft.API;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import net.minecraft.world.World;

public interface MagneticOreOverride {

	public boolean showOnHUD(World world, int x, int y, int z);

	public OreType getOreType(World world, int x, int y, int z);

}
