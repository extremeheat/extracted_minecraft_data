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

   public Path getOutputFolder(Target var1) {
      return this.getOutputFolder().resolve(var1.directory);
   }

   public PathProvider createPathProvider(Target var1, String var2) {
      return new PathProvider(this, var1, var2);
   }

   public PathProvider createRegistryElementsPathProvider(ResourceKey<? extends Registry<?>> var1) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.elementsDirPath(var1));
   }

   public PathProvider createRegistryTagsPathProvider(ResourceKey<? extends Registry<?>> var1) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.tagsDirPath(var1));
   }

   public static enum Target {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      final String directory;

      private Target(final String var3) {
         this.directory = var3;
      }

      // $FF: synthetic method
      private static Target[] $values() {
         return new Target[]{DATA_PACK, RESOURCE_PACK, REPORTS};
      }
   }

   public static class PathProvider {
      private final Path root;
      private final String kind;

      PathProvider(PackOutput var1, Target var2, String var3) {
         super();
         this.root = var1.getOutputFolder(var2);
         this.kind = var3;
      }

      public Path file(ResourceLocation var1, String var2) {
         Path var10000 = this.root.resolve(var1.getNamespace()).resolve(this.kind);
         String var10001 = var1.getPath();
         return var10000.resolve(var10001 + "." + var2);
      }

      public Path json(ResourceLocation var1) {
         return this.root.resolve(var1.getNamespace()).resolve(this.kind).resolve(var1.getPath() + ".json");
      }

      public Path json(ResourceKey<?> var1) {
         return this.root.resolve(var1.location().getNamespace()).resolve(this.kind).resolve(var1.location().getPath() + ".json");
      }
   }
}
