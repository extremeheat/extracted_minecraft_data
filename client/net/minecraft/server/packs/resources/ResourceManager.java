package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;

public interface ResourceManager extends ResourceProvider {
   Set<String> getNamespaces();

   List<Resource> getResourceStack(Identifier var1);

   Map<Identifier, Resource> listResources(String var1, Predicate<Identifier> var2);

   Map<Identifier, List<Resource>> listResourceStacks(String var1, Predicate<Identifier> var2);

   Stream<PackResources> listPacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      private Empty() {
      }

      public Set<String> getNamespaces() {
         return Set.of();
      }

      public Optional<Resource> getResource(Identifier var1) {
         return Optional.empty();
      }

      public List<Resource> getResourceStack(Identifier var1) {
         return List.of();
      }

      public Map<Identifier, Resource> listResources(String var1, Predicate<Identifier> var2) {
         return Map.of();
      }

      public Map<Identifier, List<Resource>> listResourceStacks(String var1, Predicate<Identifier> var2) {
         return Map.of();
      }

      public Stream<PackResources> listPacks() {
         return Stream.of();
      }

      // $FF: synthetic method
      private static Empty[] $values() {
         return new Empty[]{INSTANCE};
      }
   }
}
