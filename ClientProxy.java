/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController.Donator;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerSpecificRenderer;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.ReactorCraft.Auxiliary.DonatorToroidRender;
import Reika.ReactorCraft.Auxiliary.ReactorRenderList;
import Reika.ReactorCraft.Entities.EntityFusion;
import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Entities.EntityRadiation;
import Reika.ReactorCraft.Entities.RenderFusion;
import Reika.ReactorCraft.Entities.RenderNeutron;
import Reika.ReactorCraft.Entities.RenderPlasma;
import Reika.ReactorCraft.Entities.RenderRadiation;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public static final ItemSpriteSheetRenderer[] items = {
			new ItemSpriteSheetRenderer(ReactorCraft.instance, ReactorCraft.class, "Textures/Items/items1.png"),
	};

	private static final ReactorItemRenderer reactor = new ReactorItemRenderer();
	public static SteamLineRenderer line;
	public static TritiumLampRenderer lamp;

	private static final HashMap<ReactorItems, String> armorAssets = new HashMap();

	@Override
	public void addArmorRenders() {
		hazmat = RenderingRegistry.addNewArmourRendererPrefix("hazmat");

		addArmorTexture(ReactorItems.HAZBOOTS, "/Reika/ReactorCraft/Textures/Misc/haz_1.png");
		addArmorTexture(ReactorItems.HAZHELMET, "/Reika/ReactorCraft/Textures/Misc/haz_1.png");
		addArmorTexture(ReactorItems.HAZCHEST, "/Reika/ReactorCraft/Textures/Misc/haz_1.png");
		addArmorTexture(ReactorItems.HAZLEGS, "/Reika/ReactorCraft/Textures/Misc/haz_2.png");
	}

	private static void addArmorTexture(ReactorItems item, String tex) {
		ReactorCraft.logger.log("Adding armor texture for "+item+": "+tex);
		String[] s = tex.split("/");
		String file = s[s.length-1];
		String defaultTex = "reactorcraft:textures/models/armor/"+file;
		armorAssets.put(item, defaultTex);
	}

	public static String getArmorTextureAsset(ReactorItems item) {
		return armorAssets.get(item);
	}

	@Override
	public void registerSounds() {
		sounds.register();
	}

	@Override
	public void registerRenderers() {
		if (DragonOptions.NORENDERS.getState()) {
			ReactorCraft.logger.log("Disabling all machine renders for FPS and lag profiling.");
		}
		else {
			this.loadModels();
		}

		this.addArmorRenders();

		lineRender = RenderingRegistry.getNextAvailableRenderId();
		line = new SteamLineRenderer(lineRender);
		RenderingRegistry.registerBlockHandler(lineRender, line);

		lampRender = RenderingRegistry.getNextAvailableRenderId();
		lamp = new TritiumLampRenderer(lampRender);
		RenderingRegistry.registerBlockHandler(lampRender, lamp);

		this.registerSpriteSheets();

		RenderingRegistry.registerEntityRenderingHandler(EntityNeutron.class, new RenderNeutron());
		RenderingRegistry.registerEntityRenderingHandler(EntityRadiation.class, new RenderRadiation());
		RenderingRegistry.registerEntityRenderingHandler(EntityPlasma.class, new RenderPlasma());
		RenderingRegistry.registerEntityRenderingHandler(EntityFusion.class, new RenderFusion());
	}

	private void registerSpriteSheets() {

		for (int i = 0; i < ReactorItems.itemList.length; i++) {
			if (ReactorItems.itemList[i].getSpriteIndex() > -1)
				MinecraftForgeClient.registerItemRenderer(ReactorItems.itemList[i].getItemInstance(), items[ReactorItems.itemList[i].getSpriteSheet()]);
		}
	}

	public void loadModels() {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles m = ReactorTiles.TEList[i];
			if (m.hasRender() && !m.isPipe()) {
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), ReactorRenderList.instantiateRenderer(m));
			}
		}

		MinecraftForgeClient.registerItemRenderer(ReactorItems.PLACER.getItemInstance(), reactor);
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public void loadDonatorRender() {
		Collection<Donator> donators = new ArrayList();
		donators.addAll(DonatorController.instance.getReikasDonators());
		donators.addAll(PatreonController.instance.getModPatrons("Reika"));
		for (Donator s : donators) {
			if (s.ingameName != null)
				PlayerSpecificRenderer.instance.registerRenderer(s.ingameName, DonatorToroidRender.instance);
			else
				ReactorCraft.logger.logError("Donator "+s.displayName+" UUID could not be found! Cannot give special render!");
		}
	}

}
