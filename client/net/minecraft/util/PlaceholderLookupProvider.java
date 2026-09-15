package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class PlaceholderLookupProvider implements HolderGetter.Provider {
   private final HolderLookup.Provider context;
   private final UniversalLookup lookup = new UniversalLookup();
   private final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();
   private final Map<TagKey<Object>, HolderSet.Named<Object>> holderSets = new HashMap();

   public PlaceholderLookupProvider(final HolderLookup.Provider context) {
      super();
      this.context = context;
   }

   public <T> Optional<? extends HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
      return Optional.of(this.lookup.castAsLookup());
   }

   public <V> RegistryOps<V> createSerializationContext(final DynamicOps<V> parent) {
      return RegistryOps.create(parent, new RegistryOps.RegistryInfoLookup() {
         {
            Objects.requireNonNull(PlaceholderLookupProvider.this);
         }

         public <T> Optional<HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> registryKey) {
            Optional<HolderGetter<T>> result = PlaceholderLookupProvider.this.context.lookup(registryKey).map((e) -> e);
            return result.or(() -> Optional.of(PlaceholderLookupProvider.this.lookup.castAsLookup()));
         }
      });
   }

   public RegistryContextSwapper createSwapper() {
      return new RegistryContextSwapper() {
         {
            Objects.requireNonNull(PlaceholderLookupProvider.this);
         }

         public <T> DataResult<T> swapTo(final Codec<T> codec, final T value, final HolderLookup.Provider newContext) {
            return codec.encodeStart(PlaceholderLookupProvider.this.createSerializationContext(JavaOps.INSTANCE), value).flatMap((v) -> codec.parse(newContext.createSerializationContext(JavaOps.INSTANCE), v));
         }
      };
   }

   public boolean hasRegisteredPlaceholders() {
      return !this.holders.isEmpty() || !this.holderSets.isEmpty();
   }

   private class UniversalLookup implements HolderGetter<Object> {
      private UniversalLookup() {
         Objects.requireNonNull(PlaceholderLookupProvider.this);
         super();
      }

      public Optional<Holder.Reference<Object>> get(final ResourceKey<Object> id) {
         return Optional.of(this.getOrCreate(id));
      }

      public Holder.Reference<Object> getOrThrow(final ResourceKey<Object> id) {
         return this.getOrCreate(id);
      }

      private Holder.Reference<Object> getOrCreate(final ResourceKey<Object> id) {
         return (Holder.Reference)PlaceholderLookupProvider.this.holders.computeIfAbsent(id, (k) -> Holder.Reference.createStandAlone(this, k));
      }

      public Optional<HolderSet.Named<Object>> get(final TagKey<Object> id) {
         return Optional.of(this.getOrCreate(id));
      }

      public HolderSet.Named<Object> getOrThrow(final TagKey<Object> id) {
         return this.getOrCreate(id);
      }

      private HolderSet.Named<Object> getOrCreate(final TagKey<Object> id) {
         return (HolderSet.Named)PlaceholderLookupProvider.this.holderSets.computeIfAbsent(id, (k) -> HolderSet.emptyNamed(this, k));
      }

      public <T> HolderGetter<T> castAsLookup() {
         return this;
      }
   }
}
