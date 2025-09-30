package net.minecraft.server.packs.resources;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
   public SimplePreparableReloadListener() {
      super();
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.SharedState var1, Executor var2, PreparableReloadListener.PreparationBarrier var3, Executor var4) {
      ResourceManager var5 = var1.resourceManager();
      CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> this.prepare(var5, Profiler.get()), var2);
      Objects.requireNonNull(var3);
      return var10000.thenCompose(var3::wait).thenAcceptAsync((var2x) -> this.apply(var2x, var5, Profiler.get()), var4);
   }

   protected abstract T prepare(ResourceManager var1, ProfilerFiller var2);

   protected abstract void apply(T var1, ResourceManager var2, ProfilerFiller var3);
}
