/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
// Date: 21/09/2013 4:32:40 PM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package Reika.ReactorCraft.Models;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Instantiable.Rendering.LODModelPart;
import Reika.RotaryCraft.Base.RotaryModelBase;

public class ModelProcessor extends RotaryModelBase
{
	//fields
	LODModelPart Shape2a;
	LODModelPart Shape2b;
	LODModelPart Shape3;
	LODModelPart Shape3a;
	LODModelPart Shape3b;
	LODModelPart Shape3c;
	LODModelPart Shape2;
	LODModelPart Shape2c;
	LODModelPart Shape2d;
	LODModelPart Shape1;
	LODModelPart Shape1a;
	LODModelPart Shape1b;
	LODModelPart Shape1c;

	public ModelProcessor()
	{
		textureWidth = 128;
		textureHeight = 128;

		Shape2a = new LODModelPart(this, 36, 47);
		Shape2a.addBox(0F, 0F, 0F, 1, 14, 7);
		Shape2a.setRotationPoint(-0.5F, 9F, -7F);
		Shape2a.setTextureSize(128, 128);
		Shape2a.mirror = true;
		this.setRotation(Shape2a, 0F, 0F, 0F);
		Shape2b = new LODModelPart(this, 31, 18);
		Shape2b.addBox(0F, 0F, 0F, 1, 14, 7);
		Shape2b.setRotationPoint(-8F, 9F, -7F);
		Shape2b.setTextureSize(128, 128);
		Shape2b.mirror = true;
		this.setRotation(Shape2b, 0F, 0F, 0F);
		Shape3 = new LODModelPart(this, 0, 47);
		Shape3.addBox(0F, 0F, 0F, 16, 14, 1);
		Shape3.setRotationPoint(-8F, 9F, -8F);
		Shape3.setTextureSize(128, 128);
		Shape3.mirror = true;
		this.setRotation(Shape3, 0F, 0F, 0F);
		Shape3a = new LODModelPart(this, 0, 95);
		Shape3a.addBox(0F, 0F, 0F, 14, 11, 1);
		Shape3a.setRotationPoint(-7F, 12F, 1F);
		Shape3a.setTextureSize(128, 128);
		Shape3a.mirror = true;
		this.setRotation(Shape3a, 0F, 0F, 0F);
		Shape3b = new LODModelPart(this, 0, 63);
		Shape3b.addBox(0F, 0F, 0F, 14, 11, 1);
		Shape3b.setRotationPoint(-7F, 12F, 7F);
		Shape3b.setTextureSize(128, 128);
		Shape3b.mirror = true;
		this.setRotation(Shape3b, 0F, 0F, 0F);
		Shape3c = new LODModelPart(this, 0, 79);
		Shape3c.addBox(0F, 0F, 0F, 16, 14, 1);
		Shape3c.setRotationPoint(-8F, 9F, 0F);
		Shape3c.setTextureSize(128, 128);
		Shape3c.mirror = true;
		this.setRotation(Shape3c, 0F, 0F, 0F);
		Shape2 = new LODModelPart(this, 50, 19);
		Shape2.addBox(0F, 0F, 0F, 1, 11, 5);
		Shape2.setRotationPoint(-7F, 12F, 2F);
		Shape2.setTextureSize(128, 128);
		Shape2.mirror = true;
		this.setRotation(Shape2, 0F, 0F, 0F);
		Shape2c = new LODModelPart(this, 0, 18);
		Shape2c.addBox(0F, 0F, 0F, 1, 14, 7);
		Shape2c.setRotationPoint(7F, 9F, -7F);
		Shape2c.setTextureSize(128, 128);
		Shape2c.mirror = true;
		this.setRotation(Shape2c, 0F, 0F, 0F);
		Shape2d = new LODModelPart(this, 18, 19);
		Shape2d.addBox(0F, 0F, 0F, 1, 11, 5);
		Shape2d.setRotationPoint(6F, 12F, 2F);
		Shape2d.setTextureSize(128, 128);
		Shape2d.mirror = true;
		this.setRotation(Shape2d, 0F, 0F, 0F);
		Shape1 = new LODModelPart(this, 54, 10);
		Shape1.addBox(0F, 0F, 0F, 14, 1, 7);
		Shape1.setRotationPoint(-7F, 11F, 1F);
		Shape1.setTextureSize(128, 128);
		Shape1.mirror = true;
		this.setRotation(Shape1, 0F, 0F, 0F);
		Shape1a = new LODModelPart(this, 0, 109);
		Shape1a.addBox(0F, 0F, 0F, 16, 1, 9);
		Shape1a.setRotationPoint(-8F, 8F, -8F);
		Shape1a.setTextureSize(128, 128);
		Shape1a.mirror = true;
		this.setRotation(Shape1a, 0F, 0F, 0F);
		Shape1b = new LODModelPart(this, 0, 0);
		Shape1b.addBox(0F, 0F, 0F, 16, 1, 9);
		Shape1b.setRotationPoint(-8F, 23F, -8F);
		Shape1b.setTextureSize(128, 128);
		Shape1b.mirror = true;
		this.setRotation(Shape1b, 0F, 0F, 0F);
		Shape1c = new LODModelPart(this, 54, 0);
		Shape1c.addBox(0F, 0F, 0F, 14, 1, 7);
		Shape1c.setRotationPoint(-7F, 23F, 1F);
		Shape1c.setTextureSize(128, 128);
		Shape1c.mirror = true;
		this.setRotation(Shape1c, 0F, 0F, 0F);
	}

	@Override
	public void renderAll(TileEntity te, ArrayList li, float phi, float theta)
	{
		Shape2a.render(te, f5);
		Shape2b.render(te, f5);
		Shape3.render(te, f5);
		Shape3a.render(te, f5);
		Shape3b.render(te, f5);
		Shape3c.render(te, f5);
		Shape2.render(te, f5);
		Shape2c.render(te, f5);
		Shape2d.render(te, f5);
		Shape1.render(te, f5);
		Shape1a.render(te, f5);
		Shape1b.render(te, f5);
		Shape1c.render(te, f5);
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5);
	}

}
