package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface TaskScheduler<R extends Runnable> extends AutoCloseable {
   String name();

   void schedule(final R r);

   default void close() {
   }

   R wrapRunnable(final Runnable runnable);

   default <Source> CompletableFuture<Source> scheduleWithResult(final Consumer<CompletableFuture<Source>> futureConsumer) {
      CompletableFuture<Source> future = new CompletableFuture();
      this.schedule(this.wrapRunnable(() -> futureConsumer.accept(future)));
      return future;
   }

   static TaskScheduler<Runnable> wrapExecutor(final String name, final Executor executor) {
      return new TaskScheduler<Runnable>() {
         public String name() {
            return name;
         }

         public void schedule(final Runnable runnable) {
            executor.execute(runnable);
         }

         public Runnable wrapRunnable(final Runnable runnable) {
            return runnable;
         }

         public String toString() {
            return name;
         }
      };
   }
}
