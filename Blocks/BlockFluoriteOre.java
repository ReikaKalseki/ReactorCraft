/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;

public class BlockFluoriteOre extends BlockFluorite {

	private Icon rainbowIcon;

	public BlockFluoriteOre(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		int count = this.getNumberDrops(fortune);
		for (int i = 0; i < count; i++)
			li.add(ReactorItems.FLUORITE.getStackOfMetadata(this.getMetaDropped(metadata)));
		ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP());
		return li;
	}

	private int getMetaDropped(int metadata) {
		return ReactorOptions.RAINBOW.getState() ? rand.nextInt(FluoriteTypes.colorList.length) : metadata;
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
	public Icon getIcon(int s, int meta) {
		return ReactorOptions.RAINBOW.getState() ? rainbowIcon : super.getIcon(s, meta);
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
		rainbowIcon = ico.registerIcon("ReactorCraft:fluorite_rainbow");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) //may need to call lighting updates on generate
	{
		return this.isActivated(world, x, y, z) ? 12 : 6;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}

	@Override
	public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return 0.7F;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z)
	{
		return 0;
	}

}
