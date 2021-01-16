package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public interface PreparableReloadListener {
   CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6);

   default String getName() {
      return this.getClass().getSimpleName();
   }

   public interface PreparationBarrier {
      <T> CompletableFuture<T> wait(T var1);
   }
}
