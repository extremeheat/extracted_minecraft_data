package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ReloadableResourceManager extends ResourceManager {
   CompletableFuture reload(Executor var1, Executor var2, List var3, CompletableFuture var4);

   ReloadInstance createFullReload(Executor var1, Executor var2, CompletableFuture var3, List var4);

   void registerReloadListener(PreparableReloadListener var1);
}
