package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      return var1.wait(Unit.INSTANCE).thenRunAsync(() -> {
         ProfilerFiller var2x = Profiler.get();
         var2x.push("listener");
         this.onResourceManagerReload(var2);
         var2x.pop();
      }, var4);
   }

   void onResourceManagerReload(ResourceManager var1);
}
