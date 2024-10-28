package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {
   Logger LOGGER = LogUtils.getLogger();

   static TaskChainer immediate(final Executor var0) {
      return new TaskChainer() {
         public <T> void append(CompletableFuture<T> var1, Consumer<T> var2) {
            var1.thenAcceptAsync(var2, var0).exceptionally((var0x) -> {
               LOGGER.error("Task failed", var0x);
               return null;
            });
         }
      };
   }

   default void append(Runnable var1) {
      this.append(CompletableFuture.completedFuture((Object)null), (var1x) -> {
         var1.run();
      });
   }

   <T> void append(CompletableFuture<T> var1, Consumer<T> var2);
}
