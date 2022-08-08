package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   public RegistryCodecs() {
      super();
   }

   private static <T> MapCodec<RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> var0, MapCodec<T> var1) {
      return RecordCodecBuilder.mapCodec((var2) -> {
         return var2.group(ResourceKey.codec(var0).fieldOf("name").forGetter(RegistryEntry::key), Codec.INT.fieldOf("id").forGetter(RegistryEntry::id), var1.forGetter(RegistryEntry::value)).apply(var2, RegistryEntry::new);
      });
   }

   public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Codec<T> var2) {
      return withNameAndId(var0, var2.fieldOf("element")).codec().listOf().xmap((var2x) -> {
         MappedRegistry var3 = new MappedRegistry(var0, var1, (Function)null);
         Iterator var4 = var2x.iterator();

         while(var4.hasNext()) {
            RegistryEntry var5 = (RegistryEntry)var4.next();
            var3.registerMapping(var5.id(), var5.key(), var5.value(), var1);
         }

         return var3;
      }, (var0x) -> {
         ImmutableList.Builder var1 = ImmutableList.builder();
         Iterator var2 = var0x.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            var1.add(new RegistryEntry((ResourceKey)var0x.getResourceKey(var3).get(), var0x.getId(var3), var3));
         }

         return var1.build();
      });
   }

   public static <E> Codec<Registry<E>> dataPackAwareCodec(ResourceKey<? extends Registry<E>> var0, Lifecycle var1, Codec<E> var2) {
      Codec var3 = directCodec(var0, var2);
      Encoder var4 = var3.comap((var0x) -> {
         return ImmutableMap.copyOf(var0x.entrySet());
      });
      return Codec.of(var4, dataPackAwareDecoder(var0, var2, var3, var1), "DataPackRegistryCodec for " + var0);
   }

   private static <E> Decoder<Registry<E>> dataPackAwareDecoder(final ResourceKey<? extends Registry<E>> var0, final Codec<E> var1, Decoder<Map<ResourceKey<E>, E>> var2, Lifecycle var3) {
      final Decoder var4 = var2.map((var2x) -> {
         MappedRegistry var3x = new MappedRegistry(var0, var3, (Function)null);
         var2x.forEach((var2, var3xx) -> {
            var3x.register(var2, var3xx, var3);
         });
         return var3x;
      });
      return new Decoder<Registry<E>>() {
         public <T> DataResult<Pair<Registry<E>, T>> decode(DynamicOps<T> var1x, T var2) {
            DataResult var3 = var4.decode(var1x, var2);
            if (var1x instanceof RegistryOps var4x) {
               return (DataResult)var4x.registryLoader().map((var3x) -> {
                  return this.overrideFromResources(var3, var4x, var3x.loader());
               }).orElseGet(() -> {
                  return DataResult.error("Can't load registry with this ops");
               });
            } else {
               return var3.map((var0x) -> {
                  return var0x.mapFirst((var0xx) -> {
                     return var0xx;
                  });
               });
            }
         }

         private <T> DataResult<Pair<Registry<E>, T>> overrideFromResources(DataResult<Pair<WritableRegistry<E>, T>> var1x, RegistryOps<?> var2, RegistryLoader var3) {
            return var1x.flatMap((var4x) -> {
               return var3.overrideRegistryFromResources((WritableRegistry)var4x.getFirst(), var0, var1, var2.getAsJson()).map((var1x) -> {
                  return Pair.of(var1x, var4x.getSecond());
               });
            });
         }
      };
   }

   private static <T> Codec<Map<ResourceKey<T>, T>> directCodec(ResourceKey<? extends Registry<T>> var0, Codec<T> var1) {
      return Codec.unboundedMap(ResourceKey.codec(var0), var1);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> var0, Codec<E> var1) {
      return homogeneousList(var0, var1, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> var0, Codec<E> var1, boolean var2) {
      return HolderSetCodec.create(var0, RegistryFileCodec.create(var0, var1), var2);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> var0) {
      return homogeneousList(var0, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> var0, boolean var1) {
      return HolderSetCodec.create(var0, RegistryFixedCodec.create(var0), var1);
   }

   static record RegistryEntry<T>(ResourceKey<T> a, int b, T c) {
      private final ResourceKey<T> key;
      private final int id;
      private final T value;

      RegistryEntry(ResourceKey<T> var1, int var2, T var3) {
         super();
         this.key = var1;
         this.id = var2;
         this.value = var3;
      }

      public ResourceKey<T> key() {
         return this.key;
      }

      public int id() {
         return this.id;
      }

      public T value() {
         return this.value;
      }
   }
}
