package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
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

   public interface Provider {
      Stream<ResourceKey<? extends Registry<?>>> listRegistries();

      <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

      default <T> RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> var1) {
         return (RegistryLookup)this.lookup(var1).orElseThrow(() -> {
            return new IllegalStateException("Registry " + String.valueOf(var1.location()) + " not found");
         });
      }

      default <V> RegistryOps<V> createSerializationContext(DynamicOps<V> var1) {
         return RegistryOps.create(var1, this);
      }

      default HolderGetter.Provider asGetterLookup() {
         return new HolderGetter.Provider() {
            public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
               return Provider.this.lookup(var1).map((var0) -> {
                  return var0;
               });
            }
         };
      }

      static Provider create(Stream<RegistryLookup<?>> var0) {
         final Map var1 = (Map)var0.collect(Collectors.toUnmodifiableMap(RegistryLookup::key, (var0x) -> {
            return var0x;
         }));
         return new Provider() {
            public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
               return var1.keySet().stream();
            }

            public <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1x) {
               return Optional.ofNullable((RegistryLookup)var1.get(var1x));
            }
         };
      }
   }

   public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      Lifecycle registryLifecycle();

      default RegistryLookup<T> filterFeatures(FeatureFlagSet var1) {
         return FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements((var1x) -> {
            return ((FeatureElement)var1x).isEnabled(var1);
         }) : this;
      }

      default RegistryLookup<T> filterElements(final Predicate<T> var1) {
         return new Delegate<T>() {
            public RegistryLookup<T> parent() {
               return RegistryLookup.this;
            }

            public Optional<Holder.Reference<T>> get(ResourceKey<T> var1x) {
               return this.parent().get(var1x).filter((var1xx) -> {
                  return var1.test(var1xx.value());
               });
            }

            public Stream<Holder.Reference<T>> listElements() {
               return this.parent().listElements().filter((var1x) -> {
                  return var1.test(var1x.value());
               });
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

         default Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return this.parent().get(var1);
         }

         default Stream<Holder.Reference<T>> listElements() {
            return this.parent().listElements();
         }

         default Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
            return this.parent().get(var1);
         }

         default Stream<HolderSet.Named<T>> listTags() {
            return this.parent().listTags();
         }
      }
   }
}
