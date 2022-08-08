package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {
   Logger LOGGER = LogUtils.getLogger();
   TaskChainer IMMEDIATE = (var0) -> {
      ((CompletableFuture)var0.get()).exceptionally((var0x) -> {
         LOGGER.error("Task failed", var0x);
         return null;
      });
   };

   void append(DelayedTask var1);

   public interface DelayedTask extends Supplier<CompletableFuture<?>> {
   }
}
