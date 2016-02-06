/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Container.ContainerCentrifuge;
import Reika.ReactorCraft.Container.ContainerElectrolyzer;
import Reika.ReactorCraft.Container.ContainerNuclearCore;
import Reika.ReactorCraft.Container.ContainerPebbleBed;
import Reika.ReactorCraft.Container.ContainerProcessor;
import Reika.ReactorCraft.Container.ContainerSynthesizer;
import Reika.ReactorCraft.Container.ContainerThoriumCore;
import Reika.ReactorCraft.Container.ContainerWasteContainer;
import Reika.ReactorCraft.Container.ContainerWasteStorage;
import Reika.ReactorCraft.GUIs.GuiCPU;
import Reika.ReactorCraft.GUIs.GuiCentrifuge;
import Reika.ReactorCraft.GUIs.GuiElectrolyzer;
import Reika.ReactorCraft.GUIs.GuiNuclearCore;
import Reika.ReactorCraft.GUIs.GuiPebbleBed;
import Reika.ReactorCraft.GUIs.GuiProcessor;
import Reika.ReactorCraft.GUIs.GuiReactorBook;
import Reika.ReactorCraft.GUIs.GuiReactorBookPage;
import Reika.ReactorCraft.GUIs.GuiSynthesizer;
import Reika.ReactorCraft.GUIs.GuiThoriumCore;
import Reika.ReactorCraft.GUIs.GuiWasteContainer;
import Reika.ReactorCraft.GUIs.GuiWasteStorage;
import Reika.ReactorCraft.Registry.ReactorBook;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityWasteContainer;
import Reika.ReactorCraft.TileEntities.TileEntityWasteStorage;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.Thorium.TileEntityThoriumCore;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;
import cpw.mods.fml.common.network.IGuiHandler;

public class ReactorGuiHandler implements IGuiHandler {

	public static final ReactorGuiHandler instance = new ReactorGuiHandler();

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0) {
			ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
			if (r != null) {
				TileEntity te = world.getTileEntity(x, y, z);
				switch(r) {
					case FUEL:
					case BREEDER:
						return new ContainerNuclearCore(player, (TileEntityNuclearCore)te);
					case THORIUM:
						return new ContainerThoriumCore(player, (TileEntityThoriumCore)te);
					case WASTECONTAINER:
						return new ContainerWasteContainer(player, (TileEntityWasteContainer)te);
					case PROCESSOR:
						return new ContainerProcessor(player, (TileEntityUProcessor)te);
					case CENTRIFUGE:
						return new ContainerCentrifuge(player, (TileEntityCentrifuge)te);
					case SYNTHESIZER:
						return new ContainerSynthesizer(player, (TileEntitySynthesizer)te);
					case ELECTROLYZER:
						return new ContainerElectrolyzer(player, (TileEntityElectrolyzer)te);
					case STORAGE:
						return new ContainerWasteStorage(player, (TileEntityWasteStorage)te);
					case PEBBLEBED:
						return new ContainerPebbleBed(player, (TileEntityPebbleBed)te);
					case CPU:
						int slot = ReikaInventoryHelper.locateIDInInventory(ReactorItems.REMOTE.getItemInstance(), player.inventory);
						return new CoreContainer(player, te).setAlwaysInteractable().addSlotRelay(player.inventory, slot);
					default:
						return null;
				}
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);

		if (ID == 10)
			return new GuiReactorBook(player, world, 0, 0);
		if (ID == 11) {
			return new GuiReactorBook(player, world, ReactorBook.getScreen(r, te), ReactorBook.getPage(r, te));
		}
		if (ID == 12) {
			return new GuiReactorBookPage(player, world, ReactorBook.getScreen(r, te), ReactorBook.getPage(r, te));
		}

		if (r != null) {
			switch(r) {
				case FUEL:
				case BREEDER:
					return new GuiNuclearCore(player, (TileEntityNuclearCore)te);
				case THORIUM:
					return new GuiThoriumCore(player, (TileEntityThoriumCore)te);
				case WASTECONTAINER:
					return new GuiWasteContainer(player, (TileEntityWasteContainer)te);
				case PROCESSOR:
					return new GuiProcessor(player, (TileEntityUProcessor)te);
				case CENTRIFUGE:
					return new GuiCentrifuge(player, (TileEntityCentrifuge)te);
				case SYNTHESIZER:
					return new GuiSynthesizer(player, (TileEntitySynthesizer)te);
				case ELECTROLYZER:
					return new GuiElectrolyzer(player, (TileEntityElectrolyzer)te);
				case STORAGE:
					return new GuiWasteStorage(player, (TileEntityWasteStorage)te);
				case PEBBLEBED:
					return new GuiPebbleBed(player, (TileEntityPebbleBed)te);
				case CPU:
					return new GuiCPU(player, (TileEntityCPU)te);
				default:
					return null;
			}
		}
		return null;
	}

}
