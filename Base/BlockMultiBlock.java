package Reika.ReactorCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ReactorCraft.ReactorCraft;

public abstract class BlockMultiBlock extends Block {

	private final Icon[][] icons = new Icon[this.getNumberVariants()][6];
	private static final ForgeDirection[] dirs = ForgeDirection.values();

	public BlockMultiBlock(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(ReactorCraft.tabRctr);
	}

	public abstract int getNumberVariants();

	public abstract boolean checkForFullMultiBlock(World world, int x, int y, int z);

	@Override
	public final Icon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	protected abstract String getIconBaseName();

	protected int getTextureOrdinalForSide(int meta, ForgeDirection side) {
		return side.ordinal();
	}

	protected int getTextureMetaForMeta(int meta) {
		return meta;
	}

	@Override
	public final void registerIcons(IconRegister ico) {
		for (int i = 0; i < this.getNumberVariants(); i++) {
			for (int k = 0; k < 6; k++) {
				int m = this.getTextureMetaForMeta(i);
				int n = this.getTextureOrdinalForSide(m, dirs[k]);
				String name = this.getIconBaseName()+"_"+i+"_"+dirs[n].name().toLowerCase();
				icons[i][k] = ico.registerIcon("reactorcraft:multi/"+name);
			}
		}
	}

}
