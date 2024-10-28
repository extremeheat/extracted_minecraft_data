package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPongPacket implements Packet<ServerCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPongPacket> STREAM_CODEC = Packet.codec(ServerboundPongPacket::write, ServerboundPongPacket::new);
   private final int id;

   public ServerboundPongPacket(int var1) {
      super();
      this.id = var1;
   }

   private ServerboundPongPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.id);
   }

   public PacketType<ServerboundPongPacket> type() {
      return CommonPacketTypes.SERVERBOUND_PONG;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handlePong(this);
   }

   public int getId() {
      return this.id;
   }
}
