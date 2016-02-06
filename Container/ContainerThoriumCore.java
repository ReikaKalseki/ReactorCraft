/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.Fission.Thorium.TileEntityThoriumCore;

public class ContainerThoriumCore extends CoreContainer {

	private TileEntityThoriumCore thor;

	public ContainerThoriumCore(EntityPlayer player, TileEntityThoriumCore te) {
		super(player, te);

		thor = te;
	}

	/**
	 * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
	 */
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		ReikaPacketHelper.sendTankSyncPacket(ReactorCraft.packetChannel, thor, "fuelTank");
		ReikaPacketHelper.sendTankSyncPacket(ReactorCraft.packetChannel, thor, "fuelTankOut");
		ReikaPacketHelper.sendTankSyncPacket(ReactorCraft.packetChannel, thor, "wasteTank");
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{

	}
}
