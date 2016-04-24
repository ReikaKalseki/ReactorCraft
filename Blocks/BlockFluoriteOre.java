/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;

public class BlockFluoriteOre extends BlockFluorite {

	private IIcon rainbowIcon;

	public BlockFluoriteOre(Material par2Material) {
		super(par2Material);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		int count = this.getNumberDrops(fortune);
		for (int i = 0; i < count; i++)
			li.add(ReactorItems.FLUORITE.getStackOfMetadata(this.getMetaDropped(metadata)));
		ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP());
		return li;
	}

	private int getMetaDropped(int metadata) {
		return ReactorOptions.RAINBOW.getState() ? rand.nextInt(FluoriteTypes.colorList.length) : metadata%8;
	}

	private int getNumberDrops(int fortune) {
		if (ReactorOptions.RAINBOW.getState()) {
			return FluoriteTypes.colorList.length+fortune*4+rand.nextInt(11+fortune*6);
		}
		return 1+fortune+rand.nextInt(5+2*fortune);
	}

	private int droppedXP() {
		int factor = ReactorOptions.RAINBOW.getState() ? 6 : 1;
		return ReikaRandomHelper.doWithChance(ReactorOres.FLUORITE.xpDropped*factor) ? 1 : 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return ReactorOptions.RAINBOW.getState() ? rainbowIcon : super.getIcon(s, meta);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tgt, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z)%8;
		return new ItemStack(ReactorBlocks.FLUORITEORE.getBlockInstance(), 1, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int k = 0; k < FluoriteTypes.colorList.length; k++) {
			icons[k] = ico.registerIcon(FluoriteTypes.colorList[k].getOreTextureName());
		}
		rainbowIcon = ico.registerIcon("ReactorCraft:ore/fluorite_rainbow");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) //may need to call lighting updates on generate
	{
		int color = this.getColorType(world, x, y, z).getColor();
		int l = this.isActivated(world, x, y, z) ? 12 : 6;
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(color, l) : l;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}

	@Override
	public float getAmbientOcclusionLightValue()
	{
		return 0.7F;
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z)
	{
		return 0;
	}

}
