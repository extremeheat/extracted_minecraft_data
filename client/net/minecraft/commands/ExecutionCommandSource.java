package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.execution.TraceCallbacks;

public interface ExecutionCommandSource<T extends ExecutionCommandSource<T>> {
   boolean hasPermission(int var1);

   T withCallback(CommandResultCallback var1);

   CommandResultCallback callback();

   default T clearCallbacks() {
      return this.withCallback(CommandResultCallback.EMPTY);
   }

   CommandDispatcher<T> dispatcher();

   void handleError(CommandExceptionType var1, Message var2, boolean var3, @Nullable TraceCallbacks var4);

   boolean isSilent();

   default void handleError(CommandSyntaxException var1, boolean var2, @Nullable TraceCallbacks var3) {
      this.handleError(var1.getType(), var1.getRawMessage(), var2, var3);
   }

   static <T extends ExecutionCommandSource<T>> ResultConsumer<T> resultConsumer() {
      return (var0, var1, var2) -> {
         ((ExecutionCommandSource)var0.getSource()).callback().onResult(var1, var2);
      };
   }
}
