package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

public interface ResourceManager {
   Set<String> getNamespaces();

   Resource getResource(ResourceLocation var1) throws IOException;

   boolean hasResource(ResourceLocation var1);

   List<Resource> getResources(ResourceLocation var1) throws IOException;

   Collection<ResourceLocation> listResources(String var1, Predicate<String> var2);

   Stream<PackResources> listPacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      private Empty() {
      }

      public Set<String> getNamespaces() {
         return ImmutableSet.of();
      }

      public Resource getResource(ResourceLocation var1) throws IOException {
         throw new FileNotFoundException(var1.toString());
      }

      public boolean hasResource(ResourceLocation var1) {
         return false;
      }

      public List<Resource> getResources(ResourceLocation var1) {
         return ImmutableList.of();
      }

      public Collection<ResourceLocation> listResources(String var1, Predicate<String> var2) {
         return ImmutableSet.of();
      }

      public Stream<PackResources> listPacks() {
         return Stream.of();
      }
   }
}
