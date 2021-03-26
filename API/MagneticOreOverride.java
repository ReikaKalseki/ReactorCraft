package Reika.ReactorCraft.API;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface MagneticOreOverride {

	public boolean showOnHUD(World world, int x, int y, int z, EntityPlayer ep);

	public IIcon[] getRenderIcons(IBlockAccess world, int x, int y, int z);

}
