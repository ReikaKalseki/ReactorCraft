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
import Reika.ReactorCraft.Container.ContainerCentrifuge;
import Reika.ReactorCraft.Container.ContainerFuelRod;
import Reika.ReactorCraft.Container.ContainerProcessor;
import Reika.ReactorCraft.Container.ContainerSynthesizer;
import Reika.ReactorCraft.Container.ContainerWasteContainer;
import Reika.ReactorCraft.GUIs.GuiCentrifuge;
import Reika.ReactorCraft.GUIs.GuiFuelRod;
import Reika.ReactorCraft.GUIs.GuiProcessor;
import Reika.ReactorCraft.GUIs.GuiSynthesizer;
import Reika.ReactorCraft.GUIs.GuiWasteContainer;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.TileEntityUProcessor;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import cpw.mods.fml.common.network.IGuiHandler;

public class ReactorGuiHandler implements IGuiHandler {

	public static final ReactorGuiHandler instance = new ReactorGuiHandler();

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		if (r != null) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			switch(r) {
			case FUEL:
				return new ContainerFuelRod(player, (TileEntityFuelRod)te);
			case WASTECONTAINER:
				return new ContainerWasteContainer(player, (TileEntityWasteContainer)te);
			case PROCESSOR:
				return new ContainerProcessor(player, (TileEntityUProcessor)te);
			case CENTRIFUGE:
				return new ContainerCentrifuge(player, (TileEntityCentrifuge)te);
			case SYNTHESIZER:
				return new ContainerSynthesizer(player, (TileEntitySynthesizer)te);
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
			case WASTECONTAINER:
				return new GuiWasteContainer(player, (TileEntityWasteContainer)te);
			case PROCESSOR:
				return new GuiProcessor(player, (TileEntityUProcessor)te);
			case CENTRIFUGE:
				return new GuiCentrifuge(player, (TileEntityCentrifuge)te);
			case SYNTHESIZER:
				return new GuiSynthesizer(player, (TileEntitySynthesizer)te);
			default:
				return null;
			}
		}
		return null;
	}

}
