package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatAckPacket(int a) implements Packet<ServerGamePacketListener> {
   private final int offset;

   public ServerboundChatAckPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ServerboundChatAckPacket(int var1) {
      super();
      this.offset = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.offset);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatAck(this);
   }
}
