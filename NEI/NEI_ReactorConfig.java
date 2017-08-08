/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.NEI;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEI_ReactorConfig implements IConfigureNEI {

	private static final NH3Handler ammonia = new NH3Handler();
	private static final UProcessorHandler UProcessor = new UProcessorHandler();
	private static final CentrifugeHandler centrifuge = new CentrifugeHandler();
	private static final ElectrolyzerHandler electrolyzer = new ElectrolyzerHandler();

	@Override
	public void loadConfig() {
		ReactorCraft.logger.log("Loading NEI Compatibility!");

		API.registerRecipeHandler(ammonia);
		API.registerUsageHandler(ammonia);

		API.registerRecipeHandler(UProcessor);
		API.registerUsageHandler(UProcessor);

		API.registerRecipeHandler(centrifuge);
		API.registerUsageHandler(centrifuge);

		API.registerRecipeHandler(electrolyzer);
		API.registerUsageHandler(electrolyzer);

		ReactorCraft.logger.log("Hiding technical blocks from NEI!");
		this.hideBlock(ReactorBlocks.REACTOR.getBlockInstance());
		this.hideBlock(ReactorBlocks.MODELREACTOR.getBlockInstance());
		this.hideBlock(ReactorBlocks.MACHINE.getBlockInstance());
		this.hideBlock(ReactorBlocks.MODELMACHINE.getBlockInstance());
		this.hideBlock(ReactorBlocks.DUCT.getBlockInstance());
		this.hideBlock(ReactorBlocks.LINE.getBlockInstance());

		if (ReactorCraft.instance.isLocked()) {
			for (int i = 0; i < ReactorItems.itemList.length; i++) {
				ReactorItems ir = ReactorItems.itemList[i];
				API.hideItem(new ItemStack(ir.getItemInstance()));
			}
			for (int i = 0; i < ReactorBlocks.blockList.length; i++) {
				ReactorBlocks b = ReactorBlocks.blockList[i];
				this.hideBlock(b.getBlockInstance());
			}
		}
	}

	private void hideBlock(Block b) {
		API.hideItem(new ItemStack(b, 1, OreDictionary.WILDCARD_VALUE));
	}

	@Override
	public String getName() {
		return "ReactorCraft NEI Handlers";
	}

	@Override
	public String getVersion() {
		return "Gamma";
	}

}
