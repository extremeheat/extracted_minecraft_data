package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetupCompressionPacket(int compressionThreshold) implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundSetupCompressionPacket> STREAM_CODEC;

   public ClientboundSetupCompressionPacket {
      super();
   }

   public PacketType<ClientboundSetupCompressionPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_SETUP_COMPRESSION;
   }

   public void handle(final ClientLoginPacketListener listener) {
      listener.handleCompression(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundSetupCompressionPacket::compressionThreshold, ClientboundSetupCompressionPacket::new);
   }
}
