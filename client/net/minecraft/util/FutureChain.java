package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

public class FutureChain implements TaskChainer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CompletableFuture<?> head = CompletableFuture.completedFuture((Object)null);
   private final Executor executor;

   public FutureChain(Executor var1) {
      super();
      this.executor = var1;
   }

   public void append(TaskChainer.DelayedTask var1) {
      this.head = this.head.thenComposeAsync((var1x) -> {
         return (CompletionStage)var1.get();
      }, this.executor).exceptionally((var0) -> {
         if (var0 instanceof CompletionException var1) {
            var0 = var1.getCause();
         }

         if (var0 instanceof CancellationException var2) {
            throw var2;
         } else {
            LOGGER.error("Chain link failed, continuing to next one", var0);
            return null;
         }
      });
   }
}
