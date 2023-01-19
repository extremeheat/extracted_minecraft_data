package net.minecraft.server;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.storage.WorldData;

public record WorldStem(CloseableResourceManager a, ReloadableServerResources b, LayeredRegistryAccess<RegistryLayer> c, WorldData d) implements AutoCloseable {
   private final CloseableResourceManager resourceManager;
   private final ReloadableServerResources dataPackResources;
   private final LayeredRegistryAccess<RegistryLayer> registries;
   private final WorldData worldData;

   public WorldStem(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, WorldData var4) {
      super();
      this.resourceManager = var1;
      this.dataPackResources = var2;
      this.registries = var3;
      this.worldData = var4;
   }

   @Override
   public void close() {
      this.resourceManager.close();
   }
}
