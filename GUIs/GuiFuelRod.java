package Reika.ReactorCraft.GUIs;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ReactorCraft.Container.ContainerFuelRod;
import Reika.ReactorCraft.TileEntities.TileEntityFuelRod;

public class GuiFuelRod extends GuiContainer {

	public GuiFuelRod(EntityPlayer player, TileEntityFuelRod fuel) {
		super(new ContainerFuelRod(player, fuel));
		ySize = 182;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String i = "/Reika/ReactorCraft/Textures/GUI/"+"fuelrod"+".png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
