/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Registry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorAchievementPage;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;

public enum ReactorAchievements {

	MINEURANIUM(		0, 0,	ReactorOres.PITCHBLENDE.getProduct(), 							null,			false), //mine block
	PEBBLE(				0, -2,	ReactorItems.PELLET, 											MINEURANIUM,	false), //make pebble fuel
	UF6(				2, 0,	ReactorStacks.uf6can, 											MINEURANIUM,	false), //make
	DEPLETED(			2, -2,	ReactorItems.DEPLETED, 											UF6,			false), //make
	FISSION(			4, 0,	ReactorItems.FUEL, 												UF6,			false), //fission event
	PLUTONIUM(			6, 0,	ReactorItems.PLUTONIUM, 										FISSION,		true), //make
	PUPOISON(			8, 0,	Items.spider_eye, 												PLUTONIUM,		false), //s/e
	HOLDWASTE(			4, 4,	ReactorItems.WASTE,												FISSION,		false), //s/e
	DECAY(				6, 2,	ReactorTiles.STORAGE, 											FISSION,		true), //s/e
	WASTELEAK(			6, 4,	ReactorItems.GOGGLES,											HOLDWASTE,		false), //s/e
	AMMONIA(			6, -2,	ReactorStacks.nh3can,											FISSION,		false), //make NH3 steam
	NH3EXPLODE(			8, -2,	ReactorTiles.STEAMLINE,											AMMONIA,		false), //explosion
	GIGATURBINE(		6, -4,	ReactorTiles.TURBINECORE, 										AMMONIA,		true), //GW on one turbine
	HOTCORE(			4, -4,	Blocks.flowing_lava, 											FISSION,		false), //500C core
	SCRAM(				2, -4,	ReactorTiles.CPU, 												HOTCORE,		false), //s/e
	MELTDOWN(			4, -6,	MatBlocks.SLAG.getStackOf(),									HOTCORE,		false), //s/e
	HEAVYWATER(			-2, 0,	ReactorItems.BUCKET, 											MINEURANIUM,	false), //make
	CANDU(				-2, 2,	ReactorTiles.COOLANT, 											HEAVYWATER,		false), //d20 in cool cell
	PLASMA(				-4, 0,	ReactorTiles.HEATER, 											HEAVYWATER,		true), //make plasma
	ESCAPE(				-4, -2,	Blocks.fire,			 										PLASMA,			false), //!canAffect(e)
	MELTPIPE(			-4, 2,	ReactorTiles.MAGNETPIPE,										PLASMA,			false), //s/e
	FUSION(				-6, 0,	ReactorTiles.MAGNET, 											PLASMA,			true), //fusion event
	FIFTYGW(			-6, 2,	MachineRegistry.DYNAMOMETER.getCraftedProduct(),				FUSION,			true), //per reactor
	PEBBLEFAIL(			0,	-4,	ReactorItems.OLDPELLET,											PEBBLE,			true);
	;

	public static final ReactorAchievements[] list = values();

	public final ReactorAchievements dependency;
	public final int xPosition;
	public final int yPosition;
	public final boolean isSpecial;
	private final ItemStack iconItem;

	private ReactorAchievements(int x, int y, Item icon, ReactorAchievements preReq, boolean special) {
		this(x, y, new ItemStack(icon), preReq, special);
	}

	private ReactorAchievements(int x, int y, Block icon, ReactorAchievements preReq, boolean special) {
		this(x, y, new ItemStack(icon), preReq, special);
	}

	private ReactorAchievements(int x, int y, ReactorItems icon, ReactorAchievements preReq, boolean special) {
		this(x, y, icon.getStackOf(), preReq, special);
	}

	private ReactorAchievements(int x, int y, ReactorTiles icon, ReactorAchievements preReq, boolean special) {
		this(x, y, icon.getCraftedProduct(), preReq, special);
	}

	private ReactorAchievements(int x, int y, ItemStack icon, ReactorAchievements preReq, boolean special) {
		xPosition = x;
		yPosition = y;
		dependency = preReq;
		iconItem = icon;
		isSpecial = special;
	}

	public Achievement get() {
		return ReactorCraft.achievements[this.ordinal()];
	}

	public void triggerAchievement(EntityPlayer ep) {
		if (!ConfigRegistry.ACHIEVEMENTS.getState())
			return;
		if (ep == null) {
			//ReikaChatHelper.write("Player does not exist to receive their achievement \""+this+"\"!");
			//ReikaJavaLibrary.pConsole("Player does not exist to receive their achievement \""+this+"\"!");
			ReactorCraft.logger.debug("Player does not exist to receive their achievement \""+this+"\"!");
		}
		else {
			ep.triggerAchievement(this.get());
		}
	}

	public boolean hasDependency() {
		return dependency != null;
	}

	public static void registerAchievements() {
		//ReikaJavaLibrary.pConsole(Arrays.toString(ReactorCraft.config.achievementIDs));
		for (int i = 0; i < list.length; i++) {
			ReactorAchievements a = list[i];
			String id = a.name();
			Achievement dep = a.hasDependency() ? a.dependency.get() : null;
			Achievement ach = new Achievement(id, a.name().toLowerCase(), a.xPosition, a.yPosition, a.iconItem, dep);
			id = ach.statId;
			//ReikaJavaLibrary.pConsole(a+":"+id+":"+StatList.getOneShotStat(id));
			//if (StatList.getOneShotStat(id) != null)
			//	throw new IDConflictException(ReactorCraft.instance, "The mod's achievement IDs are conflicting with another at ID "+id+" ("+a+" is trying to overwrite "+StatList.getOneShotStat(id).statName+").\nCheck the config file and change them.");
			if (a.isSpecial)
				ach.setSpecial();
			ReactorCraft.achievements[i] = ach;
			ach.registerStat();
			ReactorCraft.logger.log("Registering achievement "+a+" with ID "+id+" and ingame name \""+a+"\" (slot "+i+").");
		}
		AchievementPage.registerAchievementPage(new ReactorAchievementPage("ReactorCraft", ReactorCraft.achievements));
	}

}
