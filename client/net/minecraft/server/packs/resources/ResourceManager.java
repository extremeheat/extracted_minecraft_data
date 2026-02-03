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

   List<Resource> getResourceStack(Identifier location);

   Map<Identifier, Resource> listResources(String directory, Predicate<Identifier> filter);

   Map<Identifier, List<Resource>> listResourceStacks(String directory, Predicate<Identifier> filter);

   Stream<PackResources> listPacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      private Empty() {
      }

      public Set<String> getNamespaces() {
         return Set.of();
      }

      public Optional<Resource> getResource(final Identifier location) {
         return Optional.empty();
      }

      public List<Resource> getResourceStack(final Identifier location) {
         return List.of();
      }

      public Map<Identifier, Resource> listResources(final String directory, final Predicate<Identifier> filter) {
         return Map.of();
      }

      public Map<Identifier, List<Resource>> listResourceStacks(final String directory, final Predicate<Identifier> filter) {
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
