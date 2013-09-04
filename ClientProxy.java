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

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Instantiable.ItemSpriteSheetRenderer;
import Reika.ReactorCraft.Registry.ReactorItems;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy {

	public static final ItemSpriteSheetRenderer[] items = {
		new ItemSpriteSheetRenderer(ReactorCraft.instance, ReactorCraft.class, "Textures/Items/items1.png", ""),
	};

	@Override
	public void registerSounds() {
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(ReactorCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerRenderers() {
		this.loadModels();

		this.registerSpriteSheets();
	}

	private void registerSpriteSheets() {

		for (int i = 0; i < ReactorItems.itemList.length; i++) {
			if (ReactorItems.itemList[i].getSpriteIndex() > -1)
			MinecraftForgeClient.registerItemRenderer(ReactorItems.itemList[i].getShiftedItemID(), items[ReactorItems.itemList[i].getSpriteSheet()]);
		}
	}

	public void loadModels() {

	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
