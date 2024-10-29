package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   Frozen EMPTY = (new ImmutableRegistryAccess(Map.of())).freeze();

   <E> Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> var1);

   default <E> Registry<E> lookupOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return (Registry)this.lookup(var1).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + String.valueOf(var1));
      });
   }

   Stream<RegistryEntry<?>> registries();

   default Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
      return this.registries().map((var0) -> {
         return var0.key;
      });
   }

   static Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> var0) {
      return new Frozen() {
         public <T> Optional<Registry<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            Registry var2 = var0;
            return var2.getOptional(var1);
         }

         public Stream<RegistryEntry<?>> registries() {
            return var0.entrySet().stream().map(RegistryEntry::fromMapEntry);
         }

         public Frozen freeze() {
            return this;
         }
      };
   }

   default Frozen freeze() {
      class 1FrozenAccess extends ImmutableRegistryAccess implements Frozen {
         protected _FrozenAccess/* $FF was: 1FrozenAccess*/(final RegistryAccess var1, final Stream var2) {
            super(var2);
         }
      }

      return new 1FrozenAccess(this, this.registries().map(RegistryEntry::freeze));
   }

   // $FF: synthetic method
   default HolderLookup.RegistryLookup lookupOrThrow(final ResourceKey var1) {
      return this.lookupOrThrow(var1);
   }

   // $FF: synthetic method
   default HolderGetter lookupOrThrow(final ResourceKey var1) {
      return this.lookupOrThrow(var1);
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
      final ResourceKey<? extends Registry<T>> key;

      public RegistryEntry(ResourceKey<? extends Registry<T>> var1, Registry<T> var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      private static <T, R extends Registry<? extends T>> RegistryEntry<T> fromMapEntry(Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> var0) {
         return fromUntyped((ResourceKey)var0.getKey(), (Registry)var0.getValue());
      }

      private static <T> RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> var0, Registry<?> var1) {
         return new RegistryEntry(var0, var1);
      }

      private RegistryEntry<T> freeze() {
         return new RegistryEntry(this.key, this.value.freeze());
      }

      public ResourceKey<? extends Registry<T>> key() {
         return this.key;
      }

      public Registry<T> value() {
         return this.value;
      }
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(List<? extends Registry<?>> var1) {
         super();
         this.registries = (Map)var1.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (var0) -> {
            return var0;
         }));
      }

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> var1) {
         super();
         this.registries = Map.copyOf(var1);
      }

      public ImmutableRegistryAccess(Stream<RegistryEntry<?>> var1) {
         super();
         this.registries = (Map)var1.collect(ImmutableMap.toImmutableMap(RegistryEntry::key, RegistryEntry::value));
      }

      public <E> Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable((Registry)this.registries.get(var1)).map((var0) -> {
            return var0;
         });
      }

      public Stream<RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
      }
   }

   public interface Frozen extends RegistryAccess {
   }
}
