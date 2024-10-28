package net.minecraft.server;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.storage.WorldData;

public record WorldStem(CloseableResourceManager resourceManager, ReloadableServerResources dataPackResources, LayeredRegistryAccess<RegistryLayer> registries, WorldData worldData) implements AutoCloseable {
   public WorldStem(CloseableResourceManager resourceManager, ReloadableServerResources dataPackResources, LayeredRegistryAccess<RegistryLayer> registries, WorldData worldData) {
      super();
      this.resourceManager = resourceManager;
      this.dataPackResources = dataPackResources;
      this.registries = registries;
      this.worldData = worldData;
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
