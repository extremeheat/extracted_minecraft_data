package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {
   Logger LOGGER = LogUtils.getLogger();

   static TaskChainer immediate(Executor var0) {
      return var1 -> var1.submit(var0).exceptionally(var0xx -> {
            LOGGER.error("Task failed", var0xx);
            return null;
         });
   }

   void append(TaskChainer.DelayedTask var1);

   public interface DelayedTask {
      CompletableFuture<?> submit(Executor var1);
   }
}
