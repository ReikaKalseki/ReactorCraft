package Reika.ReactorCraft.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ReactorCraft.Base.ReactorGuiBase;
import Reika.ReactorCraft.Container.ContainerSynthesizer;
import Reika.ReactorCraft.TileEntities.TileEntitySynthesizer;

public class GuiSynthesizer extends ReactorGuiBase {

	private TileEntitySynthesizer tile;

	public GuiSynthesizer(EntityPlayer ep, TileEntitySynthesizer te) {
		super(new ContainerSynthesizer(ep, te), ep, te);
		tile = te;
		ySize = 175;
		xSize = 176;
	}

	@Override
	public String getGuiTexture() {
		return "synthesizer";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		super.drawGuiContainerForegroundLayer(par1, par2);

		int x = ReikaGuiAPI.instance.getMouseRealX()-j;
		int y = ReikaGuiAPI.instance.getMouseRealY()-k;

		if (ReikaGuiAPI.instance.isMouseInBox(j+16, j+33, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRenderer, "Water", x, y);
		}
		if (ReikaGuiAPI.instance.isMouseInBox(j+133, j+150, k+17, k+78)) {
			ReikaGuiAPI.instance.drawTooltipAt(fontRenderer, "Ammonia", x, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

		int i2 = tile.getWaterScaled(60);
		this.drawTexturedModalRect(j+17, k+78-i2, 208, 80-i2, 16, i2);

		int i4 = tile.getAmmoniaScaled(60);
		this.drawTexturedModalRect(j+134, k+78-i4, 224, 80-i4, 16, i4);

		int i6 = tile.getTimerScaled(24);
		this.drawTexturedModalRect(j+103, k+26, 176, 92, i6, 34);
	}

}
