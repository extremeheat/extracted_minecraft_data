package net.minecraft.network.chat;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatHeaderPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public interface OutgoingPlayerChatMessage {
   Component serverContent();

   void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3);

   void sendHeadersToRemainingPlayers(PlayerList var1);

   static OutgoingPlayerChatMessage create(PlayerChatMessage var0) {
      return (OutgoingPlayerChatMessage)(var0.signer().isSystem()
         ? new OutgoingPlayerChatMessage.NotTracked(var0)
         : new OutgoingPlayerChatMessage.Tracked(var0));
   }

   public static class NotTracked implements OutgoingPlayerChatMessage {
      private final PlayerChatMessage message;

      public NotTracked(PlayerChatMessage var1) {
         super();
         this.message = var1;
      }

      @Override
      public Component serverContent() {
         return this.message.serverContent();
      }

      @Override
      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         PlayerChatMessage var4 = this.message.filter(var2);
         if (!var4.isFullyFiltered()) {
            RegistryAccess var5 = var1.level.registryAccess();
            ChatType.BoundNetwork var6 = var3.toNetwork(var5);
            var1.connection.send(new ClientboundPlayerChatPacket(var4, var6));
            var1.connection.addPendingMessage(var4);
         }
      }

      @Override
      public void sendHeadersToRemainingPlayers(PlayerList var1) {
      }
   }

   public static class Tracked implements OutgoingPlayerChatMessage {
      private final PlayerChatMessage message;
      private final Set<ServerPlayer> playersWithFullMessage = Sets.newIdentityHashSet();

      public Tracked(PlayerChatMessage var1) {
         super();
         this.message = var1;
      }

      @Override
      public Component serverContent() {
         return this.message.serverContent();
      }

      @Override
      public void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3) {
         PlayerChatMessage var4 = this.message.filter(var2);
         if (!var4.isFullyFiltered()) {
            this.playersWithFullMessage.add(var1);
            RegistryAccess var5 = var1.level.registryAccess();
            ChatType.BoundNetwork var6 = var3.toNetwork(var5);
            var1.connection
               .send(
                  new ClientboundPlayerChatPacket(var4, var6), PacketSendListener.exceptionallySend(() -> new ClientboundPlayerChatHeaderPacket(this.message))
               );
            var1.connection.addPendingMessage(var4);
         }
      }

      @Override
      public void sendHeadersToRemainingPlayers(PlayerList var1) {
         var1.broadcastMessageHeader(this.message, this.playersWithFullMessage);
      }
   }
}
