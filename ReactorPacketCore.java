/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.Registry.ReactorPackets;
import Reika.ReactorCraft.Registry.ReactorSounds;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;


public abstract class ReactorPacketCore implements IPacketHandler {

	private TileEntityControlRod rod;

	protected ReactorPackets pack;
	protected PacketTypes packetType;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		this.process(packet, (EntityPlayer)player);
	}

	public abstract void process(Packet250CustomPayload packet, EntityPlayer ep);

	public void handleData(Packet250CustomPayload packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x,y,z;
		boolean readinglong = false;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			packetType = PacketTypes.getPacketType(inputStream.readInt());
			switch(packetType) {
			case SOUND:
				ReactorSounds.playSoundPacket(inputStream);
				return;
			case STRING:
				control = inputStream.readInt();
				pack = ReactorPackets.getEnum(control);
				stringdata = Packet.readString(inputStream, Short.MAX_VALUE);
				break;
			case DATA:
				control = inputStream.readInt();
				pack = ReactorPackets.getEnum(control);
				len = pack.getNumberDataInts();
				data = new int[len];
				readinglong = pack.isLongPacket();
				if (!readinglong) {
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
				}
				else
					longdata = inputStream.readLong();
				break;
			case UPDATE:
				control = inputStream.readInt();
				pack = ReactorPackets.getEnum(control);
				break;
			case FLOAT:
				control = inputStream.readInt();
				pack = ReactorPackets.getEnum(control);
				floatdata = inputStream.readFloat();
				break;
			case SYNC:
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String name = Packet.readString(inputStream, Short.MAX_VALUE);
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
				return;
			case TANK:
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String tank = Packet.readString(inputStream, Short.MAX_VALUE);
				int level = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
				return;
			}
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		try {
			switch (pack) {
			case CPU:
				rod = (TileEntityControlRod)te;
				if (control == ReactorPackets.CPU.getMinValue()) {
					rod.toggle();
				}
				if (control == ReactorPackets.CPU.getMinValue()+1) {
					rod.setActive(false);
				}
				if (control == ReactorPackets.CPU.getMinValue()+2) {
					rod.setActive(true);
				}
				break;
			}
		}
		catch (NullPointerException e) {
			ReikaJavaLibrary.pConsole("Machine/item was deleted before its packet could be received!");
			ReikaChatHelper.writeString("Machine/item was deleted before its packet could be received!");
			e.printStackTrace();
		}
	}
}
