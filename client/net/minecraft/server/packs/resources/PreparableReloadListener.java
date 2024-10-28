package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface PreparableReloadListener {
   CompletableFuture<Void> reload(PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4);

   default String getName() {
      return this.getClass().getSimpleName();
   }

   public interface PreparationBarrier {
      <T> CompletableFuture<T> wait(T var1);
   }
}
