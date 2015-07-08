/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.GUIs;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorControlLayout;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Registry.ReactorPackets;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import Reika.RotaryCraft.RotaryCraft;

public class GuiCPU extends ReactorGuiBase {

	private TileEntityCPU tile;
	private ReactorControlLayout layout;

	public static final int BUTTON_SIZE = 3;
	public static final int BUTTON_SPACE = 5;
	private int offsetY = 0;

	public GuiCPU(EntityPlayer player, TileEntityCPU cpu) {
		super(new CoreContainer(player, cpu), player, cpu);
		ySize = 210;
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

		String tex = "/Reika/RotaryCraft/Textures/GUI/buttons.png";
		buttonList.add(new ImagedGuiButton(2, j+7, k+84, 12, 48, 90, 60, tex, RotaryCraft.class));
		buttonList.add(new ImagedGuiButton(3, j+157, k+84, 12, 48, 90, 108, tex, RotaryCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		switch(button.id) {
		case 0:
		case 1:
			ReikaPacketHelper.sendUpdatePacket(ReactorCraft.packetChannel, ReactorPackets.CPU.getMinValue()+1+button.id, tile);
			break;
		case 2:
			if (layout.getMinY() < offsetY)
				offsetY--;
			break;
		case 3:
			if (layout.getMaxY() > offsetY)
				offsetY++;
			break;
		}
		this.updateScreen();
	}

	@Override
	public String getGuiTexture() {
		return "control2";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int r = BUTTON_SIZE;
		int s = BUTTON_SPACE;
		int ox = 1+xSize/2-s/2-1;
		int oy = ySize/2-s/2+5;
		for (int a = layout.getMinX(); a <= layout.getMaxX(); a++) {
			for (int b = layout.getMinZ(); b <= layout.getMaxZ(); b++) {
				if (a != 0 || b != 0) {
					Color c = layout.getDisplayColorAtRelativePosition(tile.worldObj, a, offsetY, b);
					int x = ox+a*s;
					int y = oy+b*s;
					this.drawRect(x, y, x+r, y+r, c.getRGB());
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int id) {
		super.mouseClicked(x, y, id);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int s = BUTTON_SPACE;
		int ox = 1+j+xSize/2-s/2-1;
		int oy = k+ySize/2-s/2+5;
		int dx = x-ox;
		int dy = y-oy;
		int a = MathHelper.floor_double(dx/(double)s);
		int b = MathHelper.floor_double(dy/(double)s);
		TileEntityControlRod rod = layout.getControlRodAtRelativePosition(tile.worldObj, a, offsetY, b);
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
