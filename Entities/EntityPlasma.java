package Reika.ReactorCraft.Entities;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.ReactorCraft.TileEntities.TileEntityMagnet;

public class EntityPlasma extends ParticleEntity {

	private int lastMagnetX;
	private int lastMagnetY;
	private int lastMagnetZ;

	private float lastPhi;

	public EntityPlasma(World par1World) {
		super(par1World);
	}

	@Override
	public double getSpeed() {
		return 0.1;
	}

	@Override
	public boolean onEnterBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity e) {
		e.attackEntityFrom(DamageSource.lava, Integer.MAX_VALUE);
	}

	@Override
	protected void onTick() {
		if (ticksExisted > 200)
			this.setDead();

		//double v = ReikaMathLibrary.py3d(motionX, 0, motionZ);
		//motionX *= 2/v;
		//motionZ *= 2/v;
	}

	@Override
	public double getHitboxSize() {
		return 1;
	}

	public boolean canAffect(TileEntityMagnet te) {
		float phi = te.getAngle();
		if (phi >= 360)
			phi -= 360;
		if (phi < 0)
			phi += 360;
		float target = lastPhi+11.25F;
		if (target >= 360)
			target = 0;
		if (target == phi) {
			lastPhi = phi;
			lastMagnetX = te.xCoord;
			lastMagnetY = te.yCoord;
			lastMagnetZ = te.zCoord;
			return true;
		}
		return false;
	}

}
