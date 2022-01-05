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

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.IO.SoundLoader;
import Reika.ReactorCraft.Registry.ReactorSounds;

public class CommonProxy {

	public static int lineRender;
	public static int lampRender;
	public static int hazmat;

	protected static final SoundLoader sounds = new SoundLoader(ReactorSounds.class);

	public void registerRenderers()
	{

	}

	public void addArmorRenders() {}

	public World getClientWorld() {
		return null;
	}

	public void registerRenderInformation() {

	}

	public void registerSounds() {

	}

	public void loadDonatorRender() {

	}

}
