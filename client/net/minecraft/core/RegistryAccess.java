package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   RegistryAccess.Frozen EMPTY = new RegistryAccess.ImmutableRegistryAccess(Map.of()).freeze();

   <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1);

   @Override
   default <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
      return this.registry(var1).map(Registry::asLookup);
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.<E>registry(var1).orElseThrow(() -> new IllegalStateException("Missing registry: " + var1));
   }

   Stream<RegistryAccess.RegistryEntry<?>> registries();

   @Override
   default Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
      return this.registries().map(RegistryAccess.RegistryEntry::key);
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> var0) {
      return new RegistryAccess.Frozen() {
         @Override
         public <T> Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> var1) {
            Registry var2 = var0;
            return var2.getOptional(var1);
         }

         @Override
         public Stream<RegistryAccess.RegistryEntry<?>> registries() {
            return var0.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }

         @Override
         public RegistryAccess.Frozen freeze() {
            return this;
         }
      };
   }

   default RegistryAccess.Frozen freeze() {
      class 1FrozenAccess extends RegistryAccess.ImmutableRegistryAccess implements RegistryAccess.Frozen {
         protected _FrozenAccess/* $QF was: 1FrozenAccess*/(Stream<RegistryAccess.RegistryEntry<?>> var2) {
            super(var2);
         }
      }

      return new 1FrozenAccess(this.registries().map(RegistryAccess.RegistryEntry::freeze));
   }

   default Lifecycle allRegistriesLifecycle() {
      return (Lifecycle)this.registries().map(var0 -> var0.value.registryLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
   }

   public interface Frozen extends RegistryAccess {
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(List<? extends Registry<?>> var1) {
         super();
         this.registries = var1.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (Function<? super Registry, ? extends Registry>)(var0 -> var0)));
      }

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> var1) {
         super();
         this.registries = Map.copyOf(var1);
      }

      public ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> var1) {
         super();
         this.registries = var1.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      @Override
      public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map((Function<? super Registry<?>, ? extends Registry<E>>)(var0 -> var0));
      }

      @Override
      public Stream<RegistryAccess.RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> a, Registry<T> b) {
      private final ResourceKey<? extends Registry<T>> key;
      final Registry<T> value;

      public RegistryEntry(ResourceKey<? extends Registry<T>> var1, Registry<T> var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      private static <T, R extends Registry<? extends T>> RegistryAccess.RegistryEntry<T> fromMapEntry(
         Entry<? extends ResourceKey<? extends Registry<?>>, R> var0
      ) {
         return fromUntyped((ResourceKey<? extends Registry<?>>)var0.getKey(), (Registry<?>)var0.getValue());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> var0, Registry<?> var1) {
         return new RegistryAccess.RegistryEntry<>(var0, var1);
      }

      private RegistryAccess.RegistryEntry<T> freeze() {
         return new RegistryAccess.RegistryEntry<>(this.key, this.value.freeze());
      }
   }
}
