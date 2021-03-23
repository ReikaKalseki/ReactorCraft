/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Interfaces.PacketHandler;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.ReactorCraft.Auxiliary.RadiationEffects;
import Reika.ReactorCraft.Registry.ReactorPackets;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityCPU;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityControlRod;


public class ReactorPacketCore implements PacketHandler {

	protected ReactorPackets pack;

	public void handleData(PacketObj packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		double dx = 0;
		double dy = 0;
		double dz = 0;
		boolean readinglong = false;
		String stringdata = null;
		UUID id = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			PacketTypes packetType = packet.getType();
			switch(packetType) {
				case FULLSOUND:
					break;
				case SOUND:
					return;
				case STRING:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = ReactorPackets.getEnum(control);
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
				case POS:
					control = inputStream.readInt();
					pack = ReactorPackets.getEnum(control);
					dx = inputStream.readDouble();
					dy = inputStream.readDouble();
					dz = inputStream.readDouble();
					len = pack.getNumberDataInts();
					if (len > 0) {
						data = new int[len];
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
					}
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
					String name = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, inputStream);
					return;
				case TANK:
					String tank = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					int level = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
					return;
				case RAW:
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
				case PREFIXED:
					control = inputStream.readInt();
					pack = ReactorPackets.getEnum(control);
					len = inputStream.readInt();
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					break;
				case NBT:
					break;
				case STRINGINT:
				case STRINGINTLOC:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = ReactorPackets.getEnum(control);
					data = new int[pack.getNumberDataInts()];
					for (int i = 0; i < data.length; i++)
						data[i] = inputStream.readInt();
					break;
				case UUID:
					control = inputStream.readInt();
					pack = ReactorPackets.getEnum(control);
					long l1 = inputStream.readLong(); //most
					long l2 = inputStream.readLong(); //least
					id = new UUID(l1, l2);
					break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		try {
			switch (pack) {
				case CPUTOGGLE:
					((TileEntityControlRod)te).toggle(true, true);
					break;
				case CPURAISE:
					((TileEntityCPU)te).raiseAllRods();
					break;
				case CPULOWER:
					((TileEntityCPU)te).lowerAllRods();
					break;
				case ORERADIATION: {
					RadiationEffects.instance.doOreIrradiation(world, x, y, z, ep);
					break;
				}
			}
		}
		catch (Exception e) {
			ReactorCraft.logger.logError("Machine/item was deleted before its packet could be received!");
			ReikaChatHelper.writeString("Machine/item was deleted before its packet could be received!");
			e.printStackTrace();
		}
	}
}
