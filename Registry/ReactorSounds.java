/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import java.net.URL;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.RotaryCraft.Registry.ConfigRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public enum ReactorSounds implements SoundEnum {

	TURBINE("#turbine-vol"),
	FUSION("fusion"),
	CONTROL("control"),
	GENERATOR_RF("#gen_rf"),
	GENERATOR_EU("#gen_eu"),
	GENERATOR_ELC("#gen_elc"),
	SCRAM("scram");

	public static final ReactorSounds[] soundList = values();

	public static final String PREFIX = "Reika/ReactorCraft/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ReactorCraft.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";
	private static final String MUSIC_FOLDER = "music/";
	private static final String MUSIC_PREFIX = "music.";

	private final String path;
	private final String name;

	private boolean isVolumed = false;

	private ReactorSounds(String n) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		name = n;
		path = PREFIX+SOUND_FOLDER+name+SOUND_EXT;
	}

	public float getSoundVolume() {
		float vol = ConfigRegistry.MACHINEVOLUME.getFloat(); //config float
		if (vol < 0)
			vol = 0;
		if (vol > 1)
			vol = 1F;
		return vol;
	}

	@Override
	public float getModulatedVolume() {
		if (!isVolumed)
			return 1F;
		else
			return this.getSoundVolume();
	}

	public void playSound(Entity e) {
		this.playSound(e, 1, 1);
	}

	public void playSound(Entity e, float vol, float pitch) {
		this.playSound(e.worldObj, e.posX, e.posY, e.posZ, vol, pitch);
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
			return;
		ReikaSoundHelper.playSound(this, ReactorCraft.packetChannel, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch);
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch, boolean attenuate) {
		if (world.isRemote)
			return;
		ReikaSoundHelper.playSound(this, ReactorCraft.packetChannel, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, attenuate);
	}

	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	public void playSoundAtBlock(TileEntity te) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public void playSoundAtBlock(TileEntity te, float v, float p) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord, v, p);
	}

	public void playSoundAtBlock(WorldLocation loc) {
		this.playSoundAtBlock(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public void playSoundNoAttenuation(World world, double x, double y, double z, float vol, float pitch, int broadcast) {
		if (world.isRemote)
			return;
		//ReikaSoundHelper.playSound(this, ReactorCraft.packetChannel, te.worldObj, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, false);
		ReikaPacketHelper.sendSoundPacket(ReactorCraft.packetChannel, this, world, x, y, z, vol, pitch, false, broadcast);
	}

	public String getName() {
		return this.name();
	}

	public String getPath() {
		return path;
	}

	public URL getURL() {
		return ReactorCraft.class.getResource(SOUND_DIR+name+SOUND_EXT);
	}

	public static ReactorSounds getSoundByName(String name) {
		for (int i = 0; i < soundList.length; i++) {
			if (soundList[i].name().equals(name))
				return soundList[i];
		}
		ReactorCraft.logger.logError("\""+name+"\" does not correspond to a registered sound!");
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.MASTER;
	}

	@Override
	public boolean canOverlap() {
		return this == FUSION;
	}

	@Override
	public boolean attenuate() {
		return true;
	}

	@Override
	public boolean preload() {
		return false;
	}
}
