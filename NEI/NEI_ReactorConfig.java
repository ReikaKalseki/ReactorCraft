/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.NEI;

import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEI_ReactorConfig implements IConfigureNEI {

	private static final NH3Handler ammonia = new NH3Handler();
	private static final UProcessorHandler UProcessor = new UProcessorHandler();
	private static final CentrifugeHandler centrifuge = new CentrifugeHandler();

	@Override
	public void loadConfig() {
		ReactorCraft.logger.log("Loading NEI Compatibility!");

		API.registerRecipeHandler(ammonia);
		API.registerUsageHandler(ammonia);

		API.registerRecipeHandler(UProcessor);
		API.registerUsageHandler(UProcessor);

		API.registerRecipeHandler(centrifuge);
		API.registerUsageHandler(centrifuge);

		ReactorCraft.logger.log("Hiding technical blocks from NEI!");
		API.hideItem(ReactorBlocks.REACTOR.getBlockID());
		API.hideItem(ReactorBlocks.MODELREACTOR.getBlockID());
		API.hideItem(ReactorBlocks.MACHINE.getBlockID());
		API.hideItem(ReactorBlocks.MODELMACHINE.getBlockID());

		if (ReactorCraft.instance.isLocked()) {
			for (int i = 0; i < ReactorItems.itemList.length; i++) {
				ReactorItems ir = ReactorItems.itemList[i];
				API.hideItem(ir.getShiftedItemID());
			}
			for (int i = 0; i < ReactorBlocks.blockList.length; i++) {
				ReactorBlocks b = ReactorBlocks.blockList[i];
				API.hideItem(b.getBlockID());
			}
		}
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
