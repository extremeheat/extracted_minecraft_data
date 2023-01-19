package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data a) implements Packet<ServerGamePacketListener> {
   private final RemoteChatSession.Data chatSession;

   public ServerboundChatSessionUpdatePacket(FriendlyByteBuf var1) {
      this(RemoteChatSession.Data.read(var1));
   }

   public ServerboundChatSessionUpdatePacket(RemoteChatSession.Data var1) {
      super();
      this.chatSession = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      RemoteChatSession.Data.write(var1, this.chatSession);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatSessionUpdate(this);
   }
}
