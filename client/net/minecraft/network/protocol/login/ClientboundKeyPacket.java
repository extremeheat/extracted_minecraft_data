package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundKeyPacket(String serverId, byte[] publicKey, byte[] challenge, boolean shouldAuthenticate) implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundKeyPacket> STREAM_CODEC;

   public ClientboundKeyPacket {
      super();
   }

   public PacketType<ClientboundKeyPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_KEY;
   }

   public void handle(final ClientLoginPacketListener listener) {
      listener.handleKey(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(20), ClientboundKeyPacket::serverId, ByteBufCodecs.BYTE_ARRAY, ClientboundKeyPacket::publicKey, ByteBufCodecs.BYTE_ARRAY, ClientboundKeyPacket::challenge, ByteBufCodecs.BOOL, ClientboundKeyPacket::shouldAuthenticate, ClientboundKeyPacket::new);
   }
}
