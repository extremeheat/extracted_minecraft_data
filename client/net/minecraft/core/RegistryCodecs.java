package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   public RegistryCodecs() {
      super();
   }

   private static <T> MapCodec<RegistryCodecs.RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> var0, MapCodec<T> var1) {
      return RecordCodecBuilder.mapCodec(
         var2 -> var2.group(
                  ResourceKey.codec(var0).fieldOf("name").forGetter(RegistryCodecs.RegistryEntry::key),
                  Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryEntry::id),
                  var1.forGetter(RegistryCodecs.RegistryEntry::value)
               )
               .apply(var2, RegistryCodecs.RegistryEntry::new)
      );
   }

   public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Codec<T> var2) {
      return withNameAndId(var0, var2.fieldOf("element")).codec().listOf().xmap(var2x -> {
         MappedRegistry var3 = new MappedRegistry(var0, var1);

         for(RegistryCodecs.RegistryEntry var5 : var2x) {
            var3.registerMapping(var5.id(), var5.key(), var5.value(), var1);
         }

         return var3;
      }, var0x -> {
         Builder var1x = ImmutableList.builder();

         for(Object var3 : var0x) {
            var1x.add(new RegistryCodecs.RegistryEntry<>(var0x.getResourceKey(var3).get(), var0x.getId(var3), var3));
         }

         return var1x.build();
      });
   }

   public static <E> Codec<Registry<E>> fullCodec(ResourceKey<? extends Registry<E>> var0, Lifecycle var1, Codec<E> var2) {
      UnboundedMapCodec var3 = Codec.unboundedMap(ResourceKey.codec(var0), var2);
      return var3.xmap(var2x -> {
         MappedRegistry var3x = new MappedRegistry(var0, var1);
         var2x.forEach((var2xx, var3xx) -> var3x.register(var2xx, var3xx, var1));
         return var3x.freeze();
      }, var0x -> ImmutableMap.copyOf(var0x.entrySet()));
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
         this.value = (T)var3;
      }
   }
}
