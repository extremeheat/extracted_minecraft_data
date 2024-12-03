package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;

public interface OutgoingChatMessage {
   Component content();

   void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3);

   static OutgoingChatMessage create(PlayerChatMessage var0) {
      return (OutgoingChatMessage)(var0.isSystem() ? new Disguised(var0.decoratedContent()) : new Player(var0));
   }

   public static record Player(PlayerChatMessage message) implements OutgoingChatMessage {
      public Player(PlayerChatMessage var1) {
         super();
         this.message = var1;
      }

      public Component content() {
         return this.message.decoratedContent();
      }

      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         PlayerChatMessage var4 = this.message.filter(var2);
         if (!var4.isFullyFiltered()) {
            var1.connection.sendPlayerChatMessage(var4, var3);
         }

      }
   }

   public static record Disguised(Component content) implements OutgoingChatMessage {
      public Disguised(Component var1) {
         super();
         this.content = var1;
      }

      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         var1.connection.sendDisguisedChatMessage(this.content, var3);
      }
   }
}
