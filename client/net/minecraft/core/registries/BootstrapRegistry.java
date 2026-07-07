package net.minecraft.core.registries;

import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

public class BootstrapRegistry<T> implements HolderLookup.RegistryLookup<T> {
   private final ResourceKey<? extends Registry<T>> key;
   private final Lifecycle lifecycle;
   private Storage<T> storage = new RegistrationStorage();

   public BootstrapRegistry(final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle) {
      super();
      this.key = key;
      this.lifecycle = lifecycle;
   }

   public ResourceKey<? extends Registry<? extends T>> key() {
      return this.key;
   }

   public Lifecycle registryLifecycle() {
      return this.lifecycle;
   }

   public void freeze() {
      this.storage = this.storage.freeze();
   }

   public void removeIf(final Predicate<Holder.Reference<T>> predicate) {
      this.storage.removeIf(predicate);
   }

   public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
      return Optional.ofNullable(this.storage.get(id));
   }

   public Stream<Holder.Reference<T>> listElements() {
      return this.storage.listElements().stream();
   }

   public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
      return Optional.ofNullable(this.storage.get(id));
   }

   public Stream<HolderSet.Named<T>> listTags() {
      return this.storage.listTags().stream();
   }

   private class RegistrationStorage implements Storage<T> {
      private final Map<ResourceKey<T>, Holder.Reference<T>> holders;
      private final Map<TagKey<T>, HolderSet.Named<T>> holderSets;

      private RegistrationStorage() {
         Objects.requireNonNull(BootstrapRegistry.this);
         super();
         this.holders = new HashMap();
         this.holderSets = new HashMap();
      }

      public Storage<T> freeze() {
         return new FrozenStorage<T>(this.holders, this.holderSets);
      }

      public void removeIf(final Predicate<Holder.Reference<T>> predicate) {
         this.holders.values().removeIf(predicate);
      }

      public Holder.Reference<T> get(final ResourceKey<T> id) {
         return (Holder.Reference)this.holders.computeIfAbsent(id, (key) -> Holder.Reference.createStandAlone(BootstrapRegistry.this, key));
      }

      public Collection<Holder.Reference<T>> listElements() {
         throw new UnsupportedOperationException("List is not available during bootstrap");
      }

      public HolderSet.Named<T> get(final TagKey<T> id) {
         return (HolderSet.Named)this.holderSets.computeIfAbsent(id, (key) -> HolderSet.emptyNamed(BootstrapRegistry.this, key));
      }

      public Collection<HolderSet.Named<T>> listTags() {
         throw new UnsupportedOperationException("List is not available during bootstrap");
      }
   }

   private static class FrozenStorage<T> implements Storage<T> {
      private final Map<ResourceKey<T>, Holder.Reference<T>> holders;
      private final Map<TagKey<T>, HolderSet.Named<T>> holderSets;

      private FrozenStorage(final Map<ResourceKey<T>, Holder.Reference<T>> holders, final Map<TagKey<T>, HolderSet.Named<T>> holderSets) {
         super();
         this.holders = Map.copyOf(holders);
         this.holderSets = Map.copyOf(holderSets);
      }

      public Storage<T> freeze() {
         return this;
      }

      public void removeIf(final Predicate<Holder.Reference<T>> predicate) {
         throw new UnsupportedOperationException("Registry is already frozen");
      }

      public Holder.@Nullable Reference<T> get(final ResourceKey<T> id) {
         return (Holder.Reference)this.holders.get(id);
      }

      public Collection<Holder.Reference<T>> listElements() {
         return this.holders.values();
      }

      public HolderSet.@Nullable Named<T> get(final TagKey<T> id) {
         return (HolderSet.Named)this.holderSets.get(id);
      }

      public Collection<HolderSet.Named<T>> listTags() {
         return this.holderSets.values();
      }
   }

   private interface Storage<T> {
      Storage<T> freeze();

      void removeIf(Predicate<Holder.Reference<T>> predicate);

      Holder.@Nullable Reference<T> get(ResourceKey<T> id);

      Collection<Holder.Reference<T>> listElements();

      HolderSet.@Nullable Named<T> get(TagKey<T> id);

      Collection<HolderSet.Named<T>> listTags();
   }
}
