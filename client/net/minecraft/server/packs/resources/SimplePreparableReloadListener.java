package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
   public SimplePreparableReloadListener() {
      super();
   }

   @Override
   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      return CompletableFuture.<T>supplyAsync(() -> this.prepare(var2, Profiler.get()), var3)
         .thenCompose(var1::wait)
         .thenAcceptAsync(var2x -> this.apply((T)var2x, var2, Profiler.get()), var4);
   }

   protected abstract T prepare(ResourceManager var1, ProfilerFiller var2);

   protected abstract void apply(T var1, ResourceManager var2, ProfilerFiller var3);
}
