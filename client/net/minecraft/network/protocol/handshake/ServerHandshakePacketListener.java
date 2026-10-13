package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ServerboundPacketListener;

public interface ServerHandshakePacketListener extends ServerboundPacketListener {
   default ConnectionProtocol protocol() {
      return ConnectionProtocol.HANDSHAKING;
   }

   void handleIntention(ClientIntentionPacket packet);
}
