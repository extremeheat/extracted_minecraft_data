package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface ServerStatusPacketListener extends PacketListener {
   void handlePingRequest(ServerboundPingRequestPacket var1);

   void handleStatusRequest(ServerboundStatusRequestPacket var1);
}
