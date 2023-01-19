package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatAckPacket(LastSeenMessages.Update a) implements Packet<ServerGamePacketListener> {
   private final LastSeenMessages.Update lastSeenMessages;

   public ServerboundChatAckPacket(FriendlyByteBuf var1) {
      this(new LastSeenMessages.Update(var1));
   }

   public ServerboundChatAckPacket(LastSeenMessages.Update var1) {
      super();
      this.lastSeenMessages = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      this.lastSeenMessages.write(var1);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatAck(this);
   }
}
