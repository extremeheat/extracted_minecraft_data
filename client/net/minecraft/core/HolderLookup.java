package net.minecraft.core;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface HolderLookup<T> {
   Optional<Holder<T>> get(ResourceKey<T> var1);

   Stream<ResourceKey<T>> listElements();

   Optional<? extends HolderSet<T>> get(TagKey<T> var1);

   Stream<TagKey<T>> listTags();

   static <T> HolderLookup<T> forRegistry(Registry<T> var0) {
      return new HolderLookup.RegistryLookup<>(var0);
   }

   public static class RegistryLookup<T> implements HolderLookup<T> {
      protected final Registry<T> registry;

      public RegistryLookup(Registry<T> var1) {
         super();
         this.registry = var1;
      }

      @Override
      public Optional<Holder<T>> get(ResourceKey<T> var1) {
         return this.registry.getHolder(var1);
      }

      @Override
      public Stream<ResourceKey<T>> listElements() {
         return this.registry.entrySet().stream().map(Entry::getKey);
      }

      @Override
      public Optional<? extends HolderSet<T>> get(TagKey<T> var1) {
         return this.registry.getTag(var1);
      }

      @Override
      public Stream<TagKey<T>> listTags() {
         return this.registry.getTagNames();
      }
   }
}
