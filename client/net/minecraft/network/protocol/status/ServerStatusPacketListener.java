package net.minecraft.network.protocol.status;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.ping.ServerPingPacketListener;

public interface ServerStatusPacketListener extends ServerboundPacketListener, ServerPingPacketListener {
   default ConnectionProtocol protocol() {
      return ConnectionProtocol.STATUS;
   }

   void handleStatusRequest(ServerboundStatusRequestPacket packet);
}
