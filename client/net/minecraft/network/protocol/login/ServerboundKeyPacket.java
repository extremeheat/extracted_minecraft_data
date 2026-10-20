package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundKeyPacket(byte[] encryptedSecretKey, byte[] encryptedChallenge) implements Packet<ServerLoginPacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundKeyPacket> STREAM_CODEC;

   public ServerboundKeyPacket {
      super();
   }

   public PacketType<ServerboundKeyPacket> type() {
      return LoginPacketTypes.SERVERBOUND_KEY;
   }

   public void handle(final ServerLoginPacketListener listener) {
      listener.handleKey(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BYTE_ARRAY, ServerboundKeyPacket::encryptedSecretKey, ByteBufCodecs.BYTE_ARRAY, ServerboundKeyPacket::encryptedChallenge, ServerboundKeyPacket::new);
   }
}
