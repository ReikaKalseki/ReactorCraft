/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Base;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.RotaryCraft.RotaryCraft;

public abstract class ReactorGuiBase extends GuiContainer {

	private TileEntityReactorBase tile;
	private EntityPlayer player;

	public ReactorGuiBase(Container c, EntityPlayer ep, TileEntityReactorBase te) {
		super(c);
		player = ep;
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String file = "/Reika/RotaryCraft/Textures/GUI/buttons.png";
		buttonList.add(new ImagedGuiButton(24000, j-17, k+4, 18, ySize-12, 72, 0, file, "Info", 0xffffff, false, RotaryCraft.class));
		buttonList.add(new ImagedGuiButton(24001, j-17, k+ySize-8, 18, 4, 72, 252, file, "Info", 0xffffff, false, RotaryCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id == 24000 || b.id == 24001) {
			player.closeScreen();
			if (ReikaInventoryHelper.checkForItem(ReactorItems.BOOK.getItemInstance(), player.inventory.mainInventory))
				player.openGui(ReactorCraft.instance, 11, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
			else
				player.openGui(ReactorCraft.instance, 12, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, tile.getName(), xSize/2, 5, 4210752);
		if (tile instanceof IInventory && !(tile instanceof InertIInv && ySize <= 100))
			fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), xSize-58, (ySize - 96) + 3, 4210752);

		fontRendererObj.drawString("?", -10, ySize/2-4, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String i = "/Reika/ReactorCraft/Textures/GUI/"+this.getGuiTexture()+".png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ReactorCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public abstract String getGuiTexture();

}
