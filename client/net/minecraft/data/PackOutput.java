package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PackOutput {
   private final Path outputFolder;

   public PackOutput(Path var1) {
      super();
      this.outputFolder = var1;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public Path getOutputFolder(PackOutput.Target var1) {
      return this.getOutputFolder().resolve(var1.directory);
   }

   public PackOutput.PathProvider createPathProvider(PackOutput.Target var1, String var2) {
      return new PackOutput.PathProvider(this, var1, var2);
   }

   public PackOutput.PathProvider createRegistryElementsPathProvider(ResourceKey<? extends Registry<?>> var1) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.elementsDirPath(var1));
   }

   public PackOutput.PathProvider createRegistryTagsPathProvider(ResourceKey<? extends Registry<?>> var1) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.tagsDirPath(var1));
   }

   public static class PathProvider {
      private final Path root;
      private final String kind;

      PathProvider(PackOutput var1, PackOutput.Target var2, String var3) {
         super();
         this.root = var1.getOutputFolder(var2);
         this.kind = var3;
      }

      public Path file(ResourceLocation var1, String var2) {
         return this.root.resolve(var1.getNamespace()).resolve(this.kind).resolve(var1.getPath() + "." + var2);
      }

      public Path json(ResourceLocation var1) {
         return this.root.resolve(var1.getNamespace()).resolve(this.kind).resolve(var1.getPath() + ".json");
      }
   }

   public static enum Target {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      final String directory;

      private Target(final String nullxx) {
         this.directory = nullxx;
      }
   }
}