package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class PackOutput {
   private final Path outputFolder;

   public PackOutput(final Path outputFolder) {
      super();
      this.outputFolder = outputFolder;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public Path getOutputFolder(final Target target) {
      return this.getOutputFolder().resolve(target.directory);
   }

   public PathProvider createPathProvider(final Target target, final String kind) {
      return new PathProvider(this, target, kind);
   }

   public PathProvider createRegistryElementsPathProvider(final ResourceKey<? extends Registry<?>> registryKey) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.elementsDirPath(registryKey));
   }

   public PathProvider createRegistryTagsPathProvider(final ResourceKey<? extends Registry<?>> registryKey) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.tagsDirPath(registryKey));
   }

   public PathProvider createRegistryComponentPathProvider(final ResourceKey<? extends Registry<?>> registryKey) {
      return this.createPathProvider(PackOutput.Target.REPORTS, Registries.componentsDirPath(registryKey));
   }

   public static enum Target {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      private final String directory;

      private Target(final String directory) {
         this.directory = directory;
      }

      // $FF: synthetic method
      private static Target[] $values() {
         return new Target[]{DATA_PACK, RESOURCE_PACK, REPORTS};
      }
   }

   public static class PathProvider {
      private final Path root;
      private final String kind;

      private PathProvider(final PackOutput output, final Target target, final String kind) {
         super();
         this.root = output.getOutputFolder(target);
         this.kind = kind;
      }

      public Path file(final Identifier element, final String extension) {
         Path var10000 = this.root.resolve(element.getNamespace()).resolve(this.kind);
         String var10001 = element.getPath();
         return var10000.resolve(var10001 + "." + extension);
      }

      public Path json(final Identifier element) {
         return this.root.resolve(element.getNamespace()).resolve(this.kind).resolve(element.getPath() + ".json");
      }

      public Path json(final ResourceKey<?> element) {
         return this.root.resolve(element.identifier().getNamespace()).resolve(this.kind).resolve(element.identifier().getPath() + ".json");
      }
   }
}
