package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (var0, var1) -> CompletableFuture.completedFuture(var1);

   CompletableFuture<Component> decorate(@Nullable ServerPlayer var1, Component var2);

   default CompletableFuture<FilteredText<Component>> decorateFiltered(@Nullable ServerPlayer var1, FilteredText<Component> var2) {
      CompletableFuture var3 = this.decorate(var1, (Component)var2.raw());
      if (!var2.isFiltered()) {
         return var3.thenApply(FilteredText::passThrough);
      } else if (var2.filtered() == null) {
         return var3.thenApply(FilteredText::fullyFiltered);
      } else {
         CompletableFuture var4 = this.decorate(var1, (Component)var2.filtered());
         return CompletableFuture.allOf(var3, var4).thenApply(var2x -> new FilteredText<>((Component)var3.join(), (Component)var4.join()));
      }
   }

   default CompletableFuture<FilteredText<PlayerChatMessage>> decorateChat(
      @Nullable ServerPlayer var1, FilteredText<Component> var2, MessageSignature var3, boolean var4
   ) {
      return this.decorateFiltered(var1, var2).thenApply(var3x -> PlayerChatMessage.filteredSigned(var2, var3x, var3, var4));
   }
}
