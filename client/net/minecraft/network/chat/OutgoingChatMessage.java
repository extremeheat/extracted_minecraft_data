package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;

public interface OutgoingChatMessage {
   Component content();

   void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3);

   static OutgoingChatMessage create(PlayerChatMessage var0) {
      return (OutgoingChatMessage)(var0.isSystem() ? new OutgoingChatMessage.Disguised(var0.decoratedContent()) : new OutgoingChatMessage.Player(var0));
   }

   public static record Disguised(Component a) implements OutgoingChatMessage {
      private final Component content;

      public Disguised(Component var1) {
         super();
         this.content = var1;
      }

      @Override
      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         var1.connection.sendDisguisedChatMessage(this.content, var3);
      }
   }

   public static record Player(PlayerChatMessage a) implements OutgoingChatMessage {
      private final PlayerChatMessage message;

      public Player(PlayerChatMessage var1) {
         super();
         this.message = var1;
      }

      @Override
      public Component content() {
         return this.message.decoratedContent();
      }

      @Override
      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         PlayerChatMessage var4 = this.message.filter(var2);
         if (!var4.isFullyFiltered()) {
            var1.connection.sendPlayerChatMessage(var4, var3);
         }
      }
   }
}
