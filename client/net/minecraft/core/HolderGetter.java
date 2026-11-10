package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

public interface HolderGetter<T> extends HolderOwner<T> {
   Optional<Holder.Reference<T>> get(final ResourceKey<T> id);

   default Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
      return (Holder.Reference)this.get(id).orElseThrow(() -> new IllegalStateException("Missing element " + String.valueOf(id)));
   }

   Optional<HolderSet.Named<T>> get(final TagKey<T> id);

   default HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
      return (HolderSet.Named)this.get(id).orElseThrow(() -> new IllegalStateException("Missing tag " + String.valueOf(id)));
   }

   default Optional<Holder<T>> getRandomElementOf(final TagKey<T> tag, final RandomSource random) {
      return this.get(tag).flatMap((holderSet) -> holderSet.getRandomElement(random));
   }

   default Iterable<Holder<T>> getTagOrEmpty(final TagKey<T> id) {
      return (Iterable)DataFixUtils.orElse(this.get(id), List.of());
   }

   public interface Provider {
      <T> Optional<? extends HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key);

      default <T> HolderGetter<T> lookupOrThrow(final ResourceKey<? extends Registry<? extends T>> key) {
         return (HolderGetter)this.lookup(key).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(key.identifier()) + " not found"));
      }

      default <T> Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
         return this.lookup(id.registryKey()).flatMap((l) -> l.get(id));
      }

      default <T> Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
         return (Holder.Reference)this.lookup(id.registryKey()).flatMap((l) -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing element " + String.valueOf(id)));
      }

      default <T> Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
         return this.lookup(id.registry()).flatMap((l) -> l.get(id));
      }

      default <T> HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
         return (HolderSet.Named)this.lookup(id.registry()).flatMap((l) -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing tag " + String.valueOf(id)));
      }

      default <V> RegistryOps<V> createSerializationContext(final DynamicOps<V> parent) {
         return RegistryOps.<V>create(parent, this);
      }

      default Provider withFallback(final Provider fallback) {
         return new Provider() {
            {
               Objects.requireNonNull(Provider.this);
            }

            public <T> Optional<? extends HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
               Optional<? extends HolderGetter<T>> primary = Provider.this.lookup(key);
               return primary.isPresent() ? primary : fallback.lookup(key);
            }
         };
      }
   }
}
