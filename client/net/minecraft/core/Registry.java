package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T> extends Keyable, IdMap<T> {
   ResourceKey<? extends Registry<T>> key();

   default Codec<T> byNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap(Holder.Reference::value, (var1) -> {
         return this.safeCastToReference(this.wrapAsHolder(var1));
      });
   }

   default Codec<Holder<T>> holderByNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap((var0) -> {
         return var0;
      }, this::safeCastToReference);
   }

   private Codec<Holder.Reference<T>> referenceHolderWithLifecycle() {
      Codec var1 = ResourceLocation.CODEC.comapFlatMap((var1x) -> {
         return (DataResult)this.getHolder(var1x).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               String var10000 = String.valueOf(this.key());
               return "Unknown registry key in " + var10000 + ": " + String.valueOf(var1x);
            });
         });
      }, (var0) -> {
         return var0.key().location();
      });
      return ExtraCodecs.overrideLifecycle(var1, (var1x) -> {
         return (Lifecycle)this.registrationInfo(var1x.key()).map(RegistrationInfo::lifecycle).orElse(Lifecycle.experimental());
      });
   }

   private DataResult<Holder.Reference<T>> safeCastToReference(Holder<T> var1) {
      DataResult var10000;
      if (var1 instanceof Holder.Reference var2) {
         var10000 = DataResult.success(var2);
      } else {
         var10000 = DataResult.error(() -> {
            String var10000 = String.valueOf(this.key());
            return "Unregistered holder in " + var10000 + ": " + String.valueOf(var1);
         });
      }

      return var10000;
   }

   default <U> Stream<U> keys(DynamicOps<U> var1) {
      return this.keySet().stream().map((var1x) -> {
         return var1.createString(var1x.toString());
      });
   }

   @Nullable
   ResourceLocation getKey(T var1);

   Optional<ResourceKey<T>> getResourceKey(T var1);

   int getId(@Nullable T var1);

   @Nullable
   T get(@Nullable ResourceKey<T> var1);

   @Nullable
   T get(@Nullable ResourceLocation var1);

   Optional<RegistrationInfo> registrationInfo(ResourceKey<T> var1);

   Lifecycle registryLifecycle();

   default Optional<T> getOptional(@Nullable ResourceLocation var1) {
      return Optional.ofNullable(this.get(var1));
   }

   default Optional<T> getOptional(@Nullable ResourceKey<T> var1) {
      return Optional.ofNullable(this.get(var1));
   }

   default T getOrThrow(ResourceKey<T> var1) {
      Object var2 = this.get(var1);
      if (var2 == null) {
         String var10002 = String.valueOf(this.key());
         throw new IllegalStateException("Missing key in " + var10002 + ": " + String.valueOf(var1));
      } else {
         return var2;
      }
   }

   Set<ResourceLocation> keySet();

   Set<Map.Entry<ResourceKey<T>, T>> entrySet();

   Set<ResourceKey<T>> registryKeySet();

   Optional<Holder.Reference<T>> getRandom(RandomSource var1);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsKey(ResourceLocation var1);

   boolean containsKey(ResourceKey<T> var1);

   static <T> T register(Registry<? super T> var0, String var1, T var2) {
      return register(var0, new ResourceLocation(var1), var2);
   }

   static <V, T extends V> T register(Registry<V> var0, ResourceLocation var1, T var2) {
      return register(var0, ResourceKey.create(var0.key(), var1), var2);
   }

   static <V, T extends V> T register(Registry<V> var0, ResourceKey<V> var1, T var2) {
      ((WritableRegistry)var0).register(var1, var2, RegistrationInfo.BUILT_IN);
      return var2;
   }

   static <T> Holder.Reference<T> registerForHolder(Registry<T> var0, ResourceKey<T> var1, T var2) {
      return ((WritableRegistry)var0).register(var1, var2, RegistrationInfo.BUILT_IN);
   }

   static <T> Holder.Reference<T> registerForHolder(Registry<T> var0, ResourceLocation var1, T var2) {
      return registerForHolder(var0, ResourceKey.create(var0.key(), var1), var2);
   }

   Registry<T> freeze();

   Holder.Reference<T> createIntrusiveHolder(T var1);

   Optional<Holder.Reference<T>> getHolder(int var1);

   Optional<Holder.Reference<T>> getHolder(ResourceLocation var1);

   Optional<Holder.Reference<T>> getHolder(ResourceKey<T> var1);

   Holder<T> wrapAsHolder(T var1);

   default Holder.Reference<T> getHolderOrThrow(ResourceKey<T> var1) {
      return (Holder.Reference)this.getHolder(var1).orElseThrow(() -> {
         String var10002 = String.valueOf(this.key());
         return new IllegalStateException("Missing key in " + var10002 + ": " + String.valueOf(var1));
      });
   }

   Stream<Holder.Reference<T>> holders();

   Optional<HolderSet.Named<T>> getTag(TagKey<T> var1);

   default Iterable<Holder<T>> getTagOrEmpty(TagKey<T> var1) {
      return (Iterable)DataFixUtils.orElse(this.getTag(var1), List.of());
   }

   default Optional<Holder<T>> getRandomElementOf(TagKey<T> var1, RandomSource var2) {
      return this.getTag(var1).flatMap((var1x) -> {
         return var1x.getRandomElement(var2);
      });
   }

   HolderSet.Named<T> getOrCreateTag(TagKey<T> var1);

   Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

   Stream<TagKey<T>> getTagNames();

   void resetTags();

   void bindTags(Map<TagKey<T>, List<Holder<T>>> var1);

   default IdMap<Holder<T>> asHolderIdMap() {
      return new IdMap<Holder<T>>() {
         public int getId(Holder<T> var1) {
            return Registry.this.getId(var1.value());
         }

         @Nullable
         public Holder<T> byId(int var1) {
            return (Holder)Registry.this.getHolder(var1).orElse((Object)null);
         }

         public int size() {
            return Registry.this.size();
         }

         public Iterator<Holder<T>> iterator() {
            return Registry.this.holders().map((var0) -> {
               return var0;
            }).iterator();
         }

         // $FF: synthetic method
         @Nullable
         public Object byId(int var1) {
            return this.byId(var1);
         }
      };
   }

   HolderOwner<T> holderOwner();

   HolderLookup.RegistryLookup<T> asLookup();

   default HolderLookup.RegistryLookup<T> asTagAddingLookup() {
      return new HolderLookup.RegistryLookup.Delegate<T>() {
         public HolderLookup.RegistryLookup<T> parent() {
            return Registry.this.asLookup();
         }

         public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
            return Optional.of(this.getOrThrow(var1));
         }

         public HolderSet.Named<T> getOrThrow(TagKey<T> var1) {
            return Registry.this.getOrCreateTag(var1);
         }
      };
   }
}
