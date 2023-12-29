package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

   default HolderLookup<T> filterElements(final Predicate<T> var1) {
      return new HolderLookup.Delegate<T>(this) {
         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1x) {
            return this.parent.get(var1x).filter(var1xxx -> var1.test(var1xxx.value()));
         }

         @Override
         public Stream<Holder.Reference<T>> listElements() {
            return this.parent.listElements().filter(var1xx -> var1.test(var1xx.value()));
         }
      };
   }

   public static class Delegate<T> implements HolderLookup<T> {
      protected final HolderLookup<T> parent;

      public Delegate(HolderLookup<T> var1) {
         super();
         this.parent = var1;
      }

      @Override
      public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
         return this.parent.get(var1);
      }

      @Override
      public Stream<Holder.Reference<T>> listElements() {
         return this.parent.listElements();
      }

      @Override
      public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
         return this.parent.get(var1);
      }

      @Override
      public Stream<HolderSet.Named<T>> listTags() {
         return this.parent.listTags();
      }
   }

   public interface Provider {
      Stream<ResourceKey<? extends Registry<?>>> listRegistries();

      <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

      default <T> HolderLookup.RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> var1) {
         return this.<T>lookup(var1).orElseThrow(() -> new IllegalStateException("Registry " + var1.location() + " not found"));
      }

      default HolderGetter.Provider asGetterLookup() {
         return new HolderGetter.Provider() {
            @Override
            public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
               return Provider.this.lookup(var1).map(var0 -> var0);
            }
         };
      }

      static HolderLookup.Provider create(Stream<HolderLookup.RegistryLookup<?>> var0) {
         final Map var1 = var0.collect(
            Collectors.toUnmodifiableMap(
               HolderLookup.RegistryLookup::key, (Function<? super HolderLookup.RegistryLookup, ? extends HolderLookup.RegistryLookup>)(var0x -> var0x)
            )
         );
         return new HolderLookup.Provider() {
            @Override
            public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
               return var1.keySet().stream();
            }

            @Override
            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1x) {
               return Optional.ofNullable((HolderLookup.RegistryLookup<T>)var1.get(var1x));
            }
         };
      }
   }

   public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      Lifecycle registryLifecycle();

      default HolderLookup<T> filterFeatures(FeatureFlagSet var1) {
         return (HolderLookup<T>)(FeatureElement.FILTERED_REGISTRIES.contains(this.key())
            ? this.filterElements(var1x -> ((FeatureElement)var1x).isEnabled(var1))
            : this);
      }

      public abstract static class Delegate<T> implements HolderLookup.RegistryLookup<T> {
         public Delegate() {
            super();
         }

         protected abstract HolderLookup.RegistryLookup<T> parent();

         @Override
         public ResourceKey<? extends Registry<? extends T>> key() {
            return this.parent().key();
         }

         @Override
         public Lifecycle registryLifecycle() {
            return this.parent().registryLifecycle();
         }

         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return this.parent().get(var1);
         }

         @Override
         public Stream<Holder.Reference<T>> listElements() {
            return this.parent().listElements();
         }

         @Override
         public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
            return this.parent().get(var1);
         }

         @Override
         public Stream<HolderSet.Named<T>> listTags() {
            return this.parent().listTags();
         }
      }
   }
}
