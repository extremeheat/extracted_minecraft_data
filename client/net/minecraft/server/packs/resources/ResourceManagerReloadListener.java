package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   default CompletableFuture<Void> reload(PreparableReloadListener.SharedState var1, Executor var2, PreparableReloadListener.PreparationBarrier var3, Executor var4) {
      ResourceManager var5 = var1.resourceManager();
      return var3.wait(Unit.INSTANCE).thenRunAsync(() -> {
         ProfilerFiller var2 = Profiler.get();
         var2.push("listener");
         this.onResourceManagerReload(var5);
         var2.pop();
      }, var4);
   }

   void onResourceManagerReload(ResourceManager var1);
}
