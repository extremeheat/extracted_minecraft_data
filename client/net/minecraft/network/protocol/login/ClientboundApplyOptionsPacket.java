package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundApplyOptionsPacket(@ConnectionOptionsPacket.Flags byte options) implements Packet<ClientLoginPacketListener>, ConnectionOptionsPacket {
   public static final StreamCodec<ByteBuf, ClientboundApplyOptionsPacket> STREAM_CODEC;

   public ClientboundApplyOptionsPacket {
      super();
   }

   public PacketType<ClientboundApplyOptionsPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_APPLY_OPTIONS;
   }

   public void handle(final ClientLoginPacketListener listener) {
      listener.handleEnableOptions(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ConnectionOptionsPacket.FLAG_FIELD_STREAM_CODEC, ClientboundApplyOptionsPacket::options, ClientboundApplyOptionsPacket::new);
   }
}
