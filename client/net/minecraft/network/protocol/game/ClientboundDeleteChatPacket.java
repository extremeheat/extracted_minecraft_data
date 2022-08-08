package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature a) implements Packet<ClientGamePacketListener> {
   private final MessageSignature messageSignature;

   public ClientboundDeleteChatPacket(FriendlyByteBuf var1) {
      this(new MessageSignature(var1));
   }

   public ClientboundDeleteChatPacket(MessageSignature var1) {
      super();
      this.messageSignature = var1;
   }

   public void write(FriendlyByteBuf var1) {
      this.messageSignature.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDeleteChat(this);
   }

   public MessageSignature messageSignature() {
      return this.messageSignature;
   }
}
