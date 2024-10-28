package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class FileToIdConverter {
   private final String prefix;
   private final String extension;

   public FileToIdConverter(String var1, String var2) {
      super();
      this.prefix = var1;
      this.extension = var2;
   }

   public static FileToIdConverter json(String var0) {
      return new FileToIdConverter(var0, ".json");
   }

   public ResourceLocation idToFile(ResourceLocation var1) {
      String var10001 = this.prefix;
      return var1.withPath(var10001 + "/" + var1.getPath() + this.extension);
   }

   public ResourceLocation fileToId(ResourceLocation var1) {
      String var2 = var1.getPath();
      return var1.withPath(var2.substring(this.prefix.length() + 1, var2.length() - this.extension.length()));
   }

   public Map<ResourceLocation, Resource> listMatchingResources(ResourceManager var1) {
      return var1.listResources(this.prefix, (var1x) -> {
         return var1x.getPath().endsWith(this.extension);
      });
   }

   public Map<ResourceLocation, List<Resource>> listMatchingResourceStacks(ResourceManager var1) {
      return var1.listResourceStacks(this.prefix, (var1x) -> {
         return var1x.getPath().endsWith(this.extension);
      });
   }
}
