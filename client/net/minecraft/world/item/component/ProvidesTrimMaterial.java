package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public record ProvidesTrimMaterial(Holder<TrimMaterial> material) {
   public static final Codec<ProvidesTrimMaterial> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ProvidesTrimMaterial> STREAM_CODEC;

   public ProvidesTrimMaterial {
      super();
   }

   static {
      CODEC = TrimMaterial.CODEC.xmap(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
      STREAM_CODEC = TrimMaterial.STREAM_CODEC.map(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
   }
}
