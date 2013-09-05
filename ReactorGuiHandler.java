/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ReactorCraft.Container.ContainerFuelRod;
import Reika.ReactorCraft.GUIs.GuiFuelRod;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import cpw.mods.fml.common.network.IGuiHandler;

public class ReactorGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		if (r != null) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			switch(r) {
			case FUEL:
				return new ContainerFuelRod(player, (TileEntityFuelRod)te);
			default:
				return null;
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		if (r != null) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			switch(r) {
			case FUEL:
				return new GuiFuelRod(player, (TileEntityFuelRod)te);
			default:
				return null;
			}
		}
		return null;
	}

}
