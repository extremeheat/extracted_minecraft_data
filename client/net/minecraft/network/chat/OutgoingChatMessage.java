package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;

public interface OutgoingChatMessage {
   Component content();

   void sendToPlayer(ServerPlayer player, boolean filtered, ChatType.Bound chatType);

   static OutgoingChatMessage create(final PlayerChatMessage message) {
      return (OutgoingChatMessage)(message.isSystem() ? new Disguised(message.decoratedContent()) : new Player(message));
   }

   public static record Player(PlayerChatMessage message) implements OutgoingChatMessage {
      public Player {
         super();
      }

      public Component content() {
         return this.message.decoratedContent();
      }

      public void sendToPlayer(final ServerPlayer player, final boolean filtered, final ChatType.Bound chatType) {
         PlayerChatMessage filteredMessage = this.message.filter(filtered);
         if (!filteredMessage.isFullyFiltered()) {
            player.connection.sendPlayerChatMessage(filteredMessage, chatType);
         }

      }
   }

   public static record Disguised(Component content) implements OutgoingChatMessage {
      public Disguised {
         super();
      }

      public void sendToPlayer(final ServerPlayer player, final boolean filtered, final ChatType.Bound chatType) {
         player.connection.sendDisguisedChatMessage(this.content, chatType);
      }
   }
}
