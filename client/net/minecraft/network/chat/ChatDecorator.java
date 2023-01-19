package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (var0, var1) -> CompletableFuture.completedFuture(var1);

   CompletableFuture<Component> decorate(@Nullable ServerPlayer var1, Component var2);
}
