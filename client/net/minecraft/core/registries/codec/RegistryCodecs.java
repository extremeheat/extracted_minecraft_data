package net.minecraft.core.registries.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   public RegistryCodecs() {
      super();
   }

   public static <E> Codec<Holder<E>> holder(final ResourceKey<? extends Registry<E>> registryKey) {
      return RegistryFixedCodec.create(registryKey);
   }

   public static <E> Codec<Holder<E>> holder(final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec) {
      return holder(registryKey, elementCodec, true);
   }

   public static <E> Codec<Holder<E>> holder(final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec, final boolean allowInline) {
      return RegistryFileCodec.create(registryKey, elementCodec, allowInline);
   }

   public static <E> Codec<HolderSet<E>> holderSet(final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec) {
      return holderSet(registryKey, elementCodec, false);
   }

   public static <E> Codec<HolderSet<E>> holderSet(final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec, final boolean alwaysUseList) {
      return HolderSetCodec.create(registryKey, holder(registryKey, elementCodec), alwaysUseList);
   }

   public static <E> Codec<HolderSet<E>> holderSet(final ResourceKey<? extends Registry<E>> registryKey) {
      return holderSet(registryKey, false);
   }

   public static <E> Codec<HolderSet<E>> holderSet(final ResourceKey<? extends Registry<E>> registryKey, final boolean alwaysUseList) {
      return HolderSetCodec.create(registryKey, holder(registryKey), alwaysUseList);
   }
}
