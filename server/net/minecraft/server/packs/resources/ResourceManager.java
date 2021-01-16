package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public interface ResourceManager {
   Resource getResource(ResourceLocation var1) throws IOException;

   List<Resource> getResources(ResourceLocation var1) throws IOException;

   Collection<ResourceLocation> listResources(String var1, Predicate<String> var2);

   public static enum Empty implements ResourceManager {
      INSTANCE;

      private Empty() {
      }

      public Resource getResource(ResourceLocation var1) throws IOException {
         throw new FileNotFoundException(var1.toString());
      }

      public List<Resource> getResources(ResourceLocation var1) {
         return ImmutableList.of();
      }

      public Collection<ResourceLocation> listResources(String var1, Predicate<String> var2) {
         return ImmutableSet.of();
      }
   }
}
