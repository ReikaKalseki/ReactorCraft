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
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Registry.ReactorPackets;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;

public class GuiCPU extends ReactorGuiBase {

	private TileEntityCPU tile;
	private ReactorControlLayout layout;

	public static final int BUTTON_SIZE = 8;
	public static final int BUTTON_SPACE = 10;

	public GuiCPU(EntityPlayer player, TileEntityCPU cpu) {
		super(new CoreContainer(player, cpu), player, cpu);
		ySize = 182;
		xSize = 176;
		tile = cpu;
		layout = tile.getLayout();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new GuiButton(0, j+8, -1+k+19, 72, 20, "Retract All"));
		buttonList.add(new GuiButton(1, j+96, -1+k+19, 72, 20, "Insert All"));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		ArrayList<TileEntityControlRod> li = layout.getAllRods();
		for (int i = 0; i < li.size(); i++)
			ReikaPacketHelper.sendUpdatePacket(ReactorCraft.packetChannel, ReactorPackets.CPU.getMinValue()+1+button.id, li.get(i));
		this.updateScreen();
	}

	@Override
	public String getGuiTexture() {
		return "control";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int r = BUTTON_SIZE;
		int s = BUTTON_SPACE;
		int ox = xSize/2-s/2;
		int oy = ySize/2-s/2+18;
		for (int a = layout.getMinX(); a <= layout.getMaxX(); a++) {
			for (int b = layout.getMinZ(); b <= layout.getMaxZ(); b++) {
				if (a != 0 || b != 0) {
					Color c = layout.getDisplayColorAtRelativePosition(a, b);
					int x = ox+a*s;
					int y = oy+b*s;
					this.drawRect(x, y, x+r, y+r, c.getRGB());
				}
			}
		}
	}

	@Override
	public void mouseClicked(int x, int y, int id) {
		super.mouseClicked(x, y, id);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int s = BUTTON_SPACE;
		int ox = j+xSize/2-s/2;
		int oy = k+ySize/2-s/2+18;
		int dx = x-ox;
		int dy = y-oy;
		int a = MathHelper.floor_double(dx/(double)s);
		int b = MathHelper.floor_double(dy/(double)s);
		TileEntityControlRod rod = layout.getControlRodAtRelativePosition(a, b);
		//ReikaJavaLibrary.pConsole(a+", "+b+": "+rod);
		if (rod != null) {
			ReikaPacketHelper.sendUpdatePacket(ReactorCraft.packetChannel, ReactorPackets.CPU.getMinValue(), rod);
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5F, 0.9F);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

	}

}
