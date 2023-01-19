package net.minecraft.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.storage.WorldData;

public record WorldStem(CloseableResourceManager a, ReloadableServerResources b, RegistryAccess.Frozen c, WorldData d) implements AutoCloseable {
   private final CloseableResourceManager resourceManager;
   private final ReloadableServerResources dataPackResources;
   private final RegistryAccess.Frozen registryAccess;
   private final WorldData worldData;

   public WorldStem(CloseableResourceManager var1, ReloadableServerResources var2, RegistryAccess.Frozen var3, WorldData var4) {
      super();
      this.resourceManager = var1;
      this.dataPackResources = var2;
      this.registryAccess = var3;
      this.worldData = var4;
   }

   public static CompletableFuture<WorldStem> load(WorldLoader.InitConfig var0, WorldLoader.WorldDataSupplier<WorldData> var1, Executor var2, Executor var3) {
      return WorldLoader.load(var0, var1, WorldStem::new, var2, var3);
   }

   @Override
   public void close() {
      this.resourceManager.close();
   }
}
