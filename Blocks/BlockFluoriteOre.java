package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOres;

public class BlockFluoriteOre extends BlockFluorite {

	public BlockFluoriteOre(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();

		ReactorOres ore = ReactorOres.getOre(world, x, y, z);
		Random r = new Random();
		int count = 1+fortune+r.nextInt(5+2*fortune);
		for (int i = 0; i < count; i++)
			li.add(ReactorItems.FLUORITE.getStackOfMetadata(metadata));
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tgt, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(ReactorBlocks.FLUORITEORE.getBlockID(), 1, meta);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int k = 0; k < FluoriteTypes.colorList.length; k++) {
			icons[k] = ico.registerIcon(FluoriteTypes.colorList[k].getOreTextureName());
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) //may need to call lighting updates on generate
	{
		return this.isActivated(world, x, y, z) ? 12 : 6;
	}

}
