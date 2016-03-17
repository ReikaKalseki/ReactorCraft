/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.ReactorCraft.Blocks.Multi.BlockTurbineMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityReactorGenerator;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public enum ReactorStructures {

	HEATER(ReactorTiles.HEATER),
	GENERATOR(ReactorTiles.GENERATOR),
	INJECTOR(ReactorTiles.INJECTOR),
	SOLENOID(ReactorTiles.SOLENOID),
	HPTURBINE(ReactorTiles.BIGTURBINE),
	FLYWHEEL(ReactorTiles.FLYWHEEL);

	public static ReactorStructures[] structureList = values();

	private StructureRenderer render;
	private final ReactorTiles tile;

	private ReactorStructures(ReactorTiles r) {
		tile = r;
	}

	@SideOnly(Side.CLIENT)
	public StructureRenderer getRenderer() {
		if (render == null || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			FilledBlockArray f = this.getStructure(Minecraft.getMinecraft().theWorld, 0, 0, 0, ForgeDirection.EAST);
			render = new StructureRenderer(f);

			this.addOverrides();
		}
		return render;
	}

	private void addOverrides() {
		render.addOverride(new ItemStack(tile.getBlock(), 1, tile.getBlockMetadata()), tile.getCraftedProduct());

		render.addOverride(new ItemStack(ReactorTiles.STEAMLINE.getBlock(), 1, ReactorTiles.STEAMLINE.getBlockMetadata()), ReactorTiles.STEAMLINE.getCraftedProduct());
	}

	public FilledBlockArray getStructure(World world, int x, int y, int z, ForgeDirection dir) {
		switch(this){
			case FLYWHEEL:
				return this.getFlywheelStructure(world, x, y, z, dir);
			case GENERATOR:
				return this.getGeneratorStructure(world, x, y, z, dir);
			case HEATER:
				return this.getHeaterStructure(world, x, y, z, dir);
			case HPTURBINE:
				return this.getTurbineStructure(world, x, y, z, dir);
			case INJECTOR:
				return this.getInjectorStructure(world, x, y, z, dir);
			case SOLENOID:
				return this.getSolenoidStructure(world, x, y, z, dir);
			default:
				return null;
		}
	}

	private FilledBlockArray getFlywheelStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);
		int midX = x;
		int midY = y;
		int midZ = z;
		array.setBlock(midX, midY, midZ, ReactorTiles.FLYWHEEL.getBlock(), ReactorTiles.FLYWHEEL.getBlockMetadata());
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		Block b = ReactorBlocks.FLYWHEELMULTI.getBlockInstance();

		for (int i = 1; i <= 2; i++) {
			int dx = midX+left.offsetX*i;
			int dz = midZ+left.offsetZ*i;
			int m = i == 1 ? 0 : 2;
			array.setBlock(dx, midY, dz, b, m);

			dx = midX-left.offsetX*i;
			dz = midZ-left.offsetZ*i;
			array.setBlock(dx, midY, dz, b, m);
			array.setBlock(midX, midY-i, midZ, b, m);
			array.setBlock(midX, midY+i, midZ, b, m);
		}

		int dx = midX+left.offsetX;
		int dz = midZ+left.offsetZ;
		array.setBlock(dx, midY+1, dz, b, 1);
		array.setBlock(dx, midY-1, dz, b, 1);

		dx = midX-left.offsetX;
		dz = midZ-left.offsetZ;
		array.setBlock(dx, midY+1, dz, b, 1);
		array.setBlock(dx, midY-1, dz, b, 1);

		dx = midX+left.offsetX;
		dz = midZ+left.offsetZ;
		array.setBlock(dx, midY+2, dz, b, 2);
		array.setBlock(dx, midY-2, dz, b, 2);

		dx = midX-left.offsetX;
		dz = midZ-left.offsetZ;
		array.setBlock(dx, midY+2, dz, b, 2);
		array.setBlock(dx, midY-2, dz, b, 2);

		dx = midX+left.offsetX*2;
		dz = midZ+left.offsetZ*2;
		array.setBlock(dx, midY+1, dz, b, 2);
		array.setBlock(dx, midY-1, dz, b, 2);

		dx = midX-left.offsetX*2;
		dz = midZ-left.offsetZ*2;
		array.setBlock(dx, midY+1, dz, b, 2);
		array.setBlock(dx, midY-1, dz, b, 2);
		return array;
	}

	private FilledBlockArray getGeneratorStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.GENERATORMULTI.getBlockInstance();

		int l = TileEntityReactorGenerator.getGeneratorLength()-1;

		int dx = 0;
		int dz = 0;

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int i = 0; i < l; i++) {
			int seekmeta = i < 2 ? 3 : 1;
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				array.setBlock(ddx, dy, ddz, b, seekmeta);
				array.setBlock(ddx2, dy, ddz2, b, seekmeta);
				array.setBlock(dx, dy, dz, b, seekmeta);
			}
		}

		for (int i = 0; i < l; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			int ddx = dx+left.offsetX;
			int ddx2 = dx-left.offsetX;
			int ddz = dz+left.offsetZ;
			int ddz2 = dz-left.offsetZ;
			int seekmeta = 2;
			for (int k = -2; k <= 2; k += 4) {
				int dy = y+k;
				if (i == 1 && k == 2)
					seekmeta = 3;
				array.setBlock(ddx, dy, ddz, b, 2);
				array.setBlock(ddx2, dy, ddz2, b, 2);
				array.setBlock(dx, dy, dz, b, seekmeta);
			}

			ddx = dx+left.offsetX*2;
			ddx2 = dx-left.offsetX*2;
			ddz = dz+left.offsetZ*2;
			ddz2 = dz-left.offsetZ*2;

			for (int k = -1; k <= 1; k++) {
				int dy = y+k;
				array.setBlock(ddx, dy, ddz, b, 2);
				array.setBlock(ddx2, dy, ddz2, b, 2);
			}
		}

		dx = x+dir.offsetX*l;
		dz = z+dir.offsetZ*l;
		for (int k = -2; k <= 2; k++) {
			int dy = y+k;
			for (int m = -2; m <= 2; m++) {
				if ((Math.abs(k) != 2 || Math.abs(m) != 2) && (k != 0 || m != 0)) {
					int ddx = dx+left.offsetX*m;
					int ddz = dz+left.offsetZ*m;
					array.setBlock(ddx, dy, ddz, b, 2);
				}
			}
		}
		for (int i = 0; i < 2; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;

			int ddx = dx+left.offsetX*2;
			int ddz = dz+left.offsetZ*2;
			int ddx2 = dx-left.offsetX*2;
			int ddz2 = dz-left.offsetZ*2;
			array.setBlock(ddx, y+2, ddz, b, 2);
			array.setBlock(ddx2, y+2, ddz2, b, 2);

			array.setBlock(ddx, y-2, ddz, b, 2);
			array.setBlock(ddx2, y-2, ddz2, b, 2);
		}

		for (int i = 0; i < l; i++) {
			dx = x+dir.offsetX*i;
			dz = z+dir.offsetZ*i;
			array.setBlock(dx, y, dz, b, 0);
		}

		dx = x+dir.offsetX*l;
		dz = z+dir.offsetZ*l;
		array.setBlock(dx, y, dz, ReactorTiles.GENERATOR.getBlock(), ReactorTiles.GENERATOR.getBlockMetadata());

		return array;
	}

	private FilledBlockArray getHeaterStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.HEATERMULTI.getBlockInstance();

		for (int i = 0; i < 5; i++) {
			for (int k = 0; k < 5; k++) {
				for (int h = 0; h < 5; h++) {
					boolean corner = (i == 0 || i == 4) && (k == 0 || k == 4) && (h == 0 || h == 4);
					boolean edge = i == 0 || i == 4 || k == 0 || k == 4;
					int m = corner ? 2 : edge ? 3 : 4;
					array.setBlock(x+i, y+h, z+k, b, m);
					if (h > 0 && !edge) {
						array.setBlock(x+i, y+h, z+k, b, 1);
						if (i == 2 && k == 2 && h >= 2) {
							ReactorTiles r = h > 2 ? ReactorTiles.MAGNETPIPE : ReactorTiles.HEATER;
							array.setBlock(x+i, y+h, z+k, r.getBlock(), r.getBlockMetadata());
						}
					}
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				boolean corner = (i == 0 || i == 2) && (k == 0 || k == 2);
				boolean edge = i == 0 || i == 2 || k == 0 || k == 2;
				int m = corner ? 2 : edge ? 3 : 4;
				array.setBlock(x+1+i, y+5, z+1+k, b, m);
			}
		}

		for (int i = 1; i <= 3; i++) {
			for (int k = 1; k <= 3; k++) {
				array.setBlock(x+i, y+k, z+0, b, 4);
				array.setBlock(x+i, y+k, z+4, b, 4);
				array.setBlock(x+0, y+k, z+i, b, 4);
				array.setBlock(x+4, y+k, z+i, b, 4);
			}
		}

		array.setBlock(x+2, y+5, z+2, ReactorTiles.MAGNETPIPE.getBlock(), ReactorTiles.MAGNETPIPE.getBlockMetadata());
		array.setBlock(x+2, y+6, z+2, ReactorTiles.MAGNETPIPE.getBlock(), ReactorTiles.MAGNETPIPE.getBlockMetadata());

		for (int i = 0; i < 5; i++) {
			if (i != 2)
				array.setBlock(x+i, y+2, z+2, MachineRegistry.PIPE.getBlock(), MachineRegistry.PIPE.getBlockMetadata());
		}

		array.setBlock(x+2, y+2, z+3, b, 0);
		array.setBlock(x+2, y+2, z+4, b, 0);

		return array;
	}

	private FilledBlockArray getTurbineStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);

		array.fillFrom(((BlockTurbineMulti)ReactorBlocks.TURBINEMULTI.getBlockInstance()).getBlueprint(), x, y, z, dir);

		for (int i = 0; i <= 8; i++) {
			int dx = x+dir.offsetX*i;
			int dz = z+dir.offsetZ*i+left.offsetZ*5;
			ReactorTiles r = i >= 7 ? ReactorTiles.STEAMLINE : ReactorTiles.BIGTURBINE;
			array.setBlock(dx, y+5, dz, r.getBlock(), r.getBlockMetadata());
		}

		return array;
	}

	private FilledBlockArray getInjectorStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.INJECTORMULTI.getBlockInstance();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);

		for (int i = 0; i <= 4; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y+3, z+dir.offsetZ*i+left.offsetZ, b, 4);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y+3, z+dir.offsetZ*i-left.offsetZ, b, 4);
		}
		for (int i = 5; i <= 6; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ, b, 4);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ, b, 4);
		}
		for (int i = 7; i <= 8; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ, b, 4);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ, b, 4);
		}

		for (int i = 0; i <= 8; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y-1, z+dir.offsetZ*i+left.offsetZ, b, 1);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y-1, z+dir.offsetZ*i-left.offsetZ, b, 1);
		}

		for (int k = 0; k <= 2; k++) {
			array.setBlock(x+left.offsetX, y+k, z+left.offsetZ, b, 6);
			array.setBlock(x-left.offsetX, y+k, z-left.offsetZ, b, 6);
		}
		array.setBlock(x+left.offsetX+dir.offsetX*8, y, z+left.offsetZ+dir.offsetZ*8, b, 6);
		array.setBlock(x-left.offsetX+dir.offsetX*8, y, z-left.offsetZ+dir.offsetZ*8, b, 6);

		for (int i = 0; i <= 4; i++) {
			array.setBlock(x+dir.offsetX*i, y+3, z+dir.offsetZ*i, b, 3);
		}
		for (int i = 5; i <= 6; i++) {
			array.setBlock(x+dir.offsetX*i, y+2, z+dir.offsetZ*i, b, 3);
		}
		for (int i = 7; i <= 8; i++) {
			array.setBlock(x+dir.offsetX*i, y+1, z+dir.offsetZ*i, b, 3);
		}

		for (int i = 0; i <= 8; i++) {
			array.setBlock(x+dir.offsetX*i, y-1, z+dir.offsetZ*i, b, 0);
		}

		for (int i = 1; i <= 1; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ, b, 2);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ, b, 2);
		}
		for (int i = 3; i <= 7; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y, z+dir.offsetZ*i+left.offsetZ, b, 2);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y, z+dir.offsetZ*i-left.offsetZ, b, 2);
		}
		for (int i = 1; i <= 6; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y+1, z+dir.offsetZ*i+left.offsetZ, b, 2);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y+1, z+dir.offsetZ*i-left.offsetZ, b, 2);
		}
		for (int i = 1; i <= 4; i++) {
			array.setBlock(x+dir.offsetX*i+left.offsetX, y+2, z+dir.offsetZ*i+left.offsetZ, b, 2);
			array.setBlock(x+dir.offsetX*i-left.offsetX, y+2, z+dir.offsetZ*i-left.offsetZ, b, 2);
		}
		for (int i = 0; i <= 2; i++) {
			array.setBlock(x, y+i, z, b, 5);
		}

		for (int i = 1; i <= 1; i++) {
			array.setBlock(x+dir.offsetX*i, y, z+dir.offsetZ*i, b, 7);
		}
		for (int i = 1; i <= 6; i++) {
			array.setBlock(x+dir.offsetX*i, y+1, z+dir.offsetZ*i, b, 7);
		}
		for (int i = 1; i <= 4; i++) {
			array.setBlock(x+dir.offsetX*i, y+2, z+dir.offsetZ*i, b, 7);
		}

		for (int i = 3; i <= 8; i++) {
			array.setBlock(x+dir.offsetX*i, y, z+dir.offsetZ*i, ReactorTiles.MAGNETPIPE.getBlock(), ReactorTiles.MAGNETPIPE.getBlockMetadata());
		}
		array.setBlock(x+dir.offsetX*2, y, z+dir.offsetZ*2, ReactorTiles.INJECTOR.getBlock(), ReactorTiles.INJECTOR.getBlockMetadata());

		return array;
	}

	private FilledBlockArray getSolenoidStructure(World world, int x, int y, int z, ForgeDirection dir) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ReactorBlocks.SOLENOIDMULTI.getBlockInstance();

		for (int i = -1; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || j != 0 || k != 0) {
						array.setBlock(x+i, y+j, z+k, b, 5);
					}
				}
			}
		}

		for (int i = 2; i <= 7; i++) {
			array.setBlock(x+i, y, z, b, 4);
			array.setBlock(x-i, y, z, b, 4);
			array.setBlock(x, y, z+i, b, 4);
			array.setBlock(x, y, z-i, b, 4);

			if (i < 6) {
				array.setBlock(x+i, y, z+i, b, 4);
				array.setBlock(x-i, y, z+i, b, 4);
				array.setBlock(x+i, y, z-i, b, 4);
				array.setBlock(x-i, y, z-i, b, 4);
			}
		}

		array.setBlock(x-6, y+1, z-6, b, 1);
		array.setBlock(x-6, y, z-6, b, 3);
		array.setBlock(x-6, y-1, z-6, b, 1);

		array.setBlock(x+6, y+1, z-6, b, 1);
		array.setBlock(x+6, y, z-6, b, 3);
		array.setBlock(x+6, y-1, z-6, b, 1);

		array.setBlock(x-6, y+1, z+6, b, 1);
		array.setBlock(x-6, y, z+6, b, 3);
		array.setBlock(x-6, y-1, z+6, b, 1);

		array.setBlock(x+6, y+1, z+6, b, 1);
		array.setBlock(x+6, y, z+6, b, 3);
		array.setBlock(x+6, y-1, z+6, b, 1);

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 3 : 2;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y-1;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		for (int i = -5; i <= 5; i++) {
			int d = Math.abs(i) >= 4 ? 7 : 8;
			int dx = x-d;
			int dy = y+1;
			int dz = z+i;
			int m = Math.abs(i) >= 3 ? 1 : 0;

			array.setBlock(dx, dy, dz, b, m);

			dx = x+d;
			array.setBlock(dx, dy, dz, b, m);

			dx = x+i;
			dz = z+d;
			array.setBlock(dx, dy, dz, b, m);

			dz = z-d;
			array.setBlock(dx, dy, dz, b, m);
		}

		array.setBlock(array.getMidX(), 0, array.getMidZ(), ReactorTiles.SOLENOID.getBlock(), ReactorTiles.SOLENOID.getBlockMetadata());

		return array;
	}

	public String getName() {
		return StatCollector.translateToLocal("reactorstruct."+this.name().toLowerCase(Locale.ENGLISH));
	}

}
