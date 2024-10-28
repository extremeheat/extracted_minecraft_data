package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;

public class HandshakeProtocols {
   public static final ProtocolInfo<ServerHandshakePacketListener> SERVERBOUND;

   public HandshakeProtocols() {
      super();
   }

   static {
      SERVERBOUND = ProtocolInfoBuilder.serverboundProtocol(ConnectionProtocol.HANDSHAKING, (var0) -> {
         var0.addPacket(HandshakePacketTypes.CLIENT_INTENTION, ClientIntentionPacket.STREAM_CODEC);
      });
   }
}
