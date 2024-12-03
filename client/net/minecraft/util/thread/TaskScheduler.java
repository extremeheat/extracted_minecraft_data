package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface TaskScheduler<R extends Runnable> extends AutoCloseable {
   String name();

   void schedule(R var1);

   default void close() {
   }

   R wrapRunnable(Runnable var1);

   default <Source> CompletableFuture<Source> scheduleWithResult(Consumer<CompletableFuture<Source>> var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.schedule(this.wrapRunnable(() -> var1.accept(var2)));
      return var2;
   }

   static TaskScheduler<Runnable> wrapExecutor(final String var0, final Executor var1) {
      return new TaskScheduler<Runnable>() {
         public String name() {
            return var0;
         }

         public void schedule(Runnable var1x) {
            var1.execute(var1x);
         }

         public Runnable wrapRunnable(Runnable var1x) {
            return var1x;
         }

         public String toString() {
            return var0;
         }
      };
   }
}
