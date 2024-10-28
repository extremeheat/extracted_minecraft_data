package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundKeepAlivePacket implements Packet<ServerCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundKeepAlivePacket> STREAM_CODEC = Packet.codec(ServerboundKeepAlivePacket::write, ServerboundKeepAlivePacket::new);
   private final long id;

   public ServerboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   private ServerboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public PacketType<ServerboundKeepAlivePacket> type() {
      return CommonPacketTypes.SERVERBOUND_KEEP_ALIVE;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
