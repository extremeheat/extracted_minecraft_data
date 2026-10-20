package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundOptionsAppliedPacket implements Packet<ServerLoginPacketListener> {
   public static final ServerboundOptionsAppliedPacket INSTANCE = new ServerboundOptionsAppliedPacket();
   public static final StreamCodec<ByteBuf, ServerboundOptionsAppliedPacket> STREAM_CODEC;

   private ServerboundOptionsAppliedPacket() {
      super();
   }

   public PacketType<ServerboundOptionsAppliedPacket> type() {
      return LoginPacketTypes.SERVERBOUND_OPTIONS_APPLIED;
   }

   public void handle(final ServerLoginPacketListener listener) {
      listener.handleOptionsEnabled(this);
   }

   static {
      STREAM_CODEC = StreamCodec.<ByteBuf, ServerboundOptionsAppliedPacket>unit(INSTANCE);
   }
}
