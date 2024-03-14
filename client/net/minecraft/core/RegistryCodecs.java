package net.minecraft.core;

import com.mojang.serialization.Codec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   public RegistryCodecs() {
      super();
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
}
