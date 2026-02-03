package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   Frozen EMPTY = (new ImmutableRegistryAccess(Map.of())).freeze();

   <E> Optional<Registry<E>> lookup(final ResourceKey<? extends Registry<? extends E>> registryKey);

   default <E> Registry<E> lookupOrThrow(final ResourceKey<? extends Registry<? extends E>> name) {
      return (Registry)this.lookup(name).orElseThrow(() -> new IllegalStateException("Missing registry: " + String.valueOf(name)));
   }

   Stream<RegistryEntry<?>> registries();

   default Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
      return this.registries().map((e) -> e.key);
   }

   static Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> registries) {
      return new Frozen() {
         public <T> Optional<Registry<T>> lookup(final ResourceKey<? extends Registry<? extends T>> registryKey) {
            Registry<Registry<T>> registry = registries;
            return registry.getOptional(registryKey);
         }

         public Stream<RegistryEntry<?>> registries() {
            return registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
         }

         public Frozen freeze() {
            return this;
         }
      };
   }

   default Frozen freeze() {
      class FrozenAccess extends ImmutableRegistryAccess implements Frozen {
         protected FrozenAccess(final Stream<RegistryEntry<?>> entries) {
            Objects.requireNonNull(RegistryAccess.this);
            super(entries);
         }
      }

      return new FrozenAccess(this.registries().map(RegistryEntry::freeze));
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
      public RegistryEntry {
         super();
      }

      private static <T, R extends Registry<? extends T>> RegistryEntry<T> fromMapEntry(final Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> e) {
         return fromUntyped((ResourceKey)e.getKey(), (Registry)e.getValue());
      }

      private static <T> RegistryEntry<T> fromUntyped(final ResourceKey<? extends Registry<?>> key, final Registry<?> value) {
         return new RegistryEntry<T>(key, value);
      }

      private RegistryEntry<T> freeze() {
         return new RegistryEntry<T>(this.key, this.value.freeze());
      }
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(final List<? extends Registry<?>> registries) {
         super();
         this.registries = (Map)registries.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (v) -> v));
      }

      public ImmutableRegistryAccess(final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
         super();
         this.registries = Map.copyOf(registries);
      }

      public ImmutableRegistryAccess(final Stream<RegistryEntry<?>> entries) {
         super();
         this.registries = (Map)entries.collect(ImmutableMap.toImmutableMap(RegistryEntry::key, RegistryEntry::value));
      }

      public <E> Optional<Registry<E>> lookup(final ResourceKey<? extends Registry<? extends E>> registryKey) {
         return Optional.ofNullable((Registry)this.registries.get(registryKey)).map((r) -> r);
      }

      public Stream<RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
      }
   }

   public interface Frozen extends RegistryAccess {
   }
}
