package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.PackResources;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager extends ResourceManager, AutoCloseable {
   default CompletableFuture<Unit> reload(Executor var1, Executor var2, List<PackResources> var3, CompletableFuture<Unit> var4) {
      return this.createReload(var1, var2, var4, var3).done();
   }

   ReloadInstance createReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<PackResources> var4);

   void registerReloadListener(PreparableReloadListener var1);

   void close();
}
