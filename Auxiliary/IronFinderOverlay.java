/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Auxiliary;


public class IronFinderOverlay {

	public static final IronFinderOverlay instance = new IronFinderOverlay();

	private IronFinderOverlay() {

	}
	/*
	@ForgeSubscribe
	public void eventHandler(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer ep = mc.thePlayer;
		World world = mc.theWorld;
		RenderItem ri = new RenderItem();

		if (ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().itemID == ReactorItems.IRONFINDER.getShiftedItemID()) {
			int x = MathHelper.floor_double(ep.posX);
			int y = MathHelper.floor_double(ep.posY)+1;
			int z = MathHelper.floor_double(ep.posZ);
			Vec3 vec = ep.getLookVec();

			int r = 6;
			BlockArray iron = ItemIronFinder.getIronOreNearby(world, x, y, z, r);

			Icon ico = Block.oreIron.getIcon(0, 0);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			Tessellator v5 = Tessellator.instance;

			int bx = event.resolution.getScaledWidth()/2;
			int by = event.resolution.getScaledHeight()/2;
			int bz = 0;

			ReikaTextureHelper.bindTerrainTexture();

			for (int i = 0; i < iron.getSize(); i++) {
				int[] xyz = iron.getNthBlock(i);
				int dx = xyz[0]-x;
				int dy = xyz[1]-y;
				int dz = xyz[2]-z;

				GL11.glTranslated(dx, dy, dz);
				ReikaGuiAPI.instance.drawItemStack(ri, mc.fontRenderer, new ItemStack(Block.oreIron), bx-8, by-8);
				GL11.glTranslated(-dx, -dy, -dz);

			}

			ReikaTextureHelper.bindHUDTexture();
		}
	}*/
}
