package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundDisguisedChatPacket(Component a, ChatType.BoundNetwork b) implements Packet<ClientGamePacketListener> {
   private final Component message;
   private final ChatType.BoundNetwork chatType;

   public ClientboundDisguisedChatPacket(FriendlyByteBuf var1) {
      this(var1.readComponent(), new ChatType.BoundNetwork(var1));
   }

   public ClientboundDisguisedChatPacket(Component var1, ChatType.BoundNetwork var2) {
      super();
      this.message = var1;
      this.chatType = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.message);
      this.chatType.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDisguisedChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
