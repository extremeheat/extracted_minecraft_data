package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class FileToIdConverter {
   private final String prefix;
   private final String extension;

   public FileToIdConverter(final String prefix, final String extension) {
      super();
      this.prefix = prefix;
      this.extension = extension;
   }

   public static FileToIdConverter json(final String prefix) {
      return new FileToIdConverter(prefix, ".json");
   }

   public static FileToIdConverter registry(final ResourceKey<? extends Registry<?>> registry) {
      return json(Registries.elementsDirPath(registry));
   }

   public Identifier idToFile(final Identifier id) {
      String var10001 = this.prefix;
      return id.withPath(var10001 + "/" + id.getPath() + this.extension);
   }

   public Identifier fileToId(final Identifier file) {
      String path = file.getPath();
      return file.withPath(path.substring(this.prefix.length() + 1, path.length() - this.extension.length()));
   }

   public Map<Identifier, Resource> listMatchingResources(final ResourceManager manager) {
      return manager.listResources(this.prefix, (id) -> id.getPath().endsWith(this.extension));
   }

   public Map<Identifier, List<Resource>> listMatchingResourceStacks(final ResourceManager manager) {
      return manager.listResourceStacks(this.prefix, (id) -> id.getPath().endsWith(this.extension));
   }
}
