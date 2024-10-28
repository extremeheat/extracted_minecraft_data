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

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> {
         return this.prepare(var2, Profiler.get());
      }, var3);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var2x) -> {
         this.apply(var2x, var2, Profiler.get());
      }, var4);
   }

   protected abstract T prepare(ResourceManager var1, ProfilerFiller var2);

   protected abstract void apply(T var1, ResourceManager var2, ProfilerFiller var3);
}
