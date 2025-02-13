package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@FunctionalInterface
public interface PreparableReloadListener {
   CompletableFuture<Void> reload(PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4);

   default String getName() {
      return this.getClass().getSimpleName();
   }

   @FunctionalInterface
   public interface PreparationBarrier {
      <T> CompletableFuture<T> wait(T var1);
   }
}
