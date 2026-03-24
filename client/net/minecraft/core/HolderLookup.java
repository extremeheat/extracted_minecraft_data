package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {
   Stream<Holder.Reference<T>> listElements();

   default Stream<ResourceKey<T>> listElementIds() {
      return this.listElements().map(Holder.Reference::key);
   }

   Stream<HolderSet.Named<T>> listTags();

   default Stream<TagKey<T>> listTagIds() {
      return this.listTags().map(HolderSet.Named::key);
   }

   public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      Lifecycle registryLifecycle();

      default RegistryLookup<T> filterFeatures(final FeatureFlagSet enabledFeatures) {
         return FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements((t) -> ((FeatureElement)t).isEnabled(enabledFeatures)) : this;
      }

      default RegistryLookup<T> filterElements(final Predicate<T> filter) {
         return new Delegate<T>() {
            {
               Objects.requireNonNull(RegistryLookup.this);
            }

            public RegistryLookup<T> parent() {
               return RegistryLookup.this;
            }

            public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
               return this.parent().get(id).filter((holder) -> filter.test(holder.value()));
            }

            public Stream<Holder.Reference<T>> listElements() {
               return this.parent().listElements().filter((e) -> filter.test(e.value()));
            }
         };
      }

      public interface Delegate<T> extends RegistryLookup<T> {
         RegistryLookup<T> parent();

         default ResourceKey<? extends Registry<? extends T>> key() {
            return this.parent().key();
         }

         default Lifecycle registryLifecycle() {
            return this.parent().registryLifecycle();
         }

         default Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
            return this.parent().get(id);
         }

         default Stream<Holder.Reference<T>> listElements() {
            return this.parent().listElements();
         }

         default Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
            return this.parent().get(id);
         }

         default Stream<HolderSet.Named<T>> listTags() {
            return this.parent().listTags();
         }
      }
   }

   public interface Provider extends HolderGetter.Provider {
      Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys();

      default Stream<RegistryLookup<?>> listRegistries() {
         return this.listRegistryKeys().map(this::lookupOrThrow);
      }

      <T> Optional<? extends RegistryLookup<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key);

      default <T> RegistryLookup<T> lookupOrThrow(final ResourceKey<? extends Registry<? extends T>> key) {
         return (RegistryLookup)this.lookup(key).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(key.identifier()) + " not found"));
      }

      default <V> RegistryOps<V> createSerializationContext(final DynamicOps<V> parent) {
         return RegistryOps.create(parent, this);
      }

      static Provider create(final Stream<RegistryLookup<?>> lookups) {
         final Map<ResourceKey<? extends Registry<?>>, RegistryLookup<?>> map = (Map)lookups.collect(Collectors.toUnmodifiableMap(RegistryLookup::key, (e) -> e));
         return new Provider() {
            public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
               return map.keySet().stream();
            }

            public <T> Optional<RegistryLookup<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
               return Optional.ofNullable((RegistryLookup)map.get(key));
            }
         };
      }

      default Lifecycle allRegistriesLifecycle() {
         return (Lifecycle)this.listRegistries().map(RegistryLookup::registryLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
      }
   }
}
