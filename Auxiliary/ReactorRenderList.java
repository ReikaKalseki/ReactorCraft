/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;

import java.util.HashMap;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Base.RotaryTERenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReactorRenderList {

	private static HashMap<ReactorTiles, RotaryTERenderer> renders = new HashMap<ReactorTiles, RotaryTERenderer>();
	private static HashMap<ReactorTiles, ReactorTiles> overrides = new HashMap<ReactorTiles, ReactorTiles>();

	public static boolean addRender(ReactorTiles m, RotaryTERenderer r) {
		if (!renders.containsValue(r)) {
			renders.put(m, r);
			return true;
		}
		else {
			ReactorTiles parent = ReikaJavaLibrary.getHashMapKeyByValue(renders, r);
			overrides.put(m, parent);
			return false;
		}
	}

	public static RotaryTERenderer getRenderForMachine(ReactorTiles m) {
		if (overrides.containsKey(m))
			return renders.get(overrides.get(m));
		return renders.get(m);
	}

	public static String getRenderTexture(ReactorTiles m, RenderFetcher te) {
		return getRenderForMachine(m).getImageFileName(te);
	}

	public static TileEntitySpecialRenderer instantiateRenderer(ReactorTiles m) {
		try {
			RotaryTERenderer r = (RotaryTERenderer)Class.forName(m.getRenderer()).newInstance();
			if (addRender(m, r))
				return r;
			else
				return renders.get(overrides.get(m));
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call nonexistent render "+m.getRenderer()+"!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call illegal render "+m.getRenderer()+"!");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("No class found for Renderer "+m.getRenderer()+"!");
		}
	}

}
