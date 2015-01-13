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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TritiumLampRenderer;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.RotaryCraft.Registry.BlockRegistry;

public class BlockTritiumLamp extends Block {

	private final IIcon[] icons = new IIcon[FluoriteTypes.colorList.length];
	private IIcon bottom;
	private IIcon top;
	private IIcon frame;

	public BlockTritiumLamp(Material mat) {
		super(mat);
		this.setCreativeTab(ReactorCraft.tabRctr);
		this.setHardness(1.5F);
		this.setResistance(8);
		stepSound = soundTypeGlass;
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		if (iba.getBlockMetadata(x, y, z) < FluoriteTypes.colorList.length)
			return 0;
		FluoriteTypes color = this.getColor(iba, x, y, z);
		return ModList.COLORLIGHT.isLoaded() ? color.blue << 15 | color.green << 10 | color.red << 5 | 15 : 15;
	}

	private FluoriteTypes getColor(IBlockAccess iba, int x, int y, int z) {
		//TileEntityTritiumLamp te = (TileEntityTritiumLamp)iba.getTileEntity(x, y, z);
		//return te != null ? te.getColor() : FluoriteTypes.WHITE;
		return FluoriteTypes.colorList[iba.getBlockMetadata(x, y, z)%FluoriteTypes.colorList.length];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		TritiumLampRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ReactorCraft.proxy.lampRender;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = ico.registerIcon("reactorcraft:lamp/"+FluoriteTypes.colorList[i].getColorName());
		}
		frame = ico.registerIcon("reactorcraft:lamp/frame");
		top = ico.registerIcon("reactorcraft:lamp/top");
		bottom = ico.registerIcon("reactorcraft:lamp/bottom");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta%FluoriteTypes.colorList.length];
	}

	public IIcon getFrameIcon() {
		return frame;
	}

	public IIcon getTopIcon() {
		return top;
	}

	public IIcon getBottomIcon() {
		return bottom;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityTritiumLamp();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityTritiumLamp te = new TileEntityTritiumLamp();
		world.setTileEntity(x, y, z, te);
		if (this.getLightValue(world, x, y, z) > 0)
			te.onCreate();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		TileEntityTritiumLamp te = (TileEntityTritiumLamp)world.getTileEntity(x, y, z);
		te.onBreak();
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	public static class TileEntityTritiumLamp extends TileEntity {

		private BlockArray blocks = new BlockArray();

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			blocks.writeToNBT("blocks", NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			blocks.readFromNBT("blocks", NBT);
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}

		public void onBreak() {
			int r = 24;
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				int x = xyz[0];
				int y = xyz[1];
				int z = xyz[2];
				if (worldObj.getBlock(x, y, z) == BlockRegistry.LIGHT.getBlockInstance()) {
					worldObj.setBlockToAir(x, y, z);
				}
			}
		}

		public void onCreate() {
			int r = 16;
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						if (ReikaMathLibrary.py3d(i, j, k) <= r) {
							int x = xCoord+i;
							int y = yCoord+j;
							int z = zCoord+k;
							if (worldObj.getBlock(x, y, z) == Blocks.air) {
								worldObj.setBlock(x, y, z, BlockRegistry.LIGHT.getBlockInstance(), 15, 3);
								worldObj.markBlockForUpdate(x, y, z);
								blocks.addBlockCoordinate(x, y, z);
							}
							else {

							}
						}
					}
				}
			}
		}

	}

}
