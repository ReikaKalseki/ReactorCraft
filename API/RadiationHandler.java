package Reika.ReactorCraft.API;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class RadiationHandler {

	private static Class radiationHandler;
	private static Object instance;
	private static Method hasSuit;
	private static Method applyPotion;
	private static Method applyToBlock;
	private static Method fillArea;

	private static Class intensityClass;
	private static RadiationLevel[] intensities;

	public static boolean hasHazmatSuit(EntityLivingBase e) {
		try {
			return (boolean)hasSuit.invoke(instance, e);
		}
		catch (Exception e1) {
			ReikaJavaLibrary.pConsole("Error calling ReC radiation handler!");
			e1.printStackTrace();
			return false;
		}
	}

	public static boolean applyPotionEffectToEntity(EntityLivingBase e, RadiationLevel ri) {
		try {
			return (boolean)applyPotion.invoke(e, ri);
		}
		catch (Exception e1) {
			ReikaJavaLibrary.pConsole("Error calling ReC radiation handler!");
			e1.printStackTrace();
			return false;
		}
	}

	public static void irradiateBlock(World world, int x, int y, int z, RadiationLevel ri) {
		try {
			applyToBlock.invoke(world, x, y, z, ri);
		}
		catch (Exception e1) {
			ReikaJavaLibrary.pConsole("Error calling ReC radiation handler!");
			e1.printStackTrace();
		}
	}

	public static void irradiateArea(World world, int x, int y, int z, int range, float density, double force, boolean lineOfSight, RadiationLevel ri) {
		try {
			fillArea.invoke(world, x, y, z, range, density, force, lineOfSight, ri);
		}
		catch (Exception e1) {
			ReikaJavaLibrary.pConsole("Error calling ReC radiation handler!");
			e1.printStackTrace();
		}
	}

	public static RadiationLevel getRadiationIntensity(int idx) {
		return intensities[idx];
	}

	public static RadiationLevel getMaxRadiationIntensity() {
		return intensities[intensities.length-1];
	}

	public static interface RadiationLevel {

		public String name();
		public int ordinal();

		/** Whether this radiation level is even harmful */
		public boolean causesHarm();

		/** Whether an entity is sufficiently armored to be immune to this radiation level */
		public boolean hasSufficientShielding(EntityLivingBase e);

	}

	static {
		try {
			intensityClass = Class.forName("Reika.ReactorCraft.Auxiliary.RadiationEffects$RadiationIntensity");
			intensities = (RadiationLevel[])intensityClass.getEnumConstants();

			radiationHandler = Class.forName("Reika.ReactorCraft.Auxiliary.RadiationEffects");

			Field f = radiationHandler.getDeclaredField("instance");
			f.setAccessible(true);
			instance = f.get(null);

			hasSuit = radiationHandler.getDeclaredMethod("hasHazmatSuit", EntityLivingBase.class);
			hasSuit.setAccessible(true);
			applyPotion = radiationHandler.getDeclaredMethod("applyEffects", EntityLivingBase.class, intensityClass);
			applyPotion.setAccessible(true);
			applyToBlock = radiationHandler.getDeclaredMethod("transformBlock", World.class, int.class, int.class, int.class, intensityClass);
			applyToBlock.setAccessible(true);
			fillArea = radiationHandler.getDeclaredMethod("contaminateArea", World.class, int.class, int.class, int.class, int.class, float.class, double.class, boolean.class, intensityClass);
			fillArea.setAccessible(true);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not read ReC class!");
		}
	}

}
