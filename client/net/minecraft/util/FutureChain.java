package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

public class FutureChain implements TaskChainer, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CompletableFuture<?> head = CompletableFuture.completedFuture(null);
   private final Executor checkedExecutor;
   private volatile boolean closed;

   public FutureChain(Executor var1) {
      super();
      this.checkedExecutor = var2 -> {
         if (!this.closed) {
            var1.execute(var2);
         }
      };
   }

   @Override
   public void append(TaskChainer.DelayedTask var1) {
      this.head = this.head.thenComposeAsync(var2 -> var1.submit(this.checkedExecutor), this.checkedExecutor).exceptionally(var0 -> {
         if (var0 instanceof CompletionException var1x) {
            var0 = var1x.getCause();
         }

         if (var0 instanceof CancellationException var2) {
            throw var2;
         } else {
            LOGGER.error("Chain link failed, continuing to next one", var0);
            return null;
         }
      });
   }

   @Override
   public void close() {
      this.closed = true;
   }
}
