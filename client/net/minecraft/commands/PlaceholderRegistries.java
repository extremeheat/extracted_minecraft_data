package net.minecraft.commands;

import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class PlaceholderRegistries implements HolderGetter.Provider {
   private final Map<ResourceKey<? extends Registry<?>>, HolderGetter<?>> registries;

   private PlaceholderRegistries(final Map<ResourceKey<? extends Registry<?>>, HolderGetter<?>> registries) {
      super();
      this.registries = registries;
   }

   public static HolderGetter.Provider create(final Stream<ResourceKey<? extends Registry<?>>> keys, final PlaceholderBehavior behavior) {
      return new PlaceholderRegistries((Map)keys.collect(Collectors.toUnmodifiableMap((k) -> k, (var1) -> createPlaceholder(behavior))));
   }

   private static <T> HolderGetter<T> createPlaceholder(final PlaceholderBehavior behavior) {
      Object var10000;
      switch (behavior.ordinal()) {
         case 0 -> var10000 = new EmptyRegistry();
         case 1 -> var10000 = new PlaceholderRegistry();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (HolderGetter<T>)var10000;
   }

   public <T> Optional<? extends HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
      return Optional.ofNullable((HolderGetter)this.registries.get(key));
   }

   private static class EmptyRegistry<T> implements HolderGetter<T> {
      private EmptyRegistry() {
         super();
      }

      public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
         return Optional.empty();
      }

      public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
         return Optional.empty();
      }
   }

   public static enum PlaceholderBehavior {
      REJECT_ALL,
      ACCEPT_ALL;

      private PlaceholderBehavior() {
      }

      // $FF: synthetic method
      private static PlaceholderBehavior[] $values() {
         return new PlaceholderBehavior[]{REJECT_ALL, ACCEPT_ALL};
      }
   }

   private static class PlaceholderRegistry<T> implements HolderGetter<T> {
      private final Map<ResourceKey<T>, Holder.Reference<T>> holders = (new MapMaker()).weakValues().makeMap();
      private final Map<TagKey<T>, HolderSet.Named<T>> holderSets = (new MapMaker()).weakValues().makeMap();

      private PlaceholderRegistry() {
         super();
      }

      public Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
         return (Holder.Reference)this.holders.computeIfAbsent(id, (k) -> new PlaceholderElement(this, k));
      }

      public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
         return Optional.of(this.getOrThrow(id));
      }

      public HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
         return (HolderSet.Named)this.holderSets.computeIfAbsent(id, (k) -> new PlaceholderTag(this, k));
      }

      public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
         return Optional.of(this.getOrThrow(id));
      }
   }

   private static class PlaceholderElement<T> extends Holder.Reference<T> {
      protected PlaceholderElement(final HolderOwner<T> owner, final ResourceKey<T> key) {
         super(Holder.Reference.Type.STAND_ALONE, owner, key, (Object)null);
      }

      public T value() {
         throw new UnsupportedOperationException("Trying to deference placeholder element " + String.valueOf(this.key()));
      }
   }

   private static class PlaceholderTag<T> extends HolderSet.Named<T> {
      public PlaceholderTag(final HolderOwner<T> owner, final TagKey<T> key) {
         super(owner, key);
      }

      protected List<Holder<T>> contents() {
         throw new UnsupportedOperationException("Trying to deference placeholder tag " + String.valueOf(this.key()));
      }
   }
}
