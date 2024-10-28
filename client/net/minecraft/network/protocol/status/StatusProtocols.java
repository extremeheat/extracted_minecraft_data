package net.minecraft.network.protocol.status;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.PingPacketTypes;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

public class StatusProtocols {
   public static final ProtocolInfo.Unbound<ServerStatusPacketListener, ByteBuf> SERVERBOUND_TEMPLATE;
   public static final ProtocolInfo<ServerStatusPacketListener> SERVERBOUND;
   public static final ProtocolInfo.Unbound<ClientStatusPacketListener, FriendlyByteBuf> CLIENTBOUND_TEMPLATE;
   public static final ProtocolInfo<ClientStatusPacketListener> CLIENTBOUND;

   public StatusProtocols() {
      super();
   }

   static {
      SERVERBOUND_TEMPLATE = ProtocolInfoBuilder.serverboundProtocol(ConnectionProtocol.STATUS, (var0) -> {
         var0.addPacket(StatusPacketTypes.SERVERBOUND_STATUS_REQUEST, ServerboundStatusRequestPacket.STREAM_CODEC).addPacket(PingPacketTypes.SERVERBOUND_PING_REQUEST, ServerboundPingRequestPacket.STREAM_CODEC);
      });
      SERVERBOUND = SERVERBOUND_TEMPLATE.bind((var0) -> {
         return var0;
      });
      CLIENTBOUND_TEMPLATE = ProtocolInfoBuilder.clientboundProtocol(ConnectionProtocol.STATUS, (var0) -> {
         var0.addPacket(StatusPacketTypes.CLIENTBOUND_STATUS_RESPONSE, ClientboundStatusResponsePacket.STREAM_CODEC).addPacket(PingPacketTypes.CLIENTBOUND_PONG_RESPONSE, ClientboundPongResponsePacket.STREAM_CODEC);
      });
      CLIENTBOUND = CLIENTBOUND_TEMPLATE.bind(FriendlyByteBuf::new);
   }
}
