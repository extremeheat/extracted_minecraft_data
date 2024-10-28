package net.minecraft.server;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.storage.WorldData;

public record WorldStem(CloseableResourceManager resourceManager, ReloadableServerResources dataPackResources, LayeredRegistryAccess<RegistryLayer> registries, WorldData worldData) implements AutoCloseable {
   public WorldStem(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, WorldData var4) {
      super();
      this.resourceManager = var1;
      this.dataPackResources = var2;
      this.registries = var3;
      this.worldData = var4;
   }

   public void close() {
      this.resourceManager.close();
   }

   public CloseableResourceManager resourceManager() {
      return this.resourceManager;
   }

   public ReloadableServerResources dataPackResources() {
      return this.dataPackResources;
   }

   public LayeredRegistryAccess<RegistryLayer> registries() {
      return this.registries;
   }

   public WorldData worldData() {
      return this.worldData;
   }
}
