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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.FluoriteTypes;

public class BlockFluorite extends Block {

	protected Icon[] icons = new Icon[FluoriteTypes.colorList.length];

	public BlockFluorite(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setHardness(1.2F);
		this.setResistance(4F);
		this.setCreativeTab(ReactorCraft.tabRctr);
		this.setTickRandomly(true);
	}

	@Override
	public int tickRate(World world)
	{
		return 20;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r)
	{
		if (this.isActivated(world, x, y, z))
			world.setBlock(x, y, z, blockID, this.getColorType(world, x, y, z).ordinal(), 3);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int k = 0; k < FluoriteTypes.colorList.length; k++) {
			icons[k] = ico.registerIcon(FluoriteTypes.colorList[k].getBlockTextureName());
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta%FluoriteTypes.colorList.length];
	}

	@Override
	public int damageDropped(int meta) {
		return meta%FluoriteTypes.colorList.length;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		return this.isActivated(world, x, y, z) ? 15 : 0;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double offset = 0.125;
		int meta = world.getBlockMetadata(x, y, z);
		FluoriteTypes fl = this.getColorType(world, x, y, z);
		double r = fl.red/255D;
		double g = fl.green/255D;
		double b = fl.blue/255D;
		//ReikaJavaLibrary.pConsole(r+":"+g+":"+b);
		if (this.isActivated(world, x, y, z)) {
			for (int i = 0; i < 4; i++)
				world.spawnParticle("reddust", x-offset+rand.nextDouble()*(1+offset*2), y-offset+rand.nextDouble()*(1+offset*2), z-offset+rand.nextDouble()*(1+offset*2), r, g, b);
		}
	}

	public boolean isActivated(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta >= FluoriteTypes.colorList.length;
	}

	public FluoriteTypes getColorType(World world, int x, int y, int z) {
		return FluoriteTypes.colorList[world.getBlockMetadata(x, y, z)%FluoriteTypes.colorList.length];
	}
}
