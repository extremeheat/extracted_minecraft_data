package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.Logger;

public class FutureChain implements TaskChainer, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CompletableFuture<?> head = CompletableFuture.completedFuture((Object)null);
   private final Executor executor;
   private volatile boolean closed;

   public FutureChain(Executor var1) {
      super();
      this.executor = var1;
   }

   public <T> void append(CompletableFuture<T> var1, Consumer<T> var2) {
      this.head = this.head.thenCombine(var1, (var0, var1x) -> {
         return var1x;
      }).thenAcceptAsync((var2x) -> {
         if (!this.closed) {
            var2.accept(var2x);
         }

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

   public void close() {
      this.closed = true;
   }
}
