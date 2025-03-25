package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public record ProvidesTrimMaterial(EitherHolder<TrimMaterial> material) {
   public static final Codec<ProvidesTrimMaterial> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ProvidesTrimMaterial> STREAM_CODEC;

   public ProvidesTrimMaterial(Holder<TrimMaterial> var1) {
      this(new EitherHolder(var1));
   }

   /** @deprecated */
   @Deprecated
   public ProvidesTrimMaterial(ResourceKey<TrimMaterial> var1) {
      this(new EitherHolder(var1));
   }

   public ProvidesTrimMaterial(EitherHolder<TrimMaterial> var1) {
      super();
      this.material = var1;
   }

   public Optional<Holder<TrimMaterial>> unwrap(HolderLookup.Provider var1) {
      return this.material.unwrap(var1);
   }

   static {
      CODEC = EitherHolder.codec(Registries.TRIM_MATERIAL, TrimMaterial.CODEC).xmap(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
      STREAM_CODEC = EitherHolder.streamCodec(Registries.TRIM_MATERIAL, TrimMaterial.STREAM_CODEC).map(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
   }
}
