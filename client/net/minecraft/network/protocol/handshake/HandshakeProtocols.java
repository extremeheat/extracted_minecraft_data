package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.SimpleUnboundProtocol;

public class HandshakeProtocols {
   public static final SimpleUnboundProtocol<ServerHandshakePacketListener, FriendlyByteBuf> SERVERBOUND_TEMPLATE;
   public static final ProtocolInfo<ServerHandshakePacketListener> SERVERBOUND;

   public HandshakeProtocols() {
      super();
   }

   static {
      SERVERBOUND_TEMPLATE = ProtocolInfoBuilder.<ServerHandshakePacketListener, FriendlyByteBuf>serverboundProtocol(ConnectionProtocol.HANDSHAKING, (var0) -> var0.addPacket(HandshakePacketTypes.CLIENT_INTENTION, ClientIntentionPacket.STREAM_CODEC));
      SERVERBOUND = SERVERBOUND_TEMPLATE.bind(FriendlyByteBuf::new);
   }
}
