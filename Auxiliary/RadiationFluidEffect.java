package Reika.ReactorCraft.Auxiliary;

import net.minecraft.entity.EntityLivingBase;
import Reika.ReactorCraft.Auxiliary.RadiationEffects.RadiationIntensity;
import Reika.RotaryCraft.TileEntities.Storage.TileEntityReservoir.FluidEffect;


public class RadiationFluidEffect implements FluidEffect {

	@Override
	public void applyEffect(EntityLivingBase e) {
		RadiationEffects.instance.applyEffects(e, RadiationIntensity.HIGHLEVEL);
	}

}
