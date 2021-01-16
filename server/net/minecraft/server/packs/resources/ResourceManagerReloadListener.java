package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      return var1.wait(Unit.INSTANCE).thenRunAsync(() -> {
         var4.startTick();
         var4.push("listener");
         this.onResourceManagerReload(var2);
         var4.pop();
         var4.endTick();
      }, var6);
   }

   void onResourceManagerReload(ResourceManager var1);
}
