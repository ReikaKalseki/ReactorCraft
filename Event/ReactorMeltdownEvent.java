package Reika.ReactorCraft.Event;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class ReactorMeltdownEvent extends Event {

	public final World world;
	public final int centerX;
	public final int centerY;
	public final int centerZ;

	public ReactorMeltdownEvent(World world, int x, int y, int z) {
		this.world = world;
		centerX = x;
		centerY = y;
		centerZ = z;
	}

}
