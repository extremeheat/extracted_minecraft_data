package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;

public interface ReloadInstance {
   CompletableFuture<?> done();

   float getActualProgress();

   default boolean isDone() {
      return this.done().isDone();
   }

   default void checkExceptions() {
      CompletableFuture var1 = this.done();
      if (var1.isCompletedExceptionally()) {
         var1.join();
      }

   }
}
