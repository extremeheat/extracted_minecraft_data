package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatAckPacket(int offset) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatAckPacket> STREAM_CODEC = Packet.codec(ServerboundChatAckPacket::write, ServerboundChatAckPacket::new);

   private ServerboundChatAckPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ServerboundChatAckPacket(int var1) {
      super();
      this.offset = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.offset);
   }

   public PacketType<ServerboundChatAckPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_ACK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatAck(this);
   }

   public int offset() {
      return this.offset;
   }
}
