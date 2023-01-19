package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (var0, var1) -> CompletableFuture.completedFuture(var1);

   CompletableFuture<Component> decorate(@Nullable ServerPlayer var1, Component var2);

   default CompletableFuture<PlayerChatMessage> decorate(@Nullable ServerPlayer var1, PlayerChatMessage var2) {
      return var2.signedContent().isDecorated()
         ? CompletableFuture.completedFuture(var2)
         : this.decorate(var1, var2.serverContent()).thenApply(var2::withUnsignedContent);
   }

   static PlayerChatMessage attachIfNotDecorated(PlayerChatMessage var0, Component var1) {
      return !var0.signedContent().isDecorated() ? var0.withUnsignedContent(var1) : var0;
   }
}
