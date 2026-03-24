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

   public final CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> this.prepare(manager, Profiler.get()), taskExecutor);
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((preparations) -> this.apply(preparations, manager, Profiler.get()), reloadExecutor);
   }

   protected abstract T prepare(final ResourceManager manager, final ProfilerFiller profiler);

   protected abstract void apply(final T preparations, final ResourceManager manager, final ProfilerFiller profiler);
}
