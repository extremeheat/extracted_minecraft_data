package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundKeepAlivePacket implements Packet<ServerCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundKeepAlivePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundKeepAlivePacket>codec(ServerboundKeepAlivePacket::write, ServerboundKeepAlivePacket::new);
   private final long id;

   public ServerboundKeepAlivePacket(final long id) {
      super();
      this.id = id;
   }

   private ServerboundKeepAlivePacket(final FriendlyByteBuf input) {
      super();
      this.id = input.readLong();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeLong(this.id);
   }

   public PacketType<ServerboundKeepAlivePacket> type() {
      return CommonPacketTypes.SERVERBOUND_KEEP_ALIVE;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
