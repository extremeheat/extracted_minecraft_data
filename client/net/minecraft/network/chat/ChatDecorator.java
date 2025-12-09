package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (var0, var1) -> var1;

   Component decorate(@Nullable ServerPlayer var1, Component var2);
}
