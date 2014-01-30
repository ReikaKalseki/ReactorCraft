/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Registry.ReactorPackets;
import Reika.ReactorCraft.TileEntities.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.TileEntityControlRod;

public class GuiCPU extends ReactorGuiBase {

	private TileEntityCPU tile;
	private ReactorControlLayout layout;

	public GuiCPU(EntityPlayer player, TileEntityCPU cpu) {
		super(new CoreContainer(player, cpu), player, cpu);
		ySize = 148;
		xSize = 176;
		tile = cpu;
		layout = tile.getLayout();
	}

	@Override
	public String getGuiTexture() {
		return "empty";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int r = 4;
		int s = 6;
		int ox = xSize/2-s/2;
		int oy = ySize/2-s/2;
		for (int a = layout.getMinX(); a <= layout.getMaxX(); a++) {
			for (int b = layout.getMinZ(); b <= layout.getMaxZ(); b++) {
				Color c = layout.getDisplayColorAtRelativePosition(a, b);
				int x = ox+a*s;
				int y = oy-b*s;
				this.drawRect(x, y, x+r, y+r, c.getRGB());
			}
		}
	}

	@Override
	public void mouseClicked(int x, int y, int id) {
		super.mouseClicked(x, y, id);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int r = 4;
		int s = 6;
		int ox = j+xSize/2-s/2;
		int oy = k+ySize/2-s/2;
		int dx = x-ox;
		int dy = y-oy;
		int a = MathHelper.floor_double(dx/(double)s);
		int b = -MathHelper.floor_double(dy/(double)s);
		TileEntityControlRod rod = layout.getControlRodAtRelativePosition(a, b);
		ReikaJavaLibrary.pConsole(a+", "+b+": "+rod);
		if (rod != null) {
			ReikaPacketHelper.sendUpdatePacket(ReactorCraft.packetChannel, ReactorPackets.CPU.getMinValue(), rod);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

	}

}
