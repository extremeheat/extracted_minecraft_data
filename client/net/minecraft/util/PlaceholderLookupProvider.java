package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class PlaceholderLookupProvider implements HolderGetter.Provider {
   final HolderLookup.Provider context;
   final UniversalLookup lookup = new UniversalLookup();
   final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();
   final Map<TagKey<Object>, HolderSet.Named<Object>> holderSets = new HashMap();

   public PlaceholderLookupProvider(HolderLookup.Provider var1) {
      super();
      this.context = var1;
   }

   public <T> Optional<? extends HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
      return Optional.of(this.lookup.castAsLookup());
   }

   public <V> RegistryOps<V> createSerializationContext(DynamicOps<V> var1) {
      return RegistryOps.create(var1, new RegistryOps.RegistryInfoLookup() {
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            return PlaceholderLookupProvider.this.context.lookup(var1).map(RegistryOps.RegistryInfo::fromRegistryLookup).or(() -> Optional.of(new RegistryOps.RegistryInfo(PlaceholderLookupProvider.this.lookup.castAsOwner(), PlaceholderLookupProvider.this.lookup.castAsLookup(), Lifecycle.experimental())));
         }
      });
   }

   public RegistryContextSwapper createSwapper() {
      return new RegistryContextSwapper() {
         public <T> DataResult<T> swapTo(Codec<T> var1, T var2, HolderLookup.Provider var3) {
            return var1.encodeStart(PlaceholderLookupProvider.this.createSerializationContext(JavaOps.INSTANCE), var2).flatMap((var2x) -> var1.parse(var3.createSerializationContext(JavaOps.INSTANCE), var2x));
         }
      };
   }

   public boolean hasRegisteredPlaceholders() {
      return !this.holders.isEmpty() || !this.holderSets.isEmpty();
   }

   class UniversalLookup implements HolderGetter<Object>, HolderOwner<Object> {
      UniversalLookup() {
         super();
      }

      public Optional<Holder.Reference<Object>> get(ResourceKey<Object> var1) {
         return Optional.of(this.getOrCreate(var1));
      }

      public Holder.Reference<Object> getOrThrow(ResourceKey<Object> var1) {
         return this.getOrCreate(var1);
      }

      private Holder.Reference<Object> getOrCreate(ResourceKey<Object> var1) {
         return (Holder.Reference)PlaceholderLookupProvider.this.holders.computeIfAbsent(var1, (var1x) -> Holder.Reference.createStandAlone(this, var1x));
      }

      public Optional<HolderSet.Named<Object>> get(TagKey<Object> var1) {
         return Optional.of(this.getOrCreate(var1));
      }

      public HolderSet.Named<Object> getOrThrow(TagKey<Object> var1) {
         return this.getOrCreate(var1);
      }

      private HolderSet.Named<Object> getOrCreate(TagKey<Object> var1) {
         return (HolderSet.Named)PlaceholderLookupProvider.this.holderSets.computeIfAbsent(var1, (var1x) -> HolderSet.emptyNamed(this, var1x));
      }

      public <T> HolderGetter<T> castAsLookup() {
         return this;
      }

      public <T> HolderOwner<T> castAsOwner() {
         return this;
      }
   }
}
