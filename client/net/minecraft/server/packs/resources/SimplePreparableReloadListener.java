package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
   public SimplePreparableReloadListener() {
      super();
   }

   @Override
   public final CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      return CompletableFuture.<T>supplyAsync(() -> this.prepare(var2, var3), var5)
         .thenCompose(var1::wait)
         .thenAcceptAsync(var3x -> this.apply(var3x, var2, var4), var6);
   }

   protected abstract T prepare(ResourceManager var1, ProfilerFiller var2);

   protected abstract void apply(T var1, ResourceManager var2, ProfilerFiller var3);
}
