package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPunchPacket() implements Packet<ServerGamePacketListener> {
   public static final ServerboundPunchPacket INSTANCE = new ServerboundPunchPacket();
   public static final StreamCodec<ByteBuf, ServerboundPunchPacket> STREAM_CODEC;

   public ServerboundPunchPacket() {
      super();
   }

   public PacketType<ServerboundPunchPacket> type() {
      return GamePacketTypes.SERVERBOUND_PUNCH;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePunch(this);
   }

   static {
      STREAM_CODEC = StreamCodec.<ByteBuf, ServerboundPunchPacket>unit(INSTANCE);
   }
}
