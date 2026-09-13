package net.minecraft.server.packs;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

public class VanillaPackResources {
   private final FixedPathPackResources fullResources;
   private final List<PackResources> resourceLayers;

   public VanillaPackResources(final FixedPathPackResources fullResources, final List<PackResources> resourceLayers) {
      super();
      this.fullResources = fullResources;
      this.resourceLayers = resourceLayers;
   }

   public void listRawPaths(final PackType type, final Identifier resource, final Consumer<Path> output) {
      this.fullResources.listRawPaths(type, resource, output);
   }

   public PackResources fullResources() {
      return this.fullResources;
   }

   public Pack.ResourcesSupplier asResourcesSupplier() {
      return new Pack.ResourcesSupplier() {
         {
            Objects.requireNonNull(VanillaPackResources.this);
         }

         public PackMetadataResources openMetadata(final PackLocationInfo location) {
            return VanillaPackResources.this.fullResources;
         }

         public Stream<PackResources> openResources(final PackLocationInfo location, final Pack.Metadata metadata) {
            return VanillaPackResources.this.resourceLayers.stream();
         }
      };
   }

   public ResourceManager asResourceManager() {
      FallbackResourceManager vanillaOnlyResourceManager = new FallbackResourceManager(PackType.CLIENT_RESOURCES, "minecraft");
      vanillaOnlyResourceManager.push(this.fullResources);
      return vanillaOnlyResourceManager;
   }
}
