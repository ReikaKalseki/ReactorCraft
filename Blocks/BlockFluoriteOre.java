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
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
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
		Random r = new Random();
		int count = 1+fortune+r.nextInt(5+2*fortune);
		for (int i = 0; i < count; i++)
			li.add(ReactorItems.FLUORITE.getStackOfMetadata(metadata));
		ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP());
		return li;
	}

	private int droppedXP() {
		return ReikaMathLibrary.doWithChance(ReactorOres.FLUORITE.xpDropped) ? 1 : 0;
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
